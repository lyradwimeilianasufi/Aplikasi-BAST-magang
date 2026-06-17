package com.example.aplikasibast.features.attendance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikasibast.core.constants.AppConstants
import com.example.aplikasibast.core.utils.DateUtils
import com.example.aplikasibast.features.attendance.domain.model.Kehadiran
import com.example.aplikasibast.features.attendance.domain.model.RiwayatItem
import com.example.aplikasibast.features.attendance.domain.usecase.*
import com.example.aplikasibast.features.permission.domain.usecase.GetPengajuanByStatusUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AttendanceViewModel(
    private val getAllKehadiranUseCase: GetAllKehadiranUseCase,
    private val getKehadiranByIdUseCase: GetKehadiranByIdUseCase,
    private val getKehadiranByTanggalUseCase: GetKehadiranByTanggalUseCase,
    private val insertKehadiranUseCase: InsertKehadiranUseCase,
    private val updateKehadiranUseCase: UpdateKehadiranUseCase,
    private val getPengajuanByStatusUseCase: GetPengajuanByStatusUseCase
) : ViewModel() {

    val workHours = "Reguler (09:00-17:00)"
    
    val allKehadiran: Flow<List<Kehadiran>> = getAllKehadiranUseCase()

    val combinedRiwayat: Flow<List<RiwayatItem>> = combine(
        getAllKehadiranUseCase(),
        getPengajuanByStatusUseCase(AppConstants.STATUS_DISETUJUI)
    ) { kehadiranList, izinList ->
        val items = mutableListOf<RiwayatItem>()

        kehadiranList.forEach { entity ->
            val formattedTanggal = DateUtils.formatToUi(entity.tanggal)
            when (entity.status) {
                "Hadir", "Telat" -> items.add(RiwayatItem.KehadiranData(
                    id = entity.id, rawDate = entity.tanggal, tanggal = formattedTanggal, status = entity.status,
                    jamMasuk = entity.jamMasuk, jamKeluar = entity.jamKeluar, totalJam = entity.totalJam
                ))
                "Izin" -> items.add(RiwayatItem.IzinData(
                    id = entity.id, rawDate = entity.tanggal, tanggal = formattedTanggal, jenisIzin = "Izin",
                    periode = "-", durasi = "-", status = "Izin"
                ))
                "Sakit" -> items.add(RiwayatItem.SakitData(
                    id = entity.id, rawDate = entity.tanggal, tanggal = formattedTanggal, status = "Sakit", periode = "-", durasi = "-"
                ))
                "Alpa" -> items.add(RiwayatItem.AlpaData(
                    id = entity.id, rawDate = entity.tanggal, tanggal = formattedTanggal, status = "Alpa"
                ))
                else -> items.add(RiwayatItem.LiburData(
                    id = entity.id, rawDate = entity.tanggal, tanggal = formattedTanggal, status = entity.status
                ))
            }
        }

        izinList.forEach { izin ->
            val dates = generateDatesInRange(izin.tanggalMulai, izin.tanggalSelesai)
            dates.forEach { date ->
                if (kehadiranList.none { it.tanggal == date }) {
                    val formattedTanggal = DateUtils.formatToUi(date)
                    val periodeStr = "${DateUtils.formatToUi(izin.tanggalMulai)} - ${DateUtils.formatToUi(izin.tanggalSelesai)}"
                    val durasiStr = "${DateUtils.calculateDays(izin.tanggalMulai, izin.tanggalSelesai)} Hari"
                    
                    if (izin.jenisIzin.equals("Sakit", true)) {
                        items.add(RiwayatItem.SakitData(izin.id, date, formattedTanggal, periodeStr, durasiStr))
                    } else {
                        items.add(RiwayatItem.IzinData(izin.id, date, formattedTanggal, izin.jenisIzin, periodeStr, durasiStr, "Izin"))
                    }
                }
            }
        }

        items.distinctBy { it.rawDate }.sortedByDescending { it.rawDate }
    }

    private fun generateDatesInRange(start: String, end: String): List<String> {
        val dates = mutableListOf<String>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        try {
            val startDate = sdf.parse(start)
            val endDate = sdf.parse(end)
            if (startDate != null && endDate != null) {
                val cal = Calendar.getInstance().apply { time = startDate }
                while (!cal.time.after(endDate)) {
                    dates.add(sdf.format(cal.time))
                    cal.add(Calendar.DATE, 1)
                }
            }
        } catch (e: Exception) {}
        return dates
    }

    fun insertKehadiran(kehadiran: Kehadiran) = viewModelScope.launch {
        insertKehadiranUseCase(kehadiran)
    }

    fun updateKehadiran(kehadiran: Kehadiran) = viewModelScope.launch {
        updateKehadiranUseCase(kehadiran)
    }

    suspend fun getKehadiranById(id: Int) = getKehadiranByIdUseCase(id)
    
    suspend fun getKehadiranByTanggal(tanggal: String) = getKehadiranByTanggalUseCase(tanggal)
}

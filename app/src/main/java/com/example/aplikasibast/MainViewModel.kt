package com.example.aplikasibast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainViewModel(
    private val repository: AppRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    val userName = sessionManager.getUserName() ?: "User"
    val userRole = sessionManager.getUserRole() ?: "Staff"
    
    val workHours = "Reguler (09:00-17:00)"
    val currentDayUI: String get() = DateUtils.formatToUi(DateUtils.getTodayDb())
    
    private val todayDb: String get() = DateUtils.getTodayDb()

    val allKehadiran: Flow<List<KehadiranEntity>> = repository.allKehadiran

    val combinedRiwayat: Flow<List<RiwayatItem>> = combine(
        repository.allKehadiran,
        repository.getPengajuanByStatus(AppConstants.STATUS_DISETUJUI)
    ) { kehadiranList, izinList ->
        val items = mutableListOf<RiwayatItem>()

        // 1. Tambahkan data kehadiran dari database
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
                    id = entity.id, rawDate = entity.tanggal, tanggal = formattedTanggal, status = "Sakit"
                ))
                "Alpa" -> items.add(RiwayatItem.AlpaData(
                    id = entity.id, rawDate = entity.tanggal, tanggal = formattedTanggal, status = "Alpa"
                ))
                else -> items.add(RiwayatItem.LiburData(
                    id = entity.id, rawDate = entity.tanggal, tanggal = formattedTanggal, status = entity.status
                ))
            }
        }

        // 2. Tambahkan pengajuan izin/sakit yang DISETUJUI (jika belum ada di tabel kehadiran)
        izinList.forEach { izin ->
            val days = generateDatesInRange(izin.tanggalMulai, izin.tanggalSelesai)
            days.forEach { date ->
                if (kehadiranList.none { it.tanggal == date }) {
                    val formattedTanggal = DateUtils.formatToUi(date)
                    if (izin.jenisIzin.equals("Sakit", true)) {
                        items.add(RiwayatItem.SakitData(
                            id = izin.id, rawDate = date, tanggal = formattedTanggal, 
                            periode = "${DateUtils.formatToUi(izin.tanggalMulai)} - ${DateUtils.formatToUi(izin.tanggalSelesai)}",
                            durasi = "${DateUtils.calculateDays(izin.tanggalMulai, izin.tanggalSelesai)} Hari",
                            status = "Sakit"
                        ))
                    } else {
                        items.add(RiwayatItem.IzinData(
                            id = izin.id, rawDate = date, tanggal = formattedTanggal, 
                            jenisIzin = izin.jenisIzin,
                            periode = "${DateUtils.formatToUi(izin.tanggalMulai)} - ${DateUtils.formatToUi(izin.tanggalSelesai)}",
                            durasi = "${DateUtils.calculateDays(izin.tanggalMulai, izin.tanggalSelesai)} Hari",
                            status = "Izin"
                        ))
                    }
                }
            }
        }

        // 3. Urutkan berdasarkan tanggal terbaru
        items.distinctBy { it.rawDate }.sortedByDescending { it.rawDate }
    }

    private fun generateDatesInRange(start: String, end: String): List<String> {
        val dates = mutableListOf<String>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        try {
            val startDate = sdf.parse(start)
            val endDate = sdf.parse(end)
            if (startDate != null && endDate != null) {
                val calendar = Calendar.getInstance()
                calendar.time = startDate
                while (!calendar.time.after(endDate)) {
                    dates.add(sdf.format(calendar.time))
                    calendar.add(Calendar.DATE, 1)
                }
            }
        } catch (e: Exception) {}
        return dates
    }

    val dashboardState: StateFlow<DashboardData> = combine(
        repository.allKehadiran,
        repository.getPengajuanByStatus(AppConstants.STATUS_DISETUJUI)
    ) { kehadiranList, izinList ->
        val todayAbsen = kehadiranList.find { it.tanggal == todayDb }
        val activeIzin = izinList.find { todayDb >= it.tanggalMulai && todayDb <= it.tanggalSelesai }
        
        val status = when {
            todayAbsen != null -> todayAbsen.status
            activeIzin != null -> if (activeIzin.jenisIzin.equals("Sakit", true)) "Sakit" else "Izin"
            isWeekend() -> "Libur"
            isAfterWorkHours() && todayAbsen == null -> "Alpa"
            else -> userRole
        }

        DashboardData(
            kehadiran = todayAbsen,
            currentStatus = status,
            isIzinActive = activeIzin != null
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardData())

    private fun isWeekend(): Boolean {
        val day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        return day == Calendar.SATURDAY || day == Calendar.SUNDAY
    }

    private fun isAfterWorkHours(): Boolean {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 17
    }

    suspend fun getKehadiranById(id: Int) = repository.getKehadiranById(id)

    fun insertKehadiran(kehadiran: KehadiranEntity) {
        viewModelScope.launch { repository.insertKehadiran(kehadiran) }
    }

    fun updateKehadiran(kehadiran: KehadiranEntity) {
        viewModelScope.launch { repository.updateKehadiran(kehadiran) }
    }

    fun getPengajuanByStatus(status: String) = repository.getPengajuanByStatus(status)

    suspend fun getPengajuanById(id: Int) = repository.getPengajuanById(id)

    fun updatePengajuan(pengajuan: PengajuanIzinEntity) {
        viewModelScope.launch {
            repository.insertPengajuan(pengajuan)
        }
    }

    fun submitPengajuanIzin(
        jenisIzin: String, tanggalMulai: String, tanggalSelesai: String,
        alasan: String, tanggalPengajuan: String, lampiranPath: String? = null
    ) {
        viewModelScope.launch {
            val entity = PengajuanIzinEntity(
                tanggalPengajuan = tanggalPengajuan,
                jenisIzin = jenisIzin,
                tanggalMulai = tanggalMulai,
                tanggalSelesai = tanggalSelesai,
                alasan = alasan,
                lampiranPath = lampiranPath,
                teknisiNama = userName
            )
            repository.insertPengajuan(entity)
        }
    }
}

data class DashboardData(
    val kehadiran: KehadiranEntity? = null,
    val currentStatus: String = "",
    val isIzinActive: Boolean = false
)

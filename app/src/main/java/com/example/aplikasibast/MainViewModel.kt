package com.example.aplikasibast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikasibast.domain.model.Kehadiran
import com.example.aplikasibast.domain.model.PengajuanIzin
import com.example.aplikasibast.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainViewModel(
    private val getPengajuanByStatusUseCase: GetPengajuanByStatusUseCase,
    private val getPengajuanByIdUseCase: GetPengajuanByIdUseCase,
    private val submitPengajuanUseCase: SubmitPengajuanUseCase,
    private val updatePengajuanUseCase: UpdatePengajuanUseCase,
    private val getAllKehadiranUseCase: GetAllKehadiranUseCase,
    private val getKehadiranByIdUseCase: GetKehadiranByIdUseCase,
    private val insertKehadiranUseCase: InsertKehadiranUseCase,
    private val updateKehadiranUseCase: UpdateKehadiranUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    val userName = sessionManager.getUserName() ?: "User"
    val userRole = sessionManager.getUserRole() ?: "Staff"
    
    val workHours = "Reguler (09:00-17:00)"
    val currentDayUI: String get() = DateUtils.formatToUi(DateUtils.getTodayDb())
    private val todayDb: String get() = DateUtils.getTodayDb()

    val allKehadiran: Flow<List<Kehadiran>> = getAllKehadiranUseCase()

    // Logika Menggabungkan Riwayat (Professional & Reactive)
    val combinedRiwayat: Flow<List<RiwayatItem>> = combine(
        getAllKehadiranUseCase(),
        getPengajuanByStatusUseCase(AppConstants.STATUS_DISETUJUI)
    ) { kehadiranList, izinList ->
        val items = mutableListOf<RiwayatItem>()

        // 1. Tambahkan Absensi
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
                    id = entity.id, rawDate = entity.tanggal, tanggal = formattedTanggal, 
                    periode = "-", durasi = "-", status = "Sakit"
                ))
                "Alpa" -> items.add(RiwayatItem.AlpaData(
                    id = entity.id, rawDate = entity.tanggal, tanggal = formattedTanggal, status = "Alpa"
                ))
                else -> items.add(RiwayatItem.LiburData(
                    id = entity.id, rawDate = entity.tanggal, tanggal = formattedTanggal, status = entity.status
                ))
            }
        }

        // 2. Tambahkan Izin/Sakit yang disetujui (Jika belum diabsen)
        izinList.forEach { izin ->
            val dates = generateDatesInRange(izin.tanggalMulai, izin.tanggalSelesai)
            dates.forEach { date ->
                if (kehadiranList.none { it.tanggal == date }) {
                    val formattedTanggal = DateUtils.formatToUi(date)
                    val periodeStr = "${DateUtils.formatToUi(izin.tanggalMulai)} - ${DateUtils.formatToUi(izin.tanggalSelesai)}"
                    val durasiStr = "${DateUtils.calculateDays(izin.tanggalMulai, izin.tanggalSelesai)} Hari"
                    
                    if (izin.jenisIzin.equals("Sakit", true)) {
                        items.add(RiwayatItem.SakitData(
                            id = izin.id, rawDate = date, tanggal = formattedTanggal, 
                            periode = periodeStr, durasi = durasiStr
                        ))
                    } else {
                        items.add(RiwayatItem.IzinData(
                            id = izin.id, rawDate = date, tanggal = formattedTanggal, 
                            jenisIzin = izin.jenisIzin, periode = periodeStr, durasi = durasiStr, status = "Izin"
                        ))
                    }
                }
            }
        }

        items.distinctBy { it.rawDate }.sortedByDescending { it.rawDate }
    }

    val dashboardState: StateFlow<DashboardData> = combine(
        getAllKehadiranUseCase(),
        getPengajuanByStatusUseCase(AppConstants.STATUS_DISETUJUI)
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

        DashboardData(kehadiran = todayAbsen, currentStatus = status, isIzinActive = activeIzin != null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardData())

    private fun isWeekend(): Boolean {
        val day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        return day == Calendar.SATURDAY || day == Calendar.SUNDAY
    }

    private fun isAfterWorkHours(): Boolean {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 17
    }

    private fun generateDatesInRange(start: String, end: String): List<String> {
        val dates = mutableListOf<String>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        try {
            val startDate = sdf.parse(start)
            val endDate = sdf.parse(end)
            if (startDate != null && endDate != null) {
                val cal = Calendar.getInstance()
                cal.time = startDate
                while (!cal.time.after(endDate)) {
                    dates.add(sdf.format(cal.time))
                    cal.add(Calendar.DATE, 1)
                }
            }
        } catch (e: Exception) {}
        return dates
    }

    // --- Actions ---
    fun insertKehadiran(kehadiran: Kehadiran) = viewModelScope.launch { insertKehadiranUseCase(kehadiran) }
    fun updateKehadiran(kehadiran: Kehadiran) = viewModelScope.launch { updateKehadiranUseCase(kehadiran) }
    fun getPengajuanByStatus(status: String) = getPengajuanByStatusUseCase(status)
    suspend fun getPengajuanById(id: Int) = getPengajuanByIdUseCase(id)
    fun updatePengajuan(pengajuan: PengajuanIzin) = viewModelScope.launch { updatePengajuanUseCase(pengajuan) }
    suspend fun getKehadiranById(id: Int) = getKehadiranByIdUseCase(id)

    fun submitPengajuanIzin(jenisIzin: String, tanggalMulai: String, tanggalSelesai: String, alasan: String, tanggalPengajuan: String, lampiranPath: String? = null) {
        viewModelScope.launch {
            val model = PengajuanIzin(
                id = 0, tanggalPengajuan = tanggalPengajuan, jenisIzin = jenisIzin,
                tanggalMulai = tanggalMulai, tanggalSelesai = tanggalSelesai, alasan = alasan,
                status = AppConstants.STATUS_DIAJUKAN, lampiranPath = lampiranPath,
                teknisiNama = userName, alasanPenolakan = null, tanggalDiproses = null
            )
            submitPengajuanUseCase(model)
        }
    }
}

data class DashboardData(
    val kehadiran: Kehadiran? = null,
    val currentStatus: String = "",
    val isIzinActive: Boolean = false
)

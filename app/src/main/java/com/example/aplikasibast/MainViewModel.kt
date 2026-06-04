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
    
    // Format UI untuk tampilan di Dashboard
    val currentDayUI = SimpleDateFormat(AppConstants.DATE_FORMAT_UI, Locale("id", "ID")).format(Calendar.getInstance().time)
    
    // Variabel yang sempat hilang
    val workHours = "Reguler (09:00-17:00)"
    
    // Format DB untuk pembandingan data
    private val todayDb = SimpleDateFormat(AppConstants.DATE_FORMAT_DB, Locale.US).format(Calendar.getInstance().time)

    val allKehadiran: Flow<List<KehadiranEntity>> = repository.allKehadiran

    // State utama untuk Dashboard (Menggabungkan data Absensi dan Izin)
    val dashboardState: StateFlow<DashboardData> = combine(
        repository.allKehadiran,
        repository.getPengajuanByStatus(AppConstants.STATUS_DISETUJUI)
    ) { kehadiranList, izinList ->
        val todayAbsen = kehadiranList.find { it.tanggal == currentDayUI } // Cek absen berdasarkan tanggal UI yang tersimpan
        val activeIzin = izinList.find { todayDb >= it.tanggalMulai && todayDb <= it.tanggalSelesai }
        
        val status = when {
            todayAbsen != null -> "Hadir"
            activeIzin != null -> "Izin"
            isWeekend() -> "Libur"
            isAfterWorkHours() -> "Alpa"
            else -> userRole
        }

        DashboardData(
            kehadiran = todayAbsen,
            currentStatus = status,
            isIzinActive = activeIzin != null
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardData())

    // --- Helper Functions ---
    private fun isWeekend(): Boolean {
        val day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        return day == Calendar.SATURDAY || day == Calendar.SUNDAY
    }

    private fun isAfterWorkHours(): Boolean {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 17
    }

    // Kehadiran
    suspend fun getKehadiranById(id: Int) = repository.getKehadiranById(id)

    fun insertKehadiran(kehadiran: KehadiranEntity) {
        viewModelScope.launch { repository.insertKehadiran(kehadiran) }
    }

    fun updateKehadiran(kehadiran: KehadiranEntity) {
        viewModelScope.launch { repository.updateKehadiran(kehadiran) }
    }

    // Pengajuan Izin
    fun getPengajuanByStatus(status: String) = repository.getPengajuanByStatus(status)

    suspend fun getPengajuanById(id: Int) = repository.getPengajuanById(id)

    fun updatePengajuan(pengajuan: PengajuanIzinEntity) {
        viewModelScope.launch {
            repository.insertPengajuan(pengajuan) // Menggunakan insert dengan REPLACE atau buat fungsi update
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

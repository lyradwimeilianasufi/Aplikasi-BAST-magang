package com.example.aplikasibast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikasibast.domain.model.Kehadiran
import com.example.aplikasibast.domain.model.PengajuanIzin
import com.example.aplikasibast.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

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

    // Menggunakan Domain Model (Kehadiran) bukan Entity
    val allKehadiran: Flow<List<Kehadiran>> = getAllKehadiranUseCase()

    val dashboardState: StateFlow<DashboardData> = combine(
        getAllKehadiranUseCase(),
        getPengajuanByStatusUseCase(AppConstants.STATUS_DISETUJUI)
    ) { kehadiranList, izinList ->
        val todayAbsen = kehadiranList.find { it.tanggal == todayDb }
        val activeIzin = izinList.find { todayDb >= it.tanggalMulai && todayDb <= it.tanggalSelesai }
        
        val status = when {
            todayAbsen != null -> todayAbsen.status
            activeIzin != null -> "Izin"
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

    // --- Logic Kehadiran ---
    suspend fun getKehadiranById(id: Int) = getKehadiranByIdUseCase(id)

    fun insertKehadiran(kehadiran: Kehadiran) {
        viewModelScope.launch { insertKehadiranUseCase(kehadiran) }
    }

    fun updateKehadiran(kehadiran: Kehadiran) {
        viewModelScope.launch { updateKehadiranUseCase(kehadiran) }
    }

    // --- Logic Pengajuan Izin ---
    fun getPengajuanByStatus(status: String) = getPengajuanByStatusUseCase(status)

    suspend fun getPengajuanById(id: Int) = getPengajuanByIdUseCase(id)

    fun updatePengajuan(pengajuan: PengajuanIzin) {
        viewModelScope.launch { updatePengajuanUseCase(pengajuan) }
    }

    fun submitPengajuanIzin(
        jenisIzin: String, tanggalMulai: String, tanggalSelesai: String,
        alasan: String, tanggalPengajuan: String, lampiranPath: String? = null
    ) {
        viewModelScope.launch {
            val model = PengajuanIzin(
                id = 0, // Auto-generate
                tanggalPengajuan = tanggalPengajuan,
                jenisIzin = jenisIzin,
                tanggalMulai = tanggalMulai,
                tanggalSelesai = tanggalSelesai,
                alasan = alasan,
                status = AppConstants.STATUS_DIAJUKAN,
                lampiranPath = lampiranPath,
                teknisiNama = userName,
                alasanPenolakan = null,
                tanggalDiproses = null
            )
            submitPengajuanUseCase(model)
        }
    }
}

data class DashboardData(
    val kehadiran: Kehadiran? = null, // Menggunakan Domain Model
    val currentStatus: String = "",
    val isIzinActive: Boolean = false
)

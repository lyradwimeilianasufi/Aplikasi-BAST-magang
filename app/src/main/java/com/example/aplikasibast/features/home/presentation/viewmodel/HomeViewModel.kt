package com.example.aplikasibast.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikasibast.core.constants.AppConstants
import com.example.aplikasibast.core.session.SessionManager
import com.example.aplikasibast.core.utils.DateUtils
import com.example.aplikasibast.features.attendance.domain.model.Kehadiran
import com.example.aplikasibast.features.attendance.domain.usecase.GetAllKehadiranUseCase
import com.example.aplikasibast.features.permission.domain.usecase.GetPengajuanByStatusUseCase
import kotlinx.coroutines.flow.*
import java.util.Calendar

class HomeViewModel(
    private val getAllKehadiranUseCase: GetAllKehadiranUseCase,
    private val getPengajuanByStatusUseCase: GetPengajuanByStatusUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    val userName = sessionManager.getUserName() ?: "User"
    val userRole = sessionManager.getUserRole() ?: "Teknisi"
    val workHours = "Reguler (09:00-17:00)"
    val currentDayUI: String get() = DateUtils.formatToUi(DateUtils.getTodayDb())
    private val todayDb: String get() = DateUtils.getTodayDb()

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
            else -> sessionManager.getUserRole() ?: "Teknisi"
        }

        DashboardData(todayAbsen, status)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardData())

    private fun isWeekend(): Boolean {
        val day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        return day == Calendar.SATURDAY || day == Calendar.SUNDAY
    }

    private fun isAfterWorkHours(): Boolean = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 17
}

data class DashboardData(
    val kehadiran: Kehadiran? = null,
    val currentStatus: String = ""
)

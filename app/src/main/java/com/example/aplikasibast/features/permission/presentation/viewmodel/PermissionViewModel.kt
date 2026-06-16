package com.example.aplikasibast.features.permission.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikasibast.core.session.SessionManager
import com.example.aplikasibast.core.utils.DateUtils
import com.example.aplikasibast.core.constants.AppConstants
import com.example.aplikasibast.features.attendance.domain.repository.IKehadiranRepository
import com.example.aplikasibast.features.permission.domain.model.PengajuanIzin
import com.example.aplikasibast.features.permission.domain.repository.IPengajuanRepository
import com.example.aplikasibast.features.permission.domain.usecase.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class PermissionViewModel(
    private val getPengajuanByStatusUseCase: GetPengajuanByStatusUseCase,
    private val getPengajuanByIdUseCase: GetPengajuanByIdUseCase,
    private val submitPengajuanUseCase: SubmitPengajuanUseCase,
    private val permissionRepository: IPengajuanRepository,
    private val attendanceRepository: IKehadiranRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    val userName = sessionManager.getUserName() ?: "User"

    private val _submitResult = MutableSharedFlow<Result<String>>()
    val submitResult = _submitResult.asSharedFlow()

    fun getPengajuanByStatus(status: String): Flow<List<PengajuanIzin>> {
        return getPengajuanByStatusUseCase(status)
    }

    suspend fun getPengajuanById(id: Int): PengajuanIzin? {
        return getPengajuanByIdUseCase(id)
    }

    fun submitPengajuanIzin(
        jenisIzin: String,
        tanggalMulai: String,
        tanggalSelesai: String,
        alasan: String,
        tanggalPengajuan: String,
        lampiranPath: String? = null
    ) {
        viewModelScope.launch {
            // 1. Cek duplikasi pengajuan izin (overlap)
            val overlap = permissionRepository.checkOverlappingPengajuan(tanggalMulai, tanggalSelesai)
            if (overlap != null) {
                _submitResult.emit(Result.failure(Exception("Sudah ada pengajuan izin di tanggal tersebut")))
                return@launch
            }

            // 2. Cek apakah sudah absen hadir di tanggal tersebut
            // Sederhananya cek tanggal mulai, bisa diperluas untuk loop range tanggal
            val kehadiran = attendanceRepository.getKehadiranByTanggal(tanggalMulai)
            if (kehadiran != null && kehadiran.status == "Hadir") {
                _submitResult.emit(Result.failure(Exception("Anda tercatat Hadir pada tanggal $tanggalMulai, tidak bisa mengajukan izin")))
                return@launch
            }

            val model = PengajuanIzin(
                id = 0,
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
            
            try {
                submitPengajuanUseCase(model)
                _submitResult.emit(Result.success("Pengajuan berhasil dikirim"))
            } catch (e: Exception) {
                _submitResult.emit(Result.failure(e))
            }
        }
    }
}

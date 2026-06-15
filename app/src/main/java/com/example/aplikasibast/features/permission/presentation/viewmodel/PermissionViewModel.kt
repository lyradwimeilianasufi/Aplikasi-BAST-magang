package com.example.aplikasibast.features.permission.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikasibast.core.session.SessionManager
import com.example.aplikasibast.core.utils.DateUtils
import com.example.aplikasibast.core.constants.AppConstants
import com.example.aplikasibast.features.permission.domain.model.PengajuanIzin
import com.example.aplikasibast.features.permission.domain.usecase.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PermissionViewModel(
    private val getPengajuanByStatusUseCase: GetPengajuanByStatusUseCase,
    private val getPengajuanByIdUseCase: GetPengajuanByIdUseCase,
    private val submitPengajuanUseCase: SubmitPengajuanUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    val userName = sessionManager.getUserName() ?: "User"

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
            submitPengajuanUseCase(model)
        }
    }
}

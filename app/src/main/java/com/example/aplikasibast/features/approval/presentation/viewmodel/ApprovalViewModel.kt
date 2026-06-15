package com.example.aplikasibast.features.approval.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikasibast.features.permission.domain.model.PengajuanIzin
import com.example.aplikasibast.features.permission.domain.usecase.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ApprovalViewModel(
    private val getPengajuanByStatusUseCase: GetPengajuanByStatusUseCase,
    private val getPengajuanByIdUseCase: GetPengajuanByIdUseCase,
    private val updatePengajuanUseCase: UpdatePengajuanUseCase
) : ViewModel() {

    fun getPengajuanByStatus(status: String): Flow<List<PengajuanIzin>> {
        return getPengajuanByStatusUseCase(status)
    }

    suspend fun getPengajuanById(id: Int): PengajuanIzin? {
        return getPengajuanByIdUseCase(id)
    }

    fun updatePengajuan(pengajuan: PengajuanIzin) {
        viewModelScope.launch {
            updatePengajuanUseCase(pengajuan)
        }
    }
}

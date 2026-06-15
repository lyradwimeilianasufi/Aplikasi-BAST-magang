package com.example.aplikasibast.features.permission.domain.usecase

import com.example.aplikasibast.features.permission.domain.model.PengajuanIzin
import com.example.aplikasibast.features.permission.domain.repository.IPengajuanRepository

class UpdatePengajuanUseCase(private val repository: IPengajuanRepository) {
    suspend operator fun invoke(pengajuan: PengajuanIzin) {
        repository.updatePengajuan(pengajuan)
    }
}

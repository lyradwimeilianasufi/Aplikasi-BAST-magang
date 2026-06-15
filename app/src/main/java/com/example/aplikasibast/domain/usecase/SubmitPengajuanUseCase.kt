package com.example.aplikasibast.domain.usecase

import com.example.aplikasibast.domain.model.PengajuanIzin
import com.example.aplikasibast.domain.repository.IPengajuanRepository

class SubmitPengajuanUseCase(private val repository: IPengajuanRepository) {
    suspend operator fun invoke(pengajuan: PengajuanIzin) {
        repository.insertPengajuan(pengajuan)
    }
}

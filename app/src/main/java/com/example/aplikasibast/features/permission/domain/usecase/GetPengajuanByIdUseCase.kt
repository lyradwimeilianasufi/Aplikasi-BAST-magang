package com.example.aplikasibast.features.permission.domain.usecase

import com.example.aplikasibast.features.permission.domain.model.PengajuanIzin
import com.example.aplikasibast.features.permission.domain.repository.IPengajuanRepository

class GetPengajuanByIdUseCase(private val repository: IPengajuanRepository) {
    suspend operator fun invoke(id: Int): PengajuanIzin? {
        return repository.getPengajuanById(id)
    }
}

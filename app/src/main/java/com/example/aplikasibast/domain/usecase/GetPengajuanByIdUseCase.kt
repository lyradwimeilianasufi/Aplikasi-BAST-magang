package com.example.aplikasibast.domain.usecase

import com.example.aplikasibast.domain.model.PengajuanIzin
import com.example.aplikasibast.domain.repository.IPengajuanRepository

class GetPengajuanByIdUseCase(private val repository: IPengajuanRepository) {
    suspend operator fun invoke(id: Int): PengajuanIzin? {
        return repository.getPengajuanById(id)
    }
}

package com.example.aplikasibast.features.permission.domain.usecase

import com.example.aplikasibast.features.permission.domain.model.PengajuanIzin
import com.example.aplikasibast.features.permission.domain.repository.IPengajuanRepository
import kotlinx.coroutines.flow.Flow

class GetPengajuanByStatusUseCase(private val repository: IPengajuanRepository) {
    operator fun invoke(status: String): Flow<List<PengajuanIzin>> {
        return repository.getPengajuanByStatus(status)
    }
}

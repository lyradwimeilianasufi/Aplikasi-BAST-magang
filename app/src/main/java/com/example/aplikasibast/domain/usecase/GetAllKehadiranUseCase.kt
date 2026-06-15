package com.example.aplikasibast.domain.usecase

import com.example.aplikasibast.domain.model.Kehadiran
import com.example.aplikasibast.domain.repository.IKehadiranRepository
import kotlinx.coroutines.flow.Flow

class GetAllKehadiranUseCase(private val repository: IKehadiranRepository) {
    operator fun invoke(): Flow<List<Kehadiran>> {
        return repository.getAllKehadiran()
    }
}

package com.example.aplikasibast.features.attendance.domain.usecase

import com.example.aplikasibast.features.attendance.domain.model.Kehadiran
import com.example.aplikasibast.features.attendance.domain.repository.IKehadiranRepository
import kotlinx.coroutines.flow.Flow

class GetAllKehadiranUseCase(private val repository: IKehadiranRepository) {
    operator fun invoke(): Flow<List<Kehadiran>> {
        return repository.getAllKehadiran()
    }
}

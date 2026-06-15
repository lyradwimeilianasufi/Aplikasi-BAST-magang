package com.example.aplikasibast.features.attendance.domain.usecase

import com.example.aplikasibast.features.attendance.domain.model.Kehadiran
import com.example.aplikasibast.features.attendance.domain.repository.IKehadiranRepository

class GetKehadiranByIdUseCase(private val repository: IKehadiranRepository) {
    suspend operator fun invoke(id: Int): Kehadiran? {
        return repository.getKehadiranById(id)
    }
}

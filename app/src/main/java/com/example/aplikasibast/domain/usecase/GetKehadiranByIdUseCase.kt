package com.example.aplikasibast.domain.usecase

import com.example.aplikasibast.domain.model.Kehadiran
import com.example.aplikasibast.domain.repository.IKehadiranRepository

class GetKehadiranByIdUseCase(private val repository: IKehadiranRepository) {
    suspend operator fun invoke(id: Int): Kehadiran? {
        return repository.getKehadiranById(id)
    }
}

package com.example.aplikasibast.domain.usecase

import com.example.aplikasibast.domain.model.Kehadiran
import com.example.aplikasibast.domain.repository.IKehadiranRepository

class InsertKehadiranUseCase(private val repository: IKehadiranRepository) {
    suspend operator fun invoke(kehadiran: Kehadiran) {
        repository.insertKehadiran(kehadiran)
    }
}

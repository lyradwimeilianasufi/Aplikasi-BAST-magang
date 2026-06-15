package com.example.aplikasibast.features.attendance.domain.usecase

import com.example.aplikasibast.features.attendance.domain.model.Kehadiran
import com.example.aplikasibast.features.attendance.domain.repository.IKehadiranRepository

class InsertKehadiranUseCase(private val repository: IKehadiranRepository) {
    suspend operator fun invoke(kehadiran: Kehadiran) {
        repository.insertKehadiran(kehadiran)
    }
}

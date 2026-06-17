package com.example.aplikasibast.features.attendance.domain.usecase

import com.example.aplikasibast.features.attendance.domain.model.Kehadiran
import com.example.aplikasibast.features.attendance.domain.repository.IKehadiranRepository

class GetKehadiranByTanggalUseCase(private val repository: IKehadiranRepository) {
    suspend operator fun invoke(tanggal: String): Kehadiran? {
        return repository.getKehadiranByTanggal(tanggal)
    }
}

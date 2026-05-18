package com.example.aplikasibast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: AppRepository) : ViewModel() {
    
    // User Data
    val userName = "Trisnualdi"
    val userRole = "Teknisi"
    val currentDay = "Kamis, 02 Jan 2024"
    val workHours = "Full Day (06:00)"
    val jamMasukTarget = "06:00"
    val jamMasukActual = "-"
    val jamKeluarTarget = "-"
    val jamKeluarActual = "-"

    // Kehadiran Data
    val allKehadiran: Flow<List<KehadiranEntity>> = repository.allKehadiran

    suspend fun getKehadiranById(id: Int): KehadiranEntity? {
        return repository.getKehadiranById(id)
    }

    fun insertKehadiran(kehadiran: KehadiranEntity) {
        viewModelScope.launch {
            repository.insertKehadiran(kehadiran)
        }
    }

    // Pengajuan Izin Data
    fun getPengajuanByStatus(status: String): Flow<List<PengajuanIzinEntity>> {
        return repository.getPengajuanByStatus(status)
    }

    suspend fun getPengajuanById(id: Int): PengajuanIzinEntity? {
        return repository.getPengajuanById(id)
    }

    fun submitPengajuanIzin(
        jenisIzin: String,
        tanggalMulai: String,
        tanggalSelesai: String,
        alasan: String,
        tanggalPengajuan: String,
        lampiranPath: String? = null
    ) {
        viewModelScope.launch {
            val entity = PengajuanIzinEntity(
                tanggalPengajuan = tanggalPengajuan,
                jenisIzin = jenisIzin,
                tanggalMulai = tanggalMulai,
                tanggalSelesai = tanggalSelesai,
                alasan = alasan,
                lampiranPath = lampiranPath
            )
            repository.insertPengajuan(entity)
        }
    }
    
    fun updateStatusIzin(id: Int, status: String) {
        viewModelScope.launch {
            repository.updatePengajuanStatus(id, status)
        }
    }
}

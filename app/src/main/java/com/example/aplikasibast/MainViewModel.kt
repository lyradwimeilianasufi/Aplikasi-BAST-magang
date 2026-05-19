package com.example.aplikasibast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainViewModel(private val repository: AppRepository) : ViewModel() {
    
    val userName = "Trisnualdi"
    val userRole = "Teknisi"
    val currentDay = SimpleDateFormat("EEEE, dd MMM yyyy", Locale("id", "ID")).format(Calendar.getInstance().time)
    val workHours = "Full Day (06:00)"

    // Aliran data kehadiran hari ini secara real-time
    val todayKehadiran: StateFlow<KehadiranEntity?> = repository.allKehadiran
        .map { list ->
            val tanggal = SimpleDateFormat("EEEE, dd MMM yyyy", Locale("id", "ID")).format(Calendar.getInstance().time)
            list.find { it.tanggal == tanggal }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allKehadiran: Flow<List<KehadiranEntity>> = repository.allKehadiran

    // Fungsi untuk mengambil detail kehadiran berdasarkan ID
    suspend fun getKehadiranById(id: Int): KehadiranEntity? {
        return repository.getKehadiranById(id)
    }

    fun insertKehadiran(kehadiran: KehadiranEntity) {
        viewModelScope.launch {
            repository.insertKehadiran(kehadiran)
        }
    }

    fun updateKehadiran(kehadiran: KehadiranEntity) {
        viewModelScope.launch {
            repository.updateKehadiran(kehadiran)
        }
    }

    // --- Pengajuan Izin ---

    fun getPengajuanByStatus(status: String): Flow<List<PengajuanIzinEntity>> = repository.getPengajuanByStatus(status)

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

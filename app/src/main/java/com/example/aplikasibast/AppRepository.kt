package com.example.aplikasibast

import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val kehadiranDao: KehadiranDao,
    private val pengajuanIzinDao: PengajuanIzinDao
) {
    // Kehadiran
    val allKehadiran: Flow<List<KehadiranEntity>> = kehadiranDao.getAllKehadiran()

    suspend fun getKehadiranById(id: Int): KehadiranEntity? {
        return kehadiranDao.getKehadiranById(id)
    }

    suspend fun getKehadiranByTanggal(tanggal: String): KehadiranEntity? {
        return kehadiranDao.getKehadiranByTanggal(tanggal)
    }

    suspend fun insertKehadiran(kehadiran: KehadiranEntity) {
        kehadiranDao.insertKehadiran(kehadiran)
    }

    suspend fun updateKehadiran(kehadiran: KehadiranEntity) {
        kehadiranDao.updateKehadiran(kehadiran)
    }

    // Pengajuan Izin
    fun getPengajuanByStatus(status: String): Flow<List<PengajuanIzinEntity>> {
        return pengajuanIzinDao.getPengajuanByStatus(status)
    }

    suspend fun getPengajuanById(id: Int): PengajuanIzinEntity? {
        return pengajuanIzinDao.getPengajuanById(id)
    }

    suspend fun insertPengajuan(pengajuan: PengajuanIzinEntity) {
        pengajuanIzinDao.insertPengajuan(pengajuan)
    }

    suspend fun updatePengajuanStatus(id: Int, status: String) {
        pengajuanIzinDao.updateStatus(id, status)
    }
}

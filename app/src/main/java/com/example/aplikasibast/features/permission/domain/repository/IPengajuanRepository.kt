package com.example.aplikasibast.features.permission.domain.repository

import com.example.aplikasibast.features.permission.domain.model.PengajuanIzin
import kotlinx.coroutines.flow.Flow

interface IPengajuanRepository {
    fun getPengajuanByStatus(status: String): Flow<List<PengajuanIzin>>
    suspend fun getPengajuanById(id: Int): PengajuanIzin?
    suspend fun checkOverlappingPengajuan(start: String, end: String): PengajuanIzin?
    suspend fun insertPengajuan(pengajuan: PengajuanIzin)
    suspend fun updatePengajuan(pengajuan: PengajuanIzin)
}

package com.example.aplikasibast.domain.repository

import com.example.aplikasibast.domain.model.PengajuanIzin
import kotlinx.coroutines.flow.Flow

interface IPengajuanRepository {
    fun getPengajuanByStatus(status: String): Flow<List<PengajuanIzin>>
    suspend fun getPengajuanById(id: Int): PengajuanIzin?
    suspend fun insertPengajuan(pengajuan: PengajuanIzin)
    suspend fun updatePengajuan(pengajuan: PengajuanIzin)
}

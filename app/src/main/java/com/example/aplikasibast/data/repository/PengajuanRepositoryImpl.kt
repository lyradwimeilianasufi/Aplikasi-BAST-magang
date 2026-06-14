package com.example.aplikasibast.data.repository

import com.example.aplikasibast.PengajuanIzinDao
import com.example.aplikasibast.data.mapper.PengajuanMapper
import com.example.aplikasibast.domain.model.PengajuanIzin
import com.example.aplikasibast.domain.repository.IPengajuanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PengajuanRepositoryImpl(private val dao: PengajuanIzinDao) : IPengajuanRepository {
    
    override fun getPengajuanByStatus(status: String): Flow<List<PengajuanIzin>> {
        return dao.getPengajuanByStatus(status).map { list ->
            list.map { PengajuanMapper.toDomain(it) }
        }
    }

    override suspend fun getPengajuanById(id: Int): PengajuanIzin? {
        return dao.getPengajuanById(id)?.let { PengajuanMapper.toDomain(it) }
    }

    override suspend fun insertPengajuan(pengajuan: PengajuanIzin) {
        dao.insertPengajuan(PengajuanMapper.toEntity(pengajuan))
    }

    override suspend fun updatePengajuan(pengajuan: PengajuanIzin) {
        dao.updatePengajuan(PengajuanMapper.toEntity(pengajuan))
    }
}

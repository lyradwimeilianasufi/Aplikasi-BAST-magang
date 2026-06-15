package com.example.aplikasibast.data.repository

import com.example.aplikasibast.KehadiranDao
import com.example.aplikasibast.data.mapper.KehadiranMapper
import com.example.aplikasibast.domain.model.Kehadiran
import com.example.aplikasibast.domain.repository.IKehadiranRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class KehadiranRepositoryImpl(private val dao: KehadiranDao) : IKehadiranRepository {
    
    override fun getAllKehadiran(): Flow<List<Kehadiran>> {
        return dao.getAllKehadiran().map { list ->
            list.map { KehadiranMapper.toDomain(it) }
        }
    }

    override suspend fun getKehadiranById(id: Int): Kehadiran? {
        return dao.getKehadiranById(id)?.let { KehadiranMapper.toDomain(it) }
    }

    override suspend fun insertKehadiran(kehadiran: Kehadiran) {
        dao.insertKehadiran(KehadiranMapper.toEntity(kehadiran))
    }

    override suspend fun updateKehadiran(kehadiran: Kehadiran) {
        dao.updateKehadiran(KehadiranMapper.toEntity(kehadiran))
    }
}

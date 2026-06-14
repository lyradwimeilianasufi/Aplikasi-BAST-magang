package com.example.aplikasibast.domain.repository

import com.example.aplikasibast.domain.model.Kehadiran
import kotlinx.coroutines.flow.Flow

interface IKehadiranRepository {
    fun getAllKehadiran(): Flow<List<Kehadiran>>
    suspend fun getKehadiranById(id: Int): Kehadiran?
    suspend fun insertKehadiran(kehadiran: Kehadiran)
    suspend fun updateKehadiran(kehadiran: Kehadiran)
}

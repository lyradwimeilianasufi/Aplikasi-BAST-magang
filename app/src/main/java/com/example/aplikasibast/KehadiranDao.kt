package com.example.aplikasibast

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface KehadiranDao {
    @Query("SELECT * FROM kehadiran ORDER BY id DESC")
    fun getAllKehadiran(): Flow<List<KehadiranEntity>>

    @Query("SELECT * FROM kehadiran WHERE id = :id LIMIT 1")
    suspend fun getKehadiranById(id: Int): KehadiranEntity?

    @Query("SELECT * FROM kehadiran WHERE tanggal = :tanggal LIMIT 1")
    suspend fun getKehadiranByTanggal(tanggal: String): KehadiranEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKehadiran(kehadiran: KehadiranEntity)

    @Update
    suspend fun updateKehadiran(kehadiran: KehadiranEntity)

    @Delete
    suspend fun deleteKehadiran(kehadiran: KehadiranEntity)
}

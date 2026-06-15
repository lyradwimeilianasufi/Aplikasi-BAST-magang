package com.example.aplikasibast.features.permission.data.source.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PengajuanIzinDao {
    @Query("SELECT * FROM pengajuan_izin ORDER BY id DESC")
    fun getAllPengajuan(): Flow<List<PengajuanIzinEntity>>

    @Query("SELECT * FROM pengajuan_izin WHERE status = :status ORDER BY id DESC")
    fun getPengajuanByStatus(status: String): Flow<List<PengajuanIzinEntity>>

    @Query("SELECT * FROM pengajuan_izin WHERE id = :id LIMIT 1")
    suspend fun getPengajuanById(id: Int): PengajuanIzinEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPengajuan(pengajuan: PengajuanIzinEntity)

    @Update
    suspend fun updatePengajuan(pengajuan: PengajuanIzinEntity)

    @Query("UPDATE pengajuan_izin SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String)

    @Delete
    suspend fun deletePengajuan(pengajuan: PengajuanIzinEntity)
}

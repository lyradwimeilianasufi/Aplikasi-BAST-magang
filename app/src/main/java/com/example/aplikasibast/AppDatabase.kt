package com.example.aplikasibast

import androidx.room.Database
import androidx.room.RoomDatabase

// Menaikkan versi ke 6 karena ada penambahan kolom alasanPenolakan dan tanggalDiproses di PengajuanIzinEntity
@Database(entities = [KehadiranEntity::class, PengajuanIzinEntity::class], version = 6)
abstract class AppDatabase : RoomDatabase() {
    abstract fun kehadiranDao(): KehadiranDao
    abstract fun pengajuanIzinDao(): PengajuanIzinDao

    companion object {
        const val DATABASE_NAME = "bast_database"
    }
}

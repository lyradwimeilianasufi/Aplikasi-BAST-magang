package com.example.aplikasibast

import androidx.room.Database
import androidx.room.RoomDatabase

// Menaikkan versi ke 5 karena ada penambahan kolom koordinat dan pemisahan foto path
@Database(entities = [KehadiranEntity::class, PengajuanIzinEntity::class], version = 5)
abstract class AppDatabase : RoomDatabase() {
    abstract fun kehadiranDao(): KehadiranDao
    abstract fun pengajuanIzinDao(): PengajuanIzinDao

    companion object {
        const val DATABASE_NAME = "bast_database"
    }
}

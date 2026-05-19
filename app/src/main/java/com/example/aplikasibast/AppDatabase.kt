package com.example.aplikasibast

import androidx.room.Database
import androidx.room.RoomDatabase

// Menaikkan versi ke 3 untuk mereset skema database secara menyeluruh
@Database(entities = [KehadiranEntity::class, PengajuanIzinEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun kehadiranDao(): KehadiranDao
    abstract fun pengajuanIzinDao(): PengajuanIzinDao

    companion object {
        const val DATABASE_NAME = "bast_database"
    }
}

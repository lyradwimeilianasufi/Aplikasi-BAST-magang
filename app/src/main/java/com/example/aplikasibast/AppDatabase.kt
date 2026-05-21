package com.example.aplikasibast

import androidx.room.Database
import androidx.room.RoomDatabase

// Menaikkan versi ke 4 untuk mereset skema database karena ada perubahan entitas
@Database(entities = [KehadiranEntity::class, PengajuanIzinEntity::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun kehadiranDao(): KehadiranDao
    abstract fun pengajuanIzinDao(): PengajuanIzinDao

    companion object {
        const val DATABASE_NAME = "bast_database"
    }
}

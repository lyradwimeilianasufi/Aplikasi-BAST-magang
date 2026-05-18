package com.example.aplikasibast

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [KehadiranEntity::class, PengajuanIzinEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun kehadiranDao(): KehadiranDao
    abstract fun pengajuanIzinDao(): PengajuanIzinDao

    companion object {
        const val DATABASE_NAME = "bast_database"
    }
}

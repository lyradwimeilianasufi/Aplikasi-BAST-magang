package com.example.aplikasibast

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.aplikasibast.features.attendance.data.source.local.KehadiranDao
import com.example.aplikasibast.features.attendance.data.source.local.KehadiranEntity
import com.example.aplikasibast.features.permission.data.source.local.PengajuanIzinDao
import com.example.aplikasibast.features.permission.data.source.local.PengajuanIzinEntity

@Database(entities = [KehadiranEntity::class, PengajuanIzinEntity::class], version = 6)
abstract class AppDatabase : RoomDatabase() {
    abstract fun kehadiranDao(): KehadiranDao
    abstract fun pengajuanIzinDao(): PengajuanIzinDao

    companion object {
        const val DATABASE_NAME = "bast_database"
    }
}

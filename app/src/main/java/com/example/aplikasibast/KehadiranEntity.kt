package com.example.aplikasibast

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kehadiran")
data class KehadiranEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tanggal: String,
    val status: String,
    val jamMasuk: String,
    val jamKeluar: String,
    val totalJam: String,
    val fotoPath: String? = null,
    val lokasi: String? = null
)

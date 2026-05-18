package com.example.aplikasibast

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pengajuan_izin")
data class PengajuanIzinEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tanggalPengajuan: String,
    val jenisIzin: String,
    val tanggalMulai: String,
    val tanggalSelesai: String,
    val alasan: String,
    val status: String = "DIAJUKAN", // DIAJUKAN, DISETUJUI, DITOLAK
    val lampiranPath: String? = null,
    val teknisiNama: String = "Trisnualdi" // Default untuk magang ini
)

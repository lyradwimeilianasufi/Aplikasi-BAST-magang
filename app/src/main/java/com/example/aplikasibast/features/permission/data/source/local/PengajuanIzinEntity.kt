package com.example.aplikasibast.features.permission.data.source.local

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
    val status: String = "DIAJUKAN",
    val lampiranPath: String? = null,
    val teknisiNama: String = "Trisnualdi",
    val alasanPenolakan: String? = null,
    val tanggalDiproses: String? = null
)

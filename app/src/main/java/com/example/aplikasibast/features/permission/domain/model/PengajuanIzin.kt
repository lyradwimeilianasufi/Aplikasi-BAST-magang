package com.example.aplikasibast.features.permission.domain.model

data class PengajuanIzin(
    val id: Int,
    val tanggalPengajuan: String,
    val jenisIzin: String,
    val tanggalMulai: String,
    val tanggalSelesai: String,
    val alasan: String,
    val status: String,
    val lampiranPath: String?,
    val teknisiNama: String,
    val alasanPenolakan: String?,
    val tanggalDiproses: String?
)

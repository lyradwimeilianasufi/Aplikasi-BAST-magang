package com.example.aplikasibast.features.attendance.domain.model

data class Kehadiran(
    val id: Int = 0,
    val tanggal: String,
    val status: String,
    val jamMasuk: String,
    val jamKeluar: String,
    val totalJam: String,
    val fotoMasukPath: String? = null,
    val fotoKeluarPath: String? = null,
    val lokasiMasuk: String? = null,
    val lokasiKeluar: String? = null,
    val latMasuk: Double? = null,
    val lngMasuk: Double? = null,
    val latKeluar: Double? = null,
    val lngKeluar: Double? = null
)

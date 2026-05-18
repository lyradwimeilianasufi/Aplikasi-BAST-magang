package com.example.aplikasibast

sealed class RiwayatItem {
    data class KehadiranData(
        val id: Int = 0,
        val tanggal: String,
        val status: String,
        val jamMasuk: String,
        val jamKeluar: String,
        val totalJam: String
    ) : RiwayatItem()

    data class IzinData(
        val id: Int = 0,
        val tanggal: String,
        val jenisIzin: String,
        val periode: String,
        val durasi: String,
        val status: String
    ) : RiwayatItem()

    data class AlpaData(
        val id: Int = 0,
        val tanggal: String,
        val status: String
    ) : RiwayatItem()

    data class LiburData(
        val id: Int = 0,
        val tanggal: String,
        val status: String
    ) : RiwayatItem()
}

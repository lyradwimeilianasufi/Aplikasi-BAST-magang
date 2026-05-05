package com.example.aplikasibast

sealed class RiwayatItem {
    data class KehadiranData(
        val tanggal: String,
        val status: String,
        val jamMasuk: String,
        val jamKeluar: String,
        val totalJam: String
    ) : RiwayatItem()

    data class IzinData(
        val tanggal: String,
        val jenisIzin: String,
        val periode: String,
        val durasi: String,
        val status: String
    ) : RiwayatItem()

    data class AlpaData(
        val tanggal: String,
        val status: String
    ) : RiwayatItem()

    data class LiburData(
        val tanggal: String,
        val status: String
    ) : RiwayatItem()
}

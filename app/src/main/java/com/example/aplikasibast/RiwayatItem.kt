package com.example.aplikasibast

sealed class RiwayatItem {
    abstract val rawDate: String
    abstract val tanggal: String

    data class KehadiranData(
        val id: Int = 0,
        override val rawDate: String,
        override val tanggal: String,
        val status: String,
        val jamMasuk: String,
        val jamKeluar: String,
        val totalJam: String
    ) : RiwayatItem()

    data class IzinData(
        val id: Int = 0,
        override val rawDate: String,
        override val tanggal: String,
        val jenisIzin: String,
        val periode: String,
        val durasi: String,
        val status: String
    ) : RiwayatItem()

    data class SakitData(
        val id: Int = 0,
        override val rawDate: String,
        override val tanggal: String,
        val periode: String = "",
        val durasi: String = "",
        val status: String
    ) : RiwayatItem()

    data class AlpaData(
        val id: Int = 0,
        override val rawDate: String,
        override val tanggal: String,
        val status: String
    ) : RiwayatItem()

    data class LiburData(
        val id: Int = 0,
        override val rawDate: String,
        override val tanggal: String,
        val status: String
    ) : RiwayatItem()
}

package com.example.aplikasibast

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val userName = "Trisnualdi"
    val userRole = "Teknisi"
    val currentDay = "Kamis, 02 Jan 2024"
    val workHours = "Full Day (06:00)"
    val jamMasukTarget = "06:00"
    val jamMasukActual = "-"
    val jamKeluarTarget = "-"
    val jamKeluarActual = "-"
}
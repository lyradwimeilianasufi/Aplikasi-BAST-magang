package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailKehadiranActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_kehadiran)

        // Tombol Back
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Navigasi ke Lokasi Absen Masuk
        val btnLihatLokasiMasuk = findViewById<TextView>(R.id.btnLihatLokasiMasuk)
        btnLihatLokasiMasuk.setOnClickListener {
            val intent = Intent(this, LocationAbsenActivity::class.java)
            startActivity(intent)
        }

        // Navigasi ke Lokasi Absen Keluar
        val btnLihatLokasiKeluar = findViewById<TextView>(R.id.btnLihatLokasiKeluar)
        btnLihatLokasiKeluar.setOnClickListener {
            val intent = Intent(this, LocationAbsenActivity::class.java)
            startActivity(intent)
        }
    }
}
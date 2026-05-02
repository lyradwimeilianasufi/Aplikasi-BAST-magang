package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasibast.databinding.ActivityDetailKehadiranIzinBinding

class DetailKehadiranIzinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailKehadiranIzinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailKehadiranIzinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        // Tombol Kembali
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Tombol Lihat Detail Izin (Teks Oranye di bagian bawah)
        binding.btnLihatDetailIzin.setOnClickListener {
            val intent = Intent(this, DetailPengajuanIzinActivity::class.java)
            startActivity(intent)
        }
        
        // Catatan: Jika Anda ingin menampilkan data dinamis (seperti tanggal yang diklik),
        // Anda bisa mengambil data dari intent.getSerializableExtra/getParcelableExtra di sini.
    }
}

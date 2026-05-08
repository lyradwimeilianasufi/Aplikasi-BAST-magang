package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplikasibast.databinding.ActivityDaftarPengajuanIzinDitolakBinding

class DaftarPengajuanIzinDitolakActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDaftarPengajuanIzinDitolakBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDaftarPengajuanIzinDitolakBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupUI()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnTambahPengajuan.setOnClickListener {
            val intent = Intent(this, PengajuanIzinActivity::class.java)
            startActivity(intent)
        }

        // Navigasi ke halaman Detail Izin Ditolak saat kartu diklik
        binding.itemPengajuanDitolak.cvItemRiwayat.setOnClickListener {
            val intent = Intent(this, DetailIzinDitolakActivity::class.java)
            startActivity(intent)
        }

        // Navigasi Tab
        binding.tabDiajukan.setOnClickListener {
            val intent = Intent(this, DaftarPengajuanIzinActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

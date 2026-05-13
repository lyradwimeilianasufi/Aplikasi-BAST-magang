package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.aplikasibast.databinding.ActivityDaftarPengajuanIzinDiajukanBinding

class DaftarPengajuanIzinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDaftarPengajuanIzinDiajukanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. Aktifkan Edge-to-Edge
        enableEdgeToEdge()
        
        // Menggunakan nama class Binding yang benar (tanpa garis bawah)
        binding = ActivityDaftarPengajuanIzinDiajukanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Perbaiki Header Ketutup (Status Bar) & Tombol Bawah Ketutup (Navigasi)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            // Berikan padding atas pada Toolbar setinggi Status Bar (Jam/Baterai)
            binding.toolbar.updatePadding(top = systemBars.top)
            
            // Berikan padding bawah pada Tombol setinggi Navigasi Bar HP + padding asli (20dp)
            val padding20dp = (20 * resources.displayMetrics.density).toInt()
            binding.btnTambahContainer.updatePadding(bottom = systemBars.bottom + padding20dp)
            
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

        // Navigasi ke halaman Detail Izin Diajukan saat kartu diklik
        binding.cardItem.setOnClickListener {
            val intent = Intent(this, DetailIzinActivity::class.java)
            startActivity(intent)
        }

        // Tab click listeners
        binding.tabDisetujui.setOnClickListener {
            val intent = Intent(this, DaftarPengajuanBaruActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.tabDitolak.setOnClickListener {
            val intent = Intent(this, DaftarPengajuanIzinDitolakActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

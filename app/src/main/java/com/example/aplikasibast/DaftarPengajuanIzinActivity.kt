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
        
        // 1. Aktifkan mode Edge-to-Edge
        enableEdgeToEdge()
        
        binding = ActivityDaftarPengajuanIzinDiajukanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Tangani Insets secara spesifik untuk elemen atas dan bawah
        
        // Atur Toolbar agar tidak tertutup Status Bar (jam, baterai, notch)
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.updatePadding(top = bars.top)
            insets
        }

        // Atur Container Tombol agar terangkat tepat di atas Navigasi Bar HP
        ViewCompat.setOnApplyWindowInsetsListener(binding.btnTambahContainer) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            // Kita ambil padding asli (20dp) dan tambahkan dengan tinggi navigasi bar sistem
            val padding20dp = (20 * resources.displayMetrics.density).toInt()
            v.updatePadding(bottom = bars.bottom + padding20dp)
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

        // Tab click listeners
        binding.tabDitolak.setOnClickListener {
            val intent = Intent(this, DaftarPengajuanIzinDitolakActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

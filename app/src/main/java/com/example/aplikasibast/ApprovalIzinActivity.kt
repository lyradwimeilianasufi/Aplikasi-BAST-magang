package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplikasibast.databinding.ActivityApprovalIzinBinding

class ApprovalIzinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApprovalIzinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityApprovalIzinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUI()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Navigasi ke Halaman Pengajuan Izin Baru
        binding.btnTambahPengajuan.setOnClickListener {
            val intent = Intent(this, PengajuanIzinActivity::class.java)
            startActivity(intent)
        }
        
        // Tab click listeners
        binding.tabPengajuan.setOnClickListener { updateTabs(0) }
        binding.tabDisetujui.setOnClickListener { updateTabs(1) }
        binding.tabDitolak.setOnClickListener { updateTabs(2) }
    }

    private fun updateTabs(position: Int) {
        // Logika untuk berpindah activity berdasarkan tab
        when(position) {
            1 -> {
                startActivity(Intent(this, ApprovalIzinSelesaiActivity::class.java))
                finish()
            }
            2 -> {
                startActivity(Intent(this, ApprovalIzinDitolakActivity::class.java))
                finish()
            }
        }
    }
}

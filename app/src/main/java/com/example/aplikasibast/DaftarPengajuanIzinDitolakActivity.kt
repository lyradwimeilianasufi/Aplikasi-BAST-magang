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

        // Tab click listeners
        binding.tabDiajukan.setOnClickListener {
            val intent = Intent(this, DaftarPengajuanIzinActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.tabDisetujui.setOnClickListener {
            // Logic for "DISETUJUI" tab can be added here
        }

        // Detail click listener for the sample item
        binding.itemPengajuanDitolak.cvItemRiwayat.setOnClickListener {
            val intent = Intent(this, DetailPengajuanDitolakActivity::class.java)
            startActivity(intent)
        }
    }
}

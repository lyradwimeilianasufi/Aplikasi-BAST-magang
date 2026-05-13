package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplikasibast.databinding.ActivityDaftarPengajuanIzinDisetujuiBinding

class DaftarPengajuanBaruActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDaftarPengajuanIzinDisetujuiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDaftarPengajuanIzinDisetujuiBinding.inflate(layoutInflater)
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

        // Navigasi ke halaman Detail Izin Disetujui saat kartu diklik
        binding.cardItem.setOnClickListener {
            val intent = Intent(this, DetailIzinDisetujuiActivity::class.java)
            startActivity(intent)
        }

        // Navigasi Tab
        binding.tabDiajukan.setOnClickListener {
            val intent = Intent(this, DaftarPengajuanIzinActivity::class.java)
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

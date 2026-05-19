package com.example.aplikasibast

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.aplikasibast.databinding.ActivityDetailPengajuanDisetujuiBinding

class DetailPengajuanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPengajuanDisetujuiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Mengaktifkan mode Edge-to-Edge
        enableEdgeToEdge()
        binding = ActivityDetailPengajuanDisetujuiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Menangani insets agar toolbar tidak tertutup status bar/notch/kamera
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { _, insets ->
            // Mengambil insets untuk System Bars DAN Display Cutout (area kamera)
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            
            // Memberikan padding atas pada toolbar sesuai tinggi status bar + notch
            binding.toolbarLayout.updatePadding(top = systemBars.top)
            
            insets
        }

        setupUI()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}

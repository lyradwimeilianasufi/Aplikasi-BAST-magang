package com.example.aplikasibast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasibast.databinding.ActivityDetailKehadiranLiburBinding

class DetailKehadiranLiburActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailKehadiranLiburBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailKehadiranLiburBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        
        // Data can be populated from intent extras if needed
        // val tanggal = intent.getStringExtra("TANGGAL")
        // binding.tvTanggalKerja.text = tanggal
    }
}

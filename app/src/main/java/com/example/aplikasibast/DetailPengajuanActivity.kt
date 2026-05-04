package com.example.aplikasibast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasibast.databinding.ActivityDetailPengajuanBinding

class DetailPengajuanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPengajuanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPengajuanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}

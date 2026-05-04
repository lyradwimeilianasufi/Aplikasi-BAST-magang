package com.example.aplikasibast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasibast.databinding.ActivityDetailPengajuanDitolakBinding

class DetailPengajuanDitolakActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPengajuanDitolakBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPengajuanDitolakBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}

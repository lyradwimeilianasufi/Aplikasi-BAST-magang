package com.example.aplikasibast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasibast.databinding.ActivityDetailKehadiranAlpaBinding

class DetailKehadiranAlpaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailKehadiranAlpaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailKehadiranAlpaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}

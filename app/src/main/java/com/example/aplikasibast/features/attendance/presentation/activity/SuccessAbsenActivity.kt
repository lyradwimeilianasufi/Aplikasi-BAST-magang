package com.example.aplikasibast.features.attendance.presentation.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasibast.databinding.ActivitySuccessAbsenBinding

class SuccessAbsenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySuccessAbsenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessAbsenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val message = intent.getStringExtra("SUCCESS_MESSAGE") ?: "Berhasil"
        binding.tvSuccessMessage.text = message

        // Menutup otomatis setelah 2 detik
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 2000)
    }
}

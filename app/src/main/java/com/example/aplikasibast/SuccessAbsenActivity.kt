package com.example.aplikasibast

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

        // Otomatis menutup popup setelah 2 detik dan kembali ke halaman sebelumnya
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 2000)
        
        // Klik pada area transparan atau popup juga akan menutupnya
        binding.root.setOnClickListener {
            finish()
        }
    }
}
package com.example.aplikasibast

import android.content.Intent
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

        // Otomatis kembali ke beranda setelah 2 detik
        Handler(Looper.getMainLooper()).postDelayed({
            goToMainActivity()
        }, 2000)
        
        // Klik pada area popup juga akan langsung ke beranda
        binding.root.setOnClickListener {
            goToMainActivity()
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        // FLAG_ACTIVITY_CLEAR_TOP akan menghapus semua activity di atas MainActivity (Lokasi, Kamera, Preview)
        // FLAG_ACTIVITY_SINGLE_TOP memastikan MainActivity yang lama dipanggil kembali (tidak buat baru)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
}

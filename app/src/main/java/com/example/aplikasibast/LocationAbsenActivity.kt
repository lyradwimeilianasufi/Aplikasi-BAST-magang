package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplikasibast.databinding.ActivityLocationAbsenBinding

class LocationAbsenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationAbsenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLocationAbsenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Navigasi dari tombol ABSEN MASUK ke CameraAbsenActivity
        binding.btnAbsenMasuk.setOnClickListener {
            val intent = Intent(this, CameraAbsenActivity::class.java)
            startActivity(intent)
        }
    }
}

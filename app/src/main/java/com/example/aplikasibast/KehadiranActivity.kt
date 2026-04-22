package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplikasibast.databinding.ActivityKehadiranBinding

class KehadiranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKehadiranBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityKehadiranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            binding.bottomNavigationInclude.bottomNavigation.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        setupNavigation()
        setupListeners()
    }

    private fun setupListeners() {
        binding.tvLihatSemuaKehadiran.setOnClickListener {
            val intent = Intent(this, RiwayatKehadiranActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupNavigation() {
        binding.bottomNavigationInclude.bottomNavigation.selectedItemId = R.id.nav_kehadiran
        binding.bottomNavigationInclude.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_beranda -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_kehadiran -> true
                R.id.nav_riwayat -> {
                    startActivity(Intent(this, RiwayatKehadiranActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_akun -> true
                else -> false
            }
        }
    }
}

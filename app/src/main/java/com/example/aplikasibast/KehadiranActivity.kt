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

    override fun onResume() {
        super.onResume()
        // Memastikan ikon Kehadiran aktif
        binding.bottomNavigationInclude.bottomNavigation.selectedItemId = R.id.nav_kehadiran
    }

    private fun setupListeners() {
        binding.tvLihatSemuaKehadiran.setOnClickListener {
            val intent = Intent(this, RiwayatKehadiranActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
    }

    private fun setupNavigation() {
        binding.bottomNavigationInclude.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_beranda -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }
                R.id.nav_kehadiran -> true // Tetap di sini
                R.id.nav_riwayat -> {
                    val intent = Intent(this, RiwayatKehadiranActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }
                R.id.nav_akun -> {
                    // startActivity(Intent(this, AkunActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}

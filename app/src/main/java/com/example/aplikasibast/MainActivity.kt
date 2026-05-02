package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.aplikasibast.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top)
            binding.bottomNavigation.updatePadding(bottom = systemBars.bottom)
            insets
        }

        setupUI()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        // Pastikan tab Beranda terpilih saat kembali ke halaman ini
        binding.bottomNavigation.selectedItemId = R.id.nav_beranda
    }

    private fun setupUI() {
        binding.tvUserName.text = viewModel.userName
        binding.tvUserRole.text = viewModel.userRole
        binding.tvCurrentDate.text = viewModel.currentDay
        binding.tvWorkHours.text = viewModel.workHours
    }

    private fun setupListeners() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_beranda -> true
                R.id.nav_kehadiran -> {
                    val intent = Intent(this, KehadiranActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }
                R.id.nav_riwayat -> {
                    val intent = Intent(this, RiwayatKehadiranActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }
                R.id.nav_akun -> {
                    // Navigasi ke halaman Akun jika sudah ada
                    true
                }
                else -> false
            }
        }

        binding.btnAbsenMasukMain.setOnClickListener { navigateToLocationAbsen() }
        binding.btnAbsenKeluarMain.setOnClickListener { navigateToLocationAbsen() }
        binding.btnMenuIzin.setOnClickListener {
            startActivity(Intent(this, RiwayatPengajuanIzinActivity::class.java))
        }
    }

    private fun navigateToLocationAbsen() {
        startActivity(Intent(this, LocationAbsenActivity::class.java))
    }
}

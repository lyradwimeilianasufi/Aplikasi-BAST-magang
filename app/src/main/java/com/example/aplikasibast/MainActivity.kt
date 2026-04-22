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
        
        // Cek apakah baru saja kembali dari absen berhasil
        checkShowSuccessPopup()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        checkShowSuccessPopup()
    }

    private fun checkShowSuccessPopup() {
        if (intent.getBooleanExtra("SHOW_SUCCESS_POPUP", false)) {
            // Tampilkan popup sukses di atas MainActivity
            val successIntent = Intent(this, SuccessAbsenActivity::class.java)
            startActivity(successIntent)
            
            // Hapus flag agar tidak muncul berulang
            intent.removeExtra("SHOW_SUCCESS_POPUP")
        }
    }

    private fun setupUI() {
        binding.tvUserName.text = viewModel.userName
        binding.tvUserRole.text = viewModel.userRole
        binding.tvCurrentDate.text = viewModel.currentDay
        binding.tvWorkHours.text = viewModel.workHours
        binding.bottomNavigation.selectedItemId = R.id.nav_beranda
    }

    private fun setupListeners() {
        binding.btnAbsenMasukMain.setOnClickListener {
            navigateToLocationAbsen()
        }

        binding.icFingerIn.setOnClickListener {
            navigateToLocationAbsen()
        }

        binding.btnAbsenKeluarMain.setOnClickListener {
            navigateToLocationAbsen()
        }
        
        binding.icFingerOut.setOnClickListener {
            navigateToLocationAbsen()
        }

        // Navigasi "Lihat Semua" di bagian Tiket Aktif
        binding.tvLihatSemuaTiket.setOnClickListener {
            // Placeholder: Arahkan ke halaman tiket jika sudah ada
            // startActivity(Intent(this, RiwayatTiketActivity::class.java))
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_beranda -> true
                R.id.nav_kehadiran -> {
                    startActivity(Intent(this, KehadiranActivity::class.java))
                    true
                }
                R.id.nav_riwayat -> {
                    startActivity(Intent(this, RiwayatKehadiranActivity::class.java))
                    true
                }
                R.id.nav_akun -> true
                else -> false
            }
        }
    }

    private fun navigateToLocationAbsen() {
        val intent = Intent(this, LocationAbsenActivity::class.java)
        startActivity(intent)
    }
}

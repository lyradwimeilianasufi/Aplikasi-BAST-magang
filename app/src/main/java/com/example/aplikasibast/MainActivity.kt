package com.example.aplikasibast

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.example.aplikasibast.databinding.ActivityMainBinding
import com.example.aplikasibast.databinding.ActivitySuccessAbsenBinding
import kotlinx.coroutines.launch
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
        observeDashboardState()

        if (intent.getBooleanExtra("SHOW_SUCCESS_DIALOG", false)) {
            showSuccessDialog(intent.getStringExtra("SUCCESS_MESSAGE") ?: "Berhasil")
        }
    }

    private fun setupUI() {
        binding.tvUserName.text = viewModel.userName
        binding.tvCurrentDate.text = viewModel.currentDayUI
        binding.tvWorkHours.text = viewModel.workHours
    }

    private fun observeDashboardState() {
        lifecycleScope.launch {
            viewModel.dashboardState.collect { state ->
                // Update Label Status (Hadir, Izin, Teknisi, dll)
                binding.tvUserRole.text = state.currentStatus
                
                // Update Info Absensi
                val kehadiran = state.kehadiran
                if (kehadiran == null) {
                    binding.tvInTime.text = "-"
                    binding.tvOutTime.text = "-"
                    updateButtonState(true) // Tombol Masuk Aktif
                } else {
                    binding.tvInTime.text = kehadiran.jamMasuk
                    binding.tvOutTime.text = kehadiran.jamKeluar
                    
                    if (kehadiran.jamKeluar == "-") {
                        updateButtonState(false) // Tombol Keluar Aktif
                    } else {
                        disableAllButtons() // Sudah lengkap
                    }
                }
            }
        }
    }

    private fun updateButtonState(isMasuk: Boolean) {
        // Development mode: Tombol dibuat selalu aktif agar mudah ditest
        binding.btnAbsenMasukMain.isEnabled = true
        binding.btnAbsenMasukMain.alpha = if (isMasuk) 1.0f else 0.5f
        
        binding.btnAbsenKeluarMain.isEnabled = true
        binding.btnAbsenKeluarMain.alpha = if (!isMasuk) 1.0f else 0.5f
        
        binding.icFingerOut.alpha = if (!isMasuk) 1.0f else 0.4f
    }

    private fun disableAllButtons() {
        binding.btnAbsenMasukMain.isEnabled = true // Tetap aktif untuk development
        binding.btnAbsenKeluarMain.isEnabled = true
        binding.btnAbsenMasukMain.alpha = 0.5f
        binding.btnAbsenKeluarMain.alpha = 0.5f
    }

    private fun setupListeners() {
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
                else -> false
            }
        }

        binding.btnAbsenMasukMain.setOnClickListener { navigateToAbsen(true) }
        binding.btnAbsenKeluarMain.setOnClickListener { navigateToAbsen(false) }
        
        binding.btnMenuIzin.setOnClickListener {
            startActivity(Intent(this, DaftarPengajuanIzinActivity::class.java))
        }
    }

    private fun navigateToAbsen(isMasuk: Boolean) {
        val intent = Intent(this, LocationAbsenActivity::class.java)
        intent.putExtra("IS_MASUK", isMasuk)
        startActivity(intent)
    }

    private fun showSuccessDialog(message: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = ActivitySuccessAbsenBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.tvSuccessMessage.text = message
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed({ if (dialog.isShowing) dialog.dismiss() }, 2000)
    }
}

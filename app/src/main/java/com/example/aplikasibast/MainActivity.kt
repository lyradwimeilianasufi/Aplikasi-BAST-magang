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

    override fun onResume() {
        super.onResume()
        // Memastikan tab Beranda terpilih saat kembali ke activity ini
        binding.bottomNavigation.selectedItemId = R.id.nav_beranda
    }

    private fun setupUI() {
        binding.tvUserName.text = viewModel.userName
        binding.tvUserRole.text = "Teknisi" // Berdasarkan permintaan: Tampilkan "Teknisi" di bawah nama
        binding.tvCurrentDate.text = viewModel.currentDayUI
        binding.tvWorkHours.text = viewModel.workHours
    }

    private fun observeDashboardState() {
        lifecycleScope.launch {
            viewModel.dashboardState.collect { state ->
                val kehadiran = state.kehadiran
                if (kehadiran == null) {
                    binding.tvInTime.text = "-"
                    binding.tvOutTime.text = "-"
                    updateButtonState(true)
                } else {
                    binding.tvInTime.text = kehadiran.jamMasuk
                    binding.tvOutTime.text = kehadiran.jamKeluar
                    
                    if (kehadiran.jamKeluar == "-") {
                        updateButtonState(false)
                    } else {
                        disableAllButtons()
                    }
                }
            }
        }
    }

    private fun updateButtonState(isMasuk: Boolean) {
        binding.btnAbsenMasukMain.isEnabled = true
        binding.btnAbsenMasukMain.alpha = if (isMasuk) 1.0f else 0.5f
        
        binding.btnAbsenKeluarMain.isEnabled = true
        binding.btnAbsenKeluarMain.alpha = if (!isMasuk) 1.0f else 0.5f
        
        binding.icFingerOut.alpha = if (!isMasuk) 1.0f else 0.4f
    }

    private fun disableAllButtons() {
        binding.btnAbsenMasukMain.isEnabled = true
        binding.btnAbsenKeluarMain.isEnabled = true
        binding.btnAbsenMasukMain.alpha = 0.5f
        binding.btnAbsenKeluarMain.alpha = 0.5f
    }

    private fun setupListeners() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            // Menggunakan helper agar konsisten dengan activity lain
            NavigationHelper.handleBottomNavigation(this, item.itemId)
        }

        binding.btnAbsenMasukMain.setOnClickListener { navigateToAbsen(true) }
        binding.btnAbsenKeluarMain.setOnClickListener { navigateToAbsen(false) }
        
        binding.btnMenuIzin.setOnClickListener {
            startActivity(Intent(this, DaftarPengajuanActivity::class.java))
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

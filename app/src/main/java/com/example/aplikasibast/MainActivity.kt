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
        observeViewModel()

        if (intent.getBooleanExtra("SHOW_SUCCESS_DIALOG", false)) {
            showSuccessDialog()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.todayKehadiran.collect { kehadiran ->
                updateAttendanceUI(kehadiran)
            }
        }
    }

    private fun updateAttendanceUI(kehadiran: KehadiranEntity?) {
        // UNTUK DEVELOPMENT: Tombol dibuat selalu aktif agar bisa absen berkali-kali
        binding.btnAbsenMasukMain.isEnabled = true
        binding.btnAbsenMasukMain.alpha = 1.0f
        binding.btnAbsenKeluarMain.isEnabled = true
        binding.btnAbsenKeluarMain.alpha = 1.0f
        binding.icFingerOut.alpha = 1.0f

        if (kehadiran == null) {
            binding.tvInTime.text = "-"
            binding.tvOutTime.text = "-"
        } else {
            binding.tvInTime.text = kehadiran.jamMasuk
            binding.tvOutTime.text = kehadiran.jamKeluar
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra("SHOW_SUCCESS_DIALOG", false)) {
            showSuccessDialog()
        }
    }

    override fun onResume() {
        super.onResume()
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
                R.id.nav_akun -> true
                else -> false
            }
        }

        binding.btnAbsenMasukMain.setOnClickListener { navigateToLocationAbsen(true) }
        binding.btnAbsenKeluarMain.setOnClickListener { navigateToLocationAbsen(false) }
        binding.btnMenuIzin.setOnClickListener {
            startActivity(Intent(this, RiwayatPengajuanIzinActivity::class.java))
        }
    }

    private fun navigateToLocationAbsen(isMasuk: Boolean) {
        val intent = Intent(this, LocationAbsenActivity::class.java)
        intent.putExtra("IS_MASUK", isMasuk)
        startActivity(intent)
    }

    private fun showSuccessDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = ActivitySuccessAbsenBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        dialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }, 2000)

        dialogBinding.root.setOnClickListener {
            dialog.dismiss()
        }
    }
}

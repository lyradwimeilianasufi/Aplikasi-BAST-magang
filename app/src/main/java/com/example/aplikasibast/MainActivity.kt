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
import com.example.aplikasibast.databinding.ActivityMainBinding
import com.example.aplikasibast.databinding.ActivitySuccessAbsenBinding
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

        // Cek jika harus menampilkan popup sukses
        if (intent.getBooleanExtra("SHOW_SUCCESS_DIALOG", false)) {
            showSuccessDialog()
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

    private fun showSuccessDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = ActivitySuccessAbsenBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        // Membuat background dialog transparan agar overlay di XML bekerja
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        
        // Mengatur lebar dialog agar memenuhi layar (overlay)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        dialog.show()

        // Otomatis tutup dialog setelah 2 detik
        Handler(Looper.getMainLooper()).postDelayed({
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }, 2000)

        // Klik pada dialog untuk menutup
        dialogBinding.root.setOnClickListener {
            dialog.dismiss()
        }
    }
}

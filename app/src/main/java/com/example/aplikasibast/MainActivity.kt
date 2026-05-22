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
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.example.aplikasibast.databinding.ActivityMainBinding
import com.example.aplikasibast.databinding.ActivitySuccessAbsenBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

        handleSuccessIntent(intent)
    }

    private fun handleSuccessIntent(intent: Intent?) {
        if (intent?.getBooleanExtra("SHOW_SUCCESS_DIALOG", false) == true) {
            val message = intent.getStringExtra("SUCCESS_MESSAGE") ?: "Absen Masuk Berhasil"
            showSuccessDialog(message)
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
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val isWeekend = dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY

        if (kehadiran == null) {
            binding.tvInTime.text = "-"
            binding.tvOutTime.text = "-"
            
            if (isWeekend) {
                binding.tvUserRole.text = "Libur"
            } else {
                checkOtherStatuses()
            }
            
            binding.btnAbsenMasukMain.isEnabled = !isWeekend
            binding.btnAbsenMasukMain.alpha = if (isWeekend) 0.5f else 1.0f
            binding.btnAbsenKeluarMain.isEnabled = false
            binding.btnAbsenKeluarMain.alpha = 0.5f
            binding.icFingerOut.alpha = 0.4f
        } else {
            binding.tvInTime.text = kehadiran.jamMasuk
            binding.tvOutTime.text = kehadiran.jamKeluar
            binding.tvUserRole.text = kehadiran.status

            if (kehadiran.jamKeluar == "-") {
                binding.btnAbsenMasukMain.isEnabled = false
                binding.btnAbsenMasukMain.alpha = 0.5f
                binding.btnAbsenKeluarMain.isEnabled = true
                binding.btnAbsenKeluarMain.alpha = 1.0f
                binding.icFingerOut.alpha = 1.0f
            } else {
                binding.btnAbsenMasukMain.isEnabled = false
                binding.btnAbsenMasukMain.alpha = 0.5f
                binding.btnAbsenKeluarMain.isEnabled = false
                binding.btnAbsenKeluarMain.alpha = 0.5f
                binding.icFingerOut.alpha = 0.4f
            }
        }
    }

    private fun checkOtherStatuses() {
        lifecycleScope.launch {
            val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
            val approvedIzin = viewModel.getPengajuanByStatus("Disetujui").first()
            val isIzin = approvedIzin.any { it.tanggalMulai <= today && it.tanggalSelesai >= today }
            
            if (isIzin) {
                binding.tvUserRole.text = "Izin"
            } else {
                val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                if (hour >= 17) {
                    binding.tvUserRole.text = "Alpa"
                } else {
                    binding.tvUserRole.text = viewModel.userRole
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleSuccessIntent(intent)
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
        if (!isMasuk) {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            if (hour < 17) {
                Toast.makeText(this, "Belum saatnya absen keluar. Awal absen keluar jam 17:00", Toast.LENGTH_SHORT).show()
                return
            }
        }
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

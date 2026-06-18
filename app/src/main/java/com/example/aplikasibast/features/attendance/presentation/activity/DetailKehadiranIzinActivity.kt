package com.example.aplikasibast.features.attendance.presentation.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.example.aplikasibast.core.constants.AppConstants
import com.example.aplikasibast.core.utils.DateUtils
import com.example.aplikasibast.databinding.ActivityDetailKehadiranIzinBinding
import com.example.aplikasibast.features.attendance.presentation.viewmodel.AttendanceViewModel
import com.example.aplikasibast.features.permission.presentation.activity.DetailIzinDisetujuiActivity
import com.example.aplikasibast.features.permission.presentation.viewmodel.PermissionViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailKehadiranIzinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailKehadiranIzinBinding
    private val viewModel: AttendanceViewModel by viewModel()
    private val permissionViewModel: PermissionViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailKehadiranIzinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val kehadiranId = intent.getIntExtra("KEHADIRAN_ID", -1)
        val initialTanggal = intent.getStringExtra("TANGGAL")

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbarLayout.updatePadding(top = systemBars.top)
            insets
        }

        // 1. SET TANGGAL LANGSUNG DARI INTENT (SOLUSI UTAMA)
        // Ini memastikan tanggal berubah seketika saat item diklik
        initialTanggal?.let {
            binding.tvTanggalKerja.text = DateUtils.formatToUi(it)
        }
        
        // Set Jam Kerja Default
        binding.tvJamKerja.text = viewModel.workHours

        // Set badge Izin default
        binding.tvStatusBadge.text = "Izin"
        binding.tvStatusBadge.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F39C12"))
        binding.tvStatusBadge.setTextColor(Color.WHITE)

        if (initialTanggal != null) {
            loadPermissionData(initialTanggal)
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun loadPermissionData(tanggal: String) {
        lifecycleScope.launch {
            // Mencari data pengajuan yang disetujui berdasarkan tanggal ini
            val approvedPermissions = permissionViewModel.getPengajuanByStatus(AppConstants.STATUS_DISETUJUI).first()
            val matchedPermission = approvedPermissions.find { 
                val dates = generateDatesInRange(it.tanggalMulai, it.tanggalSelesai)
                dates.contains(tanggal)
            }

            if (matchedPermission != null) {
                binding.btnLihatDetailIzin.visibility = View.VISIBLE
                binding.btnLihatDetailIzin.text = "Lihat Detail Izin"
                binding.btnLihatDetailIzin.setOnClickListener {
                    val intent = Intent(this@DetailKehadiranIzinActivity, DetailIzinDisetujuiActivity::class.java)
                    intent.putExtra("PENGAJUAN_ID", matchedPermission.id) 
                    startActivity(intent)
                }
            } else {
                binding.btnLihatDetailIzin.visibility = View.GONE
            }
        }
    }

    private fun generateDatesInRange(start: String, end: String): List<String> {
        val dates = mutableListOf<String>()
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        try {
            val startDate = sdf.parse(start)
            val endDate = sdf.parse(end)
            if (startDate != null && endDate != null) {
                val cal = java.util.Calendar.getInstance().apply { time = startDate }
                while (!cal.time.after(endDate)) {
                    dates.add(sdf.format(cal.time))
                    cal.add(java.util.Calendar.DATE, 1)
                }
            }
        } catch (e: Exception) {}
        return dates
    }
}

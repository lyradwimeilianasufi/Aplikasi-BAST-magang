package com.example.aplikasibast.features.attendance.presentation.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.example.aplikasibast.MainViewModel
import com.example.aplikasibast.R
import com.example.aplikasibast.core.utils.DateUtils
import com.example.aplikasibast.databinding.ActivityDetailKehadiranHadirBinding
import com.example.aplikasibast.features.attendance.domain.model.Kehadiran
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class DetailKehadiranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailKehadiranHadirBinding
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailKehadiranHadirBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val kehadiranId = intent.getIntExtra("KEHADIRAN_ID", -1)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbarLayout.updatePadding(top = systemBars.top)
            insets
        }

        if (kehadiranId != -1) {
            loadData(kehadiranId)
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun loadData(id: Int) {
        lifecycleScope.launch {
            val data = viewModel.getKehadiranById(id)
            data?.let {
                binding.tvTanggalKerja.text = DateUtils.formatToUi(it.tanggal)
                binding.tvJamKerja.text = viewModel.workHours
                binding.tvTotalJamKerja.text = it.totalJam
                
                setupStatusUI(it.status)

                if (it.status == "Alpa" || it.status == "Libur") {
                    hideAttendanceSections()
                } else {
                    showAttendanceDetails(it)
                }
            }
        }
    }

    private fun setupStatusUI(status: String) {
        binding.tvStatusBadge.text = status
        val colorRes = when (status) {
            "Hadir" -> R.color.green_badge_bg
            "Telat" -> R.color.yellow_badge_bg
            "Izin" -> R.color.purple_badge 
            "Alpa" -> R.color.red_badge_bg
            else -> R.color.gray_light
        }
        
        val textColorRes = when (status) {
            "Hadir" -> R.color.green_badge_text
            "Telat" -> R.color.yellow_badge_text
            "Izin" -> R.color.purple_badge_text
            "Alpa" -> R.color.red_badge_text
            else -> R.color.black
        }
        
        binding.tvStatusBadge.backgroundTintList = ContextCompat.getColorStateList(this, colorRes)
        binding.tvStatusBadge.setTextColor(ContextCompat.getColor(this, textColorRes))
    }

    private fun showAttendanceDetails(it: Kehadiran) {
        binding.tvWaktuMasuk.text = it.jamMasuk
        binding.tvWaktuKeluar.text = it.jamKeluar
        
        it.fotoMasukPath?.let { path ->
            val file = File(path)
            if (file.exists()) {
                binding.ivFotoMasuk.setImageURI(Uri.fromFile(file))
                binding.ivFotoMasuk.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            }
        }

        it.fotoKeluarPath?.let { path ->
            val file = File(path)
            if (file.exists()) {
                binding.ivFotoKeluar.setImageURI(Uri.fromFile(file))
                binding.ivFotoKeluar.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            }
        }

        binding.btnLihatLokasiMasuk.setOnClickListener {
            navigateToMap("Lokasi Masuk", it.jamMasuk, it.latMasuk, it.lngMasuk, it.lokasiMasuk)
        }

        if (it.jamKeluar != "-") {
            binding.btnLihatLokasiKeluar.setOnClickListener {
                navigateToMap("Lokasi Keluar", it.jamKeluar, it.latKeluar, it.lngKeluar, it.lokasiKeluar)
            }
        } else {
            binding.btnLihatLokasiKeluar.visibility = View.INVISIBLE
        }
    }

    private fun hideAttendanceSections() {
        binding.layoutFotoMasuk.visibility = View.GONE
        binding.layoutFotoKeluar.visibility = View.GONE
        binding.btnLihatLokasiMasuk.visibility = View.GONE
        binding.btnLihatLokasiKeluar.visibility = View.GONE
        binding.tvTotalJamKerja.text = "-"
    }

    private fun navigateToMap(title: String, time: String, lat: Double?, lng: Double?, address: String?) {
        if (lat != null && lng != null) {
            val intent = Intent(this, LocationAbsenActivity::class.java).apply {
                putExtra("IS_VIEW_ONLY", true)
                putExtra("TITLE_TO_VIEW", title)
                putExtra("TIME_TO_VIEW", time)
                putExtra("LAT_TO_VIEW", lat)
                putExtra("LNG_TO_VIEW", lng)
                putExtra("ADDRESS_TO_VIEW", address)
            }
            startActivity(intent)
        }
    }
}

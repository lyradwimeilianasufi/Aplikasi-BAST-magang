package com.example.aplikasibast

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.example.aplikasibast.databinding.ActivityDetailKehadiranHadirBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class DetailKehadiranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailKehadiranHadirBinding
    private val viewModel: MainViewModel by viewModel()
    
    private var lokasiMasuk: String? = null
    private var lokasiKeluar: String? = null
    private var latMasuk: Double? = null
    private var lngMasuk: Double? = null
    private var latKeluar: Double? = null
    private var lngKeluar: Double? = null
    private var jamMasuk: String? = null
    private var jamKeluar: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailKehadiranHadirBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbarLayout) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = systemBars.top)
            insets
        }

        val kehadiranId = intent.getIntExtra("KEHADIRAN_ID", -1)
        if (kehadiranId != -1) {
            loadDetailData(kehadiranId)
        }

        setupUI()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnLihatLokasiMasuk.setOnClickListener {
            lokasiMasuk?.let { address ->
                navigateToViewLocation(address, "Lokasi Absen Masuk", jamMasuk, latMasuk, lngMasuk)
            }
        }

        binding.btnLihatLokasiKeluar.setOnClickListener {
            lokasiKeluar?.let { address ->
                navigateToViewLocation(address, "Lokasi Absen Keluar", jamKeluar, latKeluar, lngKeluar)
            }
        }
    }

    private fun navigateToViewLocation(address: String, title: String, time: String?, lat: Double?, lng: Double?) {
        val intent = Intent(this, LocationAbsenActivity::class.java)
        intent.putExtra("IS_VIEW_ONLY", true)
        intent.putExtra("ADDRESS_TO_VIEW", address)
        intent.putExtra("TITLE_TO_VIEW", title)
        intent.putExtra("TIME_TO_VIEW", time)
        if (lat != null && lng != null) {
            intent.putExtra("LAT_TO_VIEW", lat)
            intent.putExtra("LNG_TO_VIEW", lng)
        }
        startActivity(intent)
    }

    private fun loadDetailData(id: Int) {
        lifecycleScope.launch {
            val data = viewModel.getKehadiranById(id)
            data?.let { kehadiran ->
                lokasiMasuk = kehadiran.lokasiMasuk
                lokasiKeluar = kehadiran.lokasiKeluar
                latMasuk = kehadiran.latMasuk
                lngMasuk = kehadiran.lngMasuk
                latKeluar = kehadiran.latKeluar
                lngKeluar = kehadiran.lngKeluar
                jamMasuk = kehadiran.jamMasuk
                jamKeluar = kehadiran.jamKeluar

                binding.tvTanggalKerja.text = kehadiran.tanggal
                binding.tvWaktuMasuk.text = kehadiran.jamMasuk
                binding.tvWaktuKeluar.text = kehadiran.jamKeluar
                binding.tvTotalJamKerja.text = kehadiran.totalJam
                binding.tvStatusBadge.text = kehadiran.status
                
                // 1. Tampilkan Foto Absen Masuk
                kehadiran.fotoMasukPath?.let { path ->
                    val file = File(path)
                    if (file.exists()) {
                        binding.ivFotoMasuk.setImageURI(null)
                        binding.ivFotoMasuk.setImageURI(Uri.fromFile(file))
                    }
                }

                // 2. Tampilkan Foto Absen Keluar
                if (kehadiran.jamKeluar != "-") {
                    kehadiran.fotoKeluarPath?.let { path ->
                        val file = File(path)
                        if (file.exists()) {
                            binding.ivFotoKeluar.setImageURI(null)
                            binding.ivFotoKeluar.setImageURI(Uri.fromFile(file))
                        }
                    }
                }
            }
        }
    }
}

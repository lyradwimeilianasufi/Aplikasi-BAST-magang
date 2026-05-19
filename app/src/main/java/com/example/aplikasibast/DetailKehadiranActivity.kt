package com.example.aplikasibast

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.aplikasibast.databinding.ActivityDetailKehadiranHadirBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class DetailKehadiranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailKehadiranHadirBinding
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailKehadiranHadirBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            val intent = Intent(this, LocationAbsenActivity::class.java)
            startActivity(intent)
        }

        binding.btnLihatLokasiKeluar.setOnClickListener {
            val intent = Intent(this, LocationAbsenActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadDetailData(id: Int) {
        lifecycleScope.launch {
            val data = viewModel.getKehadiranById(id)
            data?.let { kehadiran ->
                binding.tvTanggalKerja.text = kehadiran.tanggal
                binding.tvWaktuMasuk.text = kehadiran.jamMasuk
                binding.tvWaktuKeluar.text = kehadiran.jamKeluar
                binding.tvTotalJamKerja.text = kehadiran.totalJam
                binding.tvStatusBadge.text = kehadiran.status
                
                // 1. Tampilkan Foto Absen Masuk
                kehadiran.fotoMasukPath?.let { path ->
                    val file = File(path)
                    if (file.exists()) {
                        binding.ivFotoMasuk.setImageURI(null) // Reset cache
                        binding.ivFotoMasuk.setImageURI(Uri.fromFile(file))
                    }
                }

                // 2. Tampilkan Foto Absen Keluar (Pastikan jamKeluar bukan default "-")
                if (kehadiran.jamKeluar != "-") {
                    kehadiran.fotoKeluarPath?.let { path ->
                        val file = File(path)
                        if (file.exists()) {
                            binding.ivFotoKeluar.setImageURI(null) // Reset cache
                            binding.ivFotoKeluar.setImageURI(Uri.fromFile(file))
                        }
                    }
                }
            }
        }
    }
}

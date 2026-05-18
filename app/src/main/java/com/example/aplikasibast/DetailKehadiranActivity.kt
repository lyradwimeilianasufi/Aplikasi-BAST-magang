package com.example.aplikasibast

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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

        // Menggunakan getIntExtra bawaan Android
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
            // Memanggil fungsi getKehadiranById yang sudah kita tambahkan di MainViewModel
            val data = viewModel.getKehadiranById(id)
            data?.let {
                binding.tvTanggalKerja.text = it.tanggal
                binding.tvWaktuMasuk.text = it.jamMasuk
                binding.tvWaktuKeluar.text = it.jamKeluar
                binding.tvTotalJamKerja.text = it.totalJam
                binding.tvStatusBadge.text = it.status
                
                // Tampilkan foto jika ada
                it.fotoPath?.let { path ->
                    val file = File(path)
                    if (file.exists()) {
                        binding.ivFotoMasuk.setImageURI(Uri.fromFile(file))
                    }
                }
            }
        }
    }
}

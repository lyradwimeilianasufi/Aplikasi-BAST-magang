package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.aplikasibast.databinding.ActivityDetailKehadiranIzinBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailKehadiranIzinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailKehadiranIzinBinding
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailKehadiranIzinBinding.inflate(layoutInflater)
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

        binding.btnLihatDetailIzin.setOnClickListener {
            val intent = Intent(this, DetailPengajuanIzinActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadDetailData(id: Int) {
        lifecycleScope.launch {
            val data = viewModel.getKehadiranById(id)
            data?.let {
                binding.tvTanggalKerja.text = it.tanggal
                binding.tvStatusBadge.text = it.status
                // Karena ini izin, jam biasanya kosong atau strip
                binding.tvTotalJamKerja.text = "-"
            }
        }
    }
}

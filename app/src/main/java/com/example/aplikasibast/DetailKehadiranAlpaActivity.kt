package com.example.aplikasibast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.aplikasibast.databinding.ActivityDetailKehadiranAlpaBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailKehadiranAlpaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailKehadiranAlpaBinding
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailKehadiranAlpaBinding.inflate(layoutInflater)
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
    }

    private fun loadDetailData(id: Int) {
        lifecycleScope.launch {
            val data = viewModel.getKehadiranById(id)
            data?.let {
                binding.tvTanggalKerja.text = it.tanggal
                binding.tvStatusBadge.text = it.status
                // Karena Alpa, jam biasanya kosong
                binding.tvTotalJamKerja.text = "-"
                binding.tvWaktuMasuk.text = "-"
                binding.tvWaktuKeluar.text = "-"
            }
        }
    }
}

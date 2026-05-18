package com.example.aplikasibast

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.aplikasibast.databinding.ActivityDetailIzinDisetujuiBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailIzinDisetujuiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailIzinDisetujuiBinding
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailIzinDisetujuiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ambil ID yang dikirim dari list
        val permitId = intent.getIntExtra("PERMIT_ID", -1)
        if (permitId != -1) {
            loadPermitDetail(permitId)
        }

        setupUI()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadPermitDetail(id: Int) {
        lifecycleScope.launch {
            // Ambil data spesifik dari DB berdasarkan ID
            // Catatan: Pastikan Anda menambahkan fungsi getPengajuanById di MainViewModel/Repository
            // Untuk sementara kita cari dari list pengajuan
            viewModel.getPengajuanByStatus("DISETUJUI").collect { list ->
                val permit = list.find { it.id == id }
                permit?.let {
                    binding.tvTanggalPengajuan.text = it.tanggalPengajuan
                    binding.tvJenisIzin.text = it.jenisIzin
                    binding.tvPeriodeIzin.text = "${it.tanggalMulai} - ${it.tanggalSelesai}"
                    binding.tvAlasan.text = it.alasan
                    // Anda bisa menambahkan mapping data lainnya di sini
                }
            }
        }
    }
}

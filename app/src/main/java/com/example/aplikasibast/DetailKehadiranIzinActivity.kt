package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.example.aplikasibast.databinding.ActivityDetailKehadiranIzinBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailKehadiranIzinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailKehadiranIzinBinding
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Aktifkan mode Edge-to-Edge
        enableEdgeToEdge()
        binding = ActivityDetailKehadiranIzinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Tangani insets agar Toolbar tidak tertutup Status Bar/Notch
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

        binding.btnLihatDetailIzin.setOnClickListener {
            val intent = Intent(this, DetailPengajuanIzinActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadDetailData(id: Int) {
        lifecycleScope.launch {
            val data = viewModel.getKehadiranById(id)
            data?.let {
                // PERBAIKAN: Gunakan format UI untuk tanggal
                binding.tvTanggalKerja.text = DateUtils.formatToUi(it.tanggal)
                binding.tvStatusBadge.text = it.status
                binding.tvTotalJamKerja.text = "-"
            }
        }
    }
}

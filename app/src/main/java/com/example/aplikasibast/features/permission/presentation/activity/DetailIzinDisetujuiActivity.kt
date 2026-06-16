package com.example.aplikasibast.features.permission.presentation.activity

import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.example.aplikasibast.R
import com.example.aplikasibast.core.utils.DateUtils
import com.example.aplikasibast.databinding.ActivityDetailIzinDisetujuiBinding
import com.example.aplikasibast.features.permission.presentation.viewmodel.PermissionViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class DetailIzinDisetujuiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailIzinDisetujuiBinding
    private val viewModel: PermissionViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailIzinDisetujuiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbarLayout.updatePadding(top = systemBars.top)
            insets
        }

        val pengajuanId = intent.getIntExtra("PENGAJUAN_ID", -1)
        if (pengajuanId != -1) {
            loadData(pengajuanId)
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun loadData(id: Int) {
        lifecycleScope.launch {
            val data = viewModel.getPengajuanById(id)
            data?.let {
                binding.tvTanggalPengajuan.text = DateUtils.formatToUi(it.tanggalPengajuan)
                binding.tvTanggalDiproses.text = it.tanggalDiproses?.let { tgl -> DateUtils.formatToUi(tgl) } ?: "-"
                binding.tvJenisIzin.text = it.jenisIzin
                binding.tvPeriodeIzin.text = "${DateUtils.formatToUi(it.tanggalMulai)} - ${DateUtils.formatToUi(it.tanggalSelesai)}"
                binding.tvAlasan.text = it.alasan
                binding.tvJumlahHari.text = "${DateUtils.calculateDays(it.tanggalMulai, it.tanggalSelesai)} Hari"

                it.lampiranPath?.let { path ->
                    val file = File(path)
                    if (file.exists()) {
                        binding.ivFilePendukung.setImageURI(Uri.fromFile(file))
                        binding.ivFilePendukung.alpha = 1.0f
                    }
                }
            }
        }
    }
}

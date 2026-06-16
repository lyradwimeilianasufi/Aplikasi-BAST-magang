package com.example.aplikasibast.features.permission.presentation.activity

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
import com.example.aplikasibast.R
import com.example.aplikasibast.core.constants.AppConstants
import com.example.aplikasibast.core.utils.DateUtils
import com.example.aplikasibast.databinding.ActivityDetailIzinBinding
import com.example.aplikasibast.features.permission.presentation.viewmodel.PermissionViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class DetailIzinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailIzinBinding
    private val viewModel: PermissionViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailIzinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pengajuanId = intent.getIntExtra("PENGAJUAN_ID", -1)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbarLayout.updatePadding(top = systemBars.top)
            insets
        }

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
                setupStatusBadge(it.status)

                if (it.status != AppConstants.STATUS_DIAJUKAN) {
                    binding.cardProcessing.visibility = View.VISIBLE
                    binding.tvTanggalDiproses.text = it.tanggalDiproses?.let { tgl -> DateUtils.formatToUi(tgl) } ?: "-"
                    
                    if (it.status == AppConstants.STATUS_DITOLAK) {
                        binding.layoutRejection.visibility = View.VISIBLE
                        binding.tvAlasanPenolakan.text = it.alasanPenolakan ?: "Tidak ada alasan spesifik"
                    }
                }

                binding.tvJenisIzin.text = it.jenisIzin
                binding.tvPeriodeIzin.text = "${DateUtils.formatToUi(it.tanggalMulai)} - ${DateUtils.formatToUi(it.tanggalSelesai)}"
                binding.tvAlasan.text = it.alasan
                binding.tvJumlahHari.text = "${DateUtils.calculateDays(it.tanggalMulai, it.tanggalSelesai)} Hari"

                it.lampiranPath?.let { path ->
                    val file = File(path)
                    if (file.exists()) {
                        binding.ivFilePendukung.setImageURI(Uri.fromFile(file))
                        binding.ivFilePendukung.alpha = 1.0f
                        binding.ivFilePendukung.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                    }
                }
            }
        }
    }

    private fun setupStatusBadge(status: String) {
        binding.tvStatusBadge.text = status
        when (status) {
            AppConstants.STATUS_DISETUJUI -> {
                binding.tvStatusBadge.backgroundTintList = ContextCompat.getColorStateList(this, R.color.green_badge_bg)
                binding.tvStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.green_badge_text))
            }
            AppConstants.STATUS_DITOLAK -> {
                binding.tvStatusBadge.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red_badge_bg)
                binding.tvStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.red_badge_text))
            }
            else -> {
                binding.tvStatusBadge.backgroundTintList = ContextCompat.getColorStateList(this, R.color.yellow_badge_bg)
                binding.tvStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.yellow_badge_text))
            }
        }
    }
}

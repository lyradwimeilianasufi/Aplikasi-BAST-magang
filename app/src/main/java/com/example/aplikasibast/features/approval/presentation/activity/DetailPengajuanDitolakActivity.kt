package com.example.aplikasibast.features.approval.presentation.activity

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
import com.example.aplikasibast.core.utils.DateUtils
import com.example.aplikasibast.databinding.ActivityDetailPengajuanDitolakBinding
import com.example.aplikasibast.features.approval.presentation.viewmodel.ApprovalViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class DetailPengajuanDitolakActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPengajuanDitolakBinding
    private val viewModel: ApprovalViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailPengajuanDitolakBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
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
                binding.tvNamaTeknisi.text = it.teknisiNama
                binding.tvJenisIzin.text = it.jenisIzin
                binding.tvPeriodeIzin.text = "${DateUtils.formatToUi(it.tanggalMulai)} - ${DateUtils.formatToUi(it.tanggalSelesai)}"
                binding.tvAlasan.text = it.alasan
                binding.tvTanggalPengajuan.text = DateUtils.formatToUi(it.tanggalPengajuan)
                binding.tvTanggalDiproses.text = DateUtils.formatToUi(it.tanggalDiproses)
                binding.tvAlasanPenolakan.text = it.alasanPenolakan ?: "Tidak ada alasan spesifik"
                binding.tvJumlahHari.text = "${DateUtils.calculateDays(it.tanggalMulai, it.tanggalSelesai)} Hari"

                // Atur warna dan tampilkan badge di sini untuk mencegah flicker warna hijau default
                binding.tvStatusBadge.text = it.status
                binding.tvStatusBadge.backgroundTintList = ContextCompat.getColorStateList(this@DetailPengajuanDitolakActivity, R.color.red_badge_bg)
                binding.tvStatusBadge.setTextColor(ContextCompat.getColor(this@DetailPengajuanDitolakActivity, R.color.red_badge_text))
                binding.tvStatusBadge.visibility = View.VISIBLE

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
}

package com.example.aplikasibast.features.approval.presentation.activity

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.aplikasibast.R
import com.example.aplikasibast.features.approval.presentation.viewmodel.ApprovalViewModel
import com.example.aplikasibast.core.constants.AppConstants
import com.example.aplikasibast.core.utils.DateUtils
import com.example.aplikasibast.databinding.ActivityDetailPengajuanDiajukanBinding
import com.example.aplikasibast.databinding.DialogTolakPengajuanBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class DetailPengajuanIzinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPengajuanDiajukanBinding
    private val viewModel: ApprovalViewModel by viewModel()
    private var pengajuanId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailPengajuanDiajukanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pengajuanId = intent.getIntExtra("PENGAJUAN_ID", -1)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadData()
        setupUI()
    }

    private fun loadData() {
        if (pengajuanId != -1) {
            lifecycleScope.launch {
                val data = viewModel.getPengajuanById(pengajuanId)
                data?.let {
                    binding.tvNamaTeknisi.text = it.teknisiNama
                    binding.tvJenisIzin.text = it.jenisIzin
                    binding.tvPeriodeIzin.text = "${DateUtils.formatToUi(it.tanggalMulai)} - ${DateUtils.formatToUi(it.tanggalSelesai)}"
                    binding.tvAlasan.text = it.alasan
                    binding.tvTanggalPengajuan.text = DateUtils.formatToUi(it.tanggalPengajuan)
                    binding.tvJumlahHari.text = "${DateUtils.calculateDays(it.tanggalMulai, it.tanggalSelesai)} Hari"
                    
                    setupStatusBadge(it.status)

                    // Jika status masih diajukan, tampilkan tombol aksi, jika sudah diproses sembunyikan
                    if (it.status == AppConstants.STATUS_DIAJUKAN) {
                        binding.layoutButtons.visibility = View.VISIBLE
                    } else {
                        binding.layoutButtons.visibility = View.GONE
                    }

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

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnTolak.setOnClickListener { showTolakDialog() }
        binding.btnSetujui.setOnClickListener { updateStatus(AppConstants.STATUS_DISETUJUI, null) }
    }

    private fun updateStatus(status: String, alasanTolak: String?) {
        lifecycleScope.launch {
            val data = viewModel.getPengajuanById(pengajuanId)
            data?.let {
                val today = DateUtils.getTodayDb()
                val updatedData = it.copy(
                    status = status,
                    tanggalDiproses = today,
                    alasanPenolakan = alasanTolak
                )
                viewModel.updatePengajuan(updatedData)
                Toast.makeText(this@DetailPengajuanIzinActivity, "Status diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun showTolakDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = DialogTolakPengajuanBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        
        dialog.window?.let { window ->
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }

        dialogBinding.btnClose.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnBatal.setOnClickListener { dialog.dismiss() }

        dialogBinding.btnSimpan.setOnClickListener {
            val alasan = dialogBinding.etAlasan.text.toString().trim()
            if (alasan.isNotEmpty()) {
                dialog.dismiss()
                updateStatus(AppConstants.STATUS_DITOLAK, alasan)
            } else {
                dialogBinding.etAlasan.error = "Alasan harus diisi"
            }
        }
        dialog.show()
    }
}

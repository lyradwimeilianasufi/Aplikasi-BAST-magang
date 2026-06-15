package com.example.aplikasibast.features.approval.presentation.activity

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.aplikasibast.MainViewModel
import com.example.aplikasibast.core.constants.AppConstants
import com.example.aplikasibast.core.utils.DateUtils
import com.example.aplikasibast.databinding.ActivityDetailPengajuanDiajukanBinding
import com.example.aplikasibast.databinding.DialogTolakPengajuanBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class DetailPengajuanIzinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPengajuanDiajukanBinding
    private val viewModel: MainViewModel by viewModel()
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
                    binding.tvPeriodeIzin.text = "${it.tanggalMulai} - ${it.tanggalSelesai}"
                    binding.tvAlasan.text = it.alasan
                    binding.tvTanggalPengajuan.text = it.tanggalPengajuan
                    binding.tvJumlahHari.text = "${DateUtils.calculateDays(it.tanggalMulai, it.tanggalSelesai)} Hari"
                }
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
                val updatedData = it.copy(
                    status = status,
                    tanggalDiproses = DateUtils.formatToUi(DateUtils.getTodayDb()),
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
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.btnSimpan.setOnClickListener {
            val alasan = dialogBinding.etAlasan.text.toString()
            if (alasan.isNotEmpty()) {
                dialog.dismiss()
                updateStatus(AppConstants.STATUS_DITOLAK, alasan)
            }
        }
        dialog.show()
    }
}

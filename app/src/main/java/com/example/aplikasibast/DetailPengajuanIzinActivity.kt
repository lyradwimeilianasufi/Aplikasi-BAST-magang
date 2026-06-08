package com.example.aplikasibast

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
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
                    
                    // FORMAT TANGGAL DISINI
                    val tglMulai = DateUtils.formatToUi(it.tanggalMulai)
                    val tglSelesai = DateUtils.formatToUi(it.tanggalSelesai)
                    binding.tvPeriodeIzin.text = "$tglMulai - $tglSelesai"
                    
                    binding.tvAlasan.text = it.alasan
                    binding.tvTanggalPengajuan.text = DateUtils.formatToUi(it.tanggalPengajuan)
                    
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
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }
        
        binding.btnTolak.setOnClickListener { showTolakDialog() }
        
        binding.btnSetujui.setOnClickListener {
            updateStatus(AppConstants.STATUS_DISETUJUI, null)
        }
    }

    private fun updateStatus(status: String, alasanTolak: String?) {
        lifecycleScope.launch {
            val data = viewModel.getPengajuanById(pengajuanId)
            data?.let {
                // PERBAIKAN: Simpan format DB (yyyy-MM-dd), bukan format UI
                val todayDb = DateUtils.getTodayDb()
                
                val updatedData = it.copy(
                    status = status,
                    tanggalDiproses = todayDb,
                    alasanPenolakan = alasanTolak
                )
                
                viewModel.updatePengajuan(updatedData)
                
                Toast.makeText(this@DetailPengajuanIzinActivity, "Status berhasil diperbarui", Toast.LENGTH_SHORT).show()
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

        dialogBinding.btnClose.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnBatal.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnSimpan.setOnClickListener {
            val alasan = dialogBinding.etAlasan.text.toString()
            if (alasan.isNotEmpty()) {
                dialog.dismiss()
                updateStatus(AppConstants.STATUS_DITOLAK, alasan)
            } else {
                dialogBinding.tilAlasan.error = "Alasan tidak boleh kosong"
            }
        }

        dialog.show()
        dialog.window?.let { window ->
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(window.attributes)
            layoutParams.width = (resources.displayMetrics.widthPixels * 0.85).toInt()
            layoutParams.gravity = Gravity.CENTER
            window.attributes = layoutParams
        }
    }
}

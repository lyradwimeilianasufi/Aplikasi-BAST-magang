package com.example.aplikasibast

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.aplikasibast.databinding.ActivityDetailIzinDitolakBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class DetailIzinDitolakActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailIzinDitolakBinding
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailIzinDitolakBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
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
                binding.tvAlasanPenolakan.text = it.alasanPenolakan ?: "-"
                binding.tvJenisIzin.text = it.jenisIzin
                
                val tglMulai = DateUtils.formatToUi(it.tanggalMulai)
                val tglSelesai = DateUtils.formatToUi(it.tanggalSelesai)
                binding.tvPeriodeIzin.text = "$tglMulai - $tglSelesai"
                
                binding.tvAlasan.text = it.alasan
                binding.tvJumlahHari.text = "${DateUtils.calculateDays(it.tanggalMulai, it.tanggalSelesai)} Hari"

                if (!it.lampiranPath.isNullOrEmpty()) {
                    val file = File(it.lampiranPath)
                    if (file.exists()) {
                        binding.ivFilePendukung.setImageURI(Uri.fromFile(file))
                        binding.ivFilePendukung.alpha = 1.0f
                        // Diubah ke CENTER_CROP agar memenuhi card sesuai contoh gambar
                        binding.ivFilePendukung.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                    } else {
                        binding.ivFilePendukung.setImageResource(R.drawable.ic_gallery)
                        binding.ivFilePendukung.alpha = 0.2f
                        binding.ivFilePendukung.scaleType = android.widget.ImageView.ScaleType.CENTER
                    }
                } else {
                    binding.ivFilePendukung.setImageResource(R.drawable.ic_gallery)
                    binding.ivFilePendukung.alpha = 0.2f
                    binding.ivFilePendukung.scaleType = android.widget.ImageView.ScaleType.CENTER
                }
            }
        }
    }
}

package com.example.aplikasibast

import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.aplikasibast.databinding.ActivityDetailIzinDisetujuiBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

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

        // PERBAIKAN: Gunakan key PENGAJUAN_ID secara konsisten
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
                
                binding.tvJumlahHari.text = "${hitungDurasi(it.tanggalMulai, it.tanggalSelesai)} Hari"

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

    private fun hitungDurasi(mulai: String, selesai: String): Long {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val d1 = sdf.parse(mulai)
            val d2 = sdf.parse(selesai)
            TimeUnit.MILLISECONDS.toDays(d2!!.time - d1!!.time) + 1
        } catch (e: Exception) { 1 }
    }
}

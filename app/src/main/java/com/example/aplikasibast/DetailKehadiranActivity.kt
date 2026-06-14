package com.example.aplikasibast

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
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
import com.example.aplikasibast.databinding.ActivityDetailKehadiranHadirBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class DetailKehadiranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailKehadiranHadirBinding
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailKehadiranHadirBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val kehadiranId = intent.getIntExtra("KEHADIRAN_ID", -1)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbarLayout.updatePadding(top = systemBars.top)
            insets
        }

        if (kehadiranId != -1) {
            loadData(kehadiranId)
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun loadData(id: Int) {
        lifecycleScope.launch {
            // Coba ambil dari tabel kehadiran (absensi fisik)
            val presence = viewModel.getKehadiranById(id)
            
            if (presence != null) {
                displayPresenceData(presence)
            } else {
                // Jika tidak ada, coba ambil dari tabel pengajuan (Izin/Sakit/Cuti)
                val leave = viewModel.getPengajuanById(id)
                leave?.let {
                    displayLeaveData(it)
                }
            }
        }
    }

    private fun displayPresenceData(it: KehadiranEntity) {
        binding.tvTanggalKerja.text = DateUtils.formatToUi(it.tanggal)
        binding.tvJamKerja.text = viewModel.workHours
        binding.tvTotalJamKerja.text = it.totalJam
        
        setupStatusUI(it.status)

        // Sembunyikan tombol detail izin, tampilkan detail absen
        binding.btnLihatDetailIzin.visibility = View.GONE
        binding.layoutDetailAbsen.visibility = View.VISIBLE

        showAttendanceDetails(it.jamMasuk, it.jamKeluar, it.lokasiMasuk, it.lokasiKeluar, it.fotoMasukPath, it.fotoKeluarPath, it.latMasuk, it.lngMasuk, it.latKeluar, it.lngKeluar)
    }

    private fun displayLeaveData(it: PengajuanIzinEntity) {
        binding.tvTanggalKerja.text = DateUtils.formatToUi(it.tanggalMulai) 
        binding.tvJamKerja.text = viewModel.workHours
        binding.tvTotalJamKerja.text = " - "
        
        setupStatusUI(it.jenisIzin)

        // TAMPILAN KHUSUS IZIN: Tampilkan tombol "Lihat Detail Izin" dan sembunyikan detail absen
        binding.btnLihatDetailIzin.visibility = View.VISIBLE
        binding.layoutDetailAbsen.visibility = View.GONE

        // Teks tombol statis sesuai permintaan
        binding.btnLihatDetailIzin.text = "Lihat Detail Izin >"

        binding.btnLihatDetailIzin.setOnClickListener { _ ->
            // Cek status untuk menentukan Activity tujuan
            val targetActivity = when (it.status.uppercase()) {
                "DISETUJUI" -> DetailIzinDisetujuiActivity::class.java
                "DITOLAK" -> DetailIzinDitolakActivity::class.java
                else -> DetailIzinDitolakActivity::class.java // Default
            }

            val intent = Intent(this, targetActivity).apply {
                putExtra("PENGAJUAN_ID", it.id)
            }
            startActivity(intent)
        }
    }

    private fun setupStatusUI(status: String) {
        // Cek apakah jenisnya pengajuan (Izin, Cuti, Sakit)
        val isLeaveType = status.equals("Izin", true) || 
                         status.equals("Cuti", true) || 
                         status.equals("Sakit", true)

        // Tampilkan teks "Izin" di badge jika termasuk jenis pengajuan, sesuai gambar
        binding.tvStatusBadge.text = if (isLeaveType) "Izin" else status
        
        // Samakan warna dengan Riwayat Kehadiran (Solid Colors)
        val colorHex = when {
            status.equals("Hadir", true) -> "#27AE60"
            status.equals("Telat", true) -> "#F2994A"
            isLeaveType -> "#F2994A"
            status.equals("Alpa", true) -> "#EB5757"
            else -> "#9E9E9E"
        }
        
        binding.tvStatusBadge.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorHex))
        binding.tvStatusBadge.setTextColor(Color.WHITE)

        // Logika khusus untuk "Telat" -> Tampilkan badge "Hadir" di bawahnya
        if (status.equals("Telat", true)) {
            binding.tvStatusBadgeHadir.visibility = View.VISIBLE
            binding.tvStatusBadgeHadir.text = "Hadir"
            binding.tvStatusBadgeHadir.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#27AE60"))
            binding.tvStatusBadgeHadir.setTextColor(Color.WHITE)
        } else {
            binding.tvStatusBadgeHadir.visibility = View.GONE
        }
    }

    private fun showAttendanceDetails(
        jamMasuk: String, jamKeluar: String, 
        lokasiMasuk: String?, lokasiKeluar: String?, 
        fotoMasuk: String?, fotoKeluar: String?,
        latM: Double?, lngM: Double?, latK: Double?, lngK: Double?
    ) {
        binding.tvWaktuMasuk.text = jamMasuk
        binding.tvWaktuKeluar.text = jamKeluar
        
        // Lokasi Masuk
        if (lokasiMasuk == null || lokasiMasuk == "-" || latM == null) {
            binding.btnLihatLokasiMasuk.text = "-"
            binding.btnLihatLokasiMasuk.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.btnLihatLokasiMasuk.setOnClickListener(null)
        } else {
            binding.btnLihatLokasiMasuk.text = "Lihat Lokasi"
            binding.btnLihatLokasiMasuk.setTextColor(ContextCompat.getColor(this, R.color.blue_primary))
            binding.btnLihatLokasiMasuk.setOnClickListener {
                navigateToMap("Lokasi Masuk", jamMasuk, latM, lngM, lokasiMasuk)
            }
        }

        // Lokasi Keluar
        if (lokasiKeluar == null || lokasiKeluar == "-" || latK == null) {
            binding.btnLihatLokasiKeluar.text = "-"
            binding.btnLihatLokasiKeluar.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.btnLihatLokasiKeluar.setOnClickListener(null)
        } else {
            binding.btnLihatLokasiKeluar.text = "Lihat Lokasi"
            binding.btnLihatLokasiKeluar.setTextColor(ContextCompat.getColor(this, R.color.blue_primary))
            binding.btnLihatLokasiKeluar.setOnClickListener {
                navigateToMap("Lokasi Keluar", jamKeluar, latK, lngK, lokasiKeluar)
            }
        }
        
        // Foto Masuk
        if (fotoMasuk != null) {
            val file = File(fotoMasuk)
            if (file.exists()) {
                binding.ivFotoMasuk.setImageURI(Uri.fromFile(file))
            } else {
                binding.ivFotoMasuk.setImageResource(R.drawable.ic_user)
            }
        } else {
            binding.ivFotoMasuk.setImageResource(R.drawable.ic_user)
        }

        // Foto Keluar
        if (fotoKeluar != null) {
            val file = File(fotoKeluar)
            if (file.exists()) {
                binding.ivFotoKeluar.setImageURI(Uri.fromFile(file))
            } else {
                binding.ivFotoKeluar.setImageResource(R.drawable.ic_user)
            }
        } else {
            binding.ivFotoKeluar.setImageResource(R.drawable.ic_user)
        }
    }

    private fun navigateToMap(title: String, time: String, lat: Double?, lng: Double?, address: String?) {
        if (lat != null && lng != null) {
            val intent = Intent(this, LocationAbsenActivity::class.java).apply {
                putExtra("IS_VIEW_ONLY", true)
                putExtra("TITLE_TO_VIEW", title)
                putExtra("TIME_TO_VIEW", time)
                putExtra("LAT_TO_VIEW", lat)
                putExtra("LNG_TO_VIEW", lng)
                putExtra("ADDRESS_TO_VIEW", address)
            }
            startActivity(intent)
        }
    }
}

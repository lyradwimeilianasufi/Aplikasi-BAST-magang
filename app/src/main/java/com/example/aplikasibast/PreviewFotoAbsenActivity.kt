package com.example.aplikasibast

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.aplikasibast.databinding.ActivityPreviewFotoAbsenBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PreviewFotoAbsenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreviewFotoAbsenBinding
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewFotoAbsenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val photoPath = intent.getStringExtra("FILE_PATH")
        if (photoPath != null) {
            val photoFile = File(photoPath)
            if (photoFile.exists()) {
                binding.ivPreview.setImageURI(Uri.fromFile(photoFile))
            }
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnConfirm.setOnClickListener {
            saveAttendanceAndFinish(photoPath)
        }
    }

    private fun saveAttendanceAndFinish(photoPath: String?) {
        val isMasuk = intent.getBooleanExtra("IS_MASUK", true)
        val lokasi = intent.getStringExtra("LOKASI") ?: "Lokasi tidak diketahui"
        
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale("id", "ID"))
        val timeFormat = SimpleDateFormat("HH:mm 'WIB'", Locale("id", "ID"))
        
        val tanggal = dateFormat.format(calendar.time)
        val jamSekarang = timeFormat.format(calendar.time)

        lifecycleScope.launch {
            if (isMasuk) {
                // Logika Absen Masuk: Buat data baru
                val kehadiran = KehadiranEntity(
                    tanggal = tanggal,
                    status = "Hadir",
                    jamMasuk = jamSekarang,
                    jamKeluar = "-",
                    totalJam = "-",
                    fotoMasukPath = photoPath,
                    lokasiMasuk = lokasi
                )
                viewModel.insertKehadiran(kehadiran)
                finishWithSuccess()
            } else {
                // Logika Absen Keluar: Cari data hari ini dan update
                val todayKehadiran = viewModel.allKehadiran.first().find { it.tanggal == tanggal }
                
                if (todayKehadiran != null) {
                    val updatedKehadiran = todayKehadiran.copy(
                        jamKeluar = jamSekarang,
                        fotoKeluarPath = photoPath,
                        lokasiKeluar = lokasi,
                        totalJam = calculateTotalWorkHours(todayKehadiran.jamMasuk, jamSekarang)
                    )
                    viewModel.updateKehadiran(updatedKehadiran)
                    finishWithSuccess()
                } else {
                    Toast.makeText(this@PreviewFotoAbsenActivity, "Data absen masuk tidak ditemukan untuk hari ini", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun finishWithSuccess() {
        Toast.makeText(this, "Absensi berhasil disimpan", Toast.LENGTH_SHORT).show()

        // Kembali ke MainActivity dan tampilkan dialog sukses
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra("SHOW_SUCCESS_DIALOG", true)
        startActivity(intent)
        finish()
    }

    private fun calculateTotalWorkHours(jamMasuk: String, jamKeluar: String): String {
        return try {
            val format = SimpleDateFormat("HH:mm", Locale("id", "ID"))
            // Ambil bagian jam saja, misal "08:30 WIB" -> "08:30"
            val startStr = jamMasuk.substringBefore(" WIB")
            val endStr = jamKeluar.substringBefore(" WIB")
            
            val dateMasuk = format.parse(startStr)
            val dateKeluar = format.parse(endStr)
            
            if (dateMasuk != null && dateKeluar != null) {
                val diff = dateKeluar.time - dateMasuk.time
                val hours = diff / (1000 * 60 * 60)
                val minutes = (diff / (1000 * 60)) % 60
                
                String.format("%02d Jam %02d Menit", hours, minutes)
            } else {
                "-"
            }
        } catch (e: Exception) {
            "-"
        }
    }
}

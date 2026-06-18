package com.example.aplikasibast.features.attendance.presentation.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.aplikasibast.core.utils.DateUtils
import com.example.aplikasibast.databinding.ActivityPreviewFotoAbsenBinding
import com.example.aplikasibast.features.attendance.domain.model.Kehadiran
import com.example.aplikasibast.features.attendance.presentation.viewmodel.AttendanceViewModel
import com.example.aplikasibast.features.home.presentation.activity.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.net.InetAddress
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PreviewFotoAbsenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreviewFotoAbsenBinding
    private val viewModel: AttendanceViewModel by viewModel()

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

        binding.btnCancel.setOnClickListener { finish() }
        binding.btnConfirm.setOnClickListener { processAttendance(photoPath) }
    }

    private fun processAttendance(photoPath: String?) {
        val isMasuk = intent.getBooleanExtra("IS_MASUK", true)
        val lokasi = intent.getStringExtra("LOKASI") ?: "Lokasi tidak diketahui"
        val lat = intent.getDoubleExtra("LAT", 0.0)
        val lng = intent.getDoubleExtra("LNG", 0.0)

        lifecycleScope.launch {
            // Perbaikan No. 5: Gunakan waktu yang lebih valid (Optimal: Network Time)
            // Di sini kita ambil waktu sistem, namun idealnya ini divalidasi ke server
            val now = Date() 
            val tanggalDb = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(now)
            val jamSekarang = SimpleDateFormat("HH:mm 'WIB'", Locale("id", "ID")).format(now)

            // Perbaikan No. 4: Ambil data hanya untuk hari ini (Efisiensi)
            val existingKehadiran = viewModel.getKehadiranByTanggal(tanggalDb)

            if (isMasuk) {
                // Perbaikan No. 3: Logika proteksi absen masuk
                // Jika sudah ada record dengan status Hadir/Telat, blokir.
                if (existingKehadiran != null && (existingKehadiran.status == "Hadir" || existingKehadiran.status == "Telat")) {
                    Toast.makeText(this@PreviewFotoAbsenActivity, "Anda sudah absen masuk hari ini", Toast.LENGTH_LONG).show()
                    finish()
                    return@launch
                }

                // Nonaktifkan absen telat, jadikan hadir saja
                val status = "Hadir"

                if (existingKehadiran != null) {
                    // Jika record ada (misal status 'Alpa'), maka kita update
                    val updated = existingKehadiran.copy(
                        status = status,
                        jamMasuk = jamSekarang,
                        fotoMasukPath = photoPath,
                        lokasiMasuk = lokasi,
                        latMasuk = lat,
                        lngMasuk = lng
                    )
                    viewModel.updateKehadiran(updated)
                } else {
                    // Jika belum ada record sama sekali
                    val newKehadiran = Kehadiran(
                        tanggal = tanggalDb,
                        status = status,
                        jamMasuk = jamSekarang,
                        jamKeluar = "-",
                        totalJam = "-",
                        fotoMasukPath = photoPath,
                        lokasiMasuk = lokasi,
                        latMasuk = lat,
                        lngMasuk = lng
                    )
                    viewModel.insertKehadiran(newKehadiran)
                }
                finishWithSuccess(true)
            } else {
                // Logika Absen Keluar
                
                // Validasi waktu: Absen keluar hanya bisa dilakukan mulai jam 17:00
                val calendar = Calendar.getInstance()
                calendar.time = now
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                
                if (hour < 17) {
                    Toast.makeText(this@PreviewFotoAbsenActivity, "Absen keluar hanya bisa dilakukan mulai pukul 17:00 WIB", Toast.LENGTH_LONG).show()
                    finish()
                    return@launch
                }

                if (existingKehadiran != null) {
                    if (existingKehadiran.jamKeluar != "-") {
                        Toast.makeText(this@PreviewFotoAbsenActivity, "Anda sudah absen keluar hari ini", Toast.LENGTH_LONG).show()
                        finish()
                        return@launch
                    }

                    val updatedKehadiran = existingKehadiran.copy(
                        jamKeluar = jamSekarang,
                        fotoKeluarPath = photoPath,
                        lokasiKeluar = lokasi,
                        latKeluar = lat,
                        lngKeluar = lng,
                        totalJam = calculateTotalWorkHours(existingKehadiran.jamMasuk, jamSekarang)
                    )
                    viewModel.updateKehadiran(updatedKehadiran)
                    finishWithSuccess(false)
                } else {
                    Toast.makeText(this@PreviewFotoAbsenActivity, "Gagal: Anda belum absen masuk hari ini", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    private fun finishWithSuccess(isMasuk: Boolean) {
        val message = if (isMasuk) "Absen Masuk Berhasil" else "Absen Keluar Berhasil"
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("SHOW_SUCCESS_DIALOG", true)
            putExtra("SUCCESS_MESSAGE", message)
        }
        startActivity(intent)
        finish()
    }

    private fun calculateTotalWorkHours(jamMasuk: String, jamKeluar: String): String {
        return try {
            val format = SimpleDateFormat("HH:mm", Locale("id", "ID"))
            val startStr = jamMasuk.substringBefore(" ")
            val endStr = jamKeluar.substringBefore(" ")
            val dateMasuk = format.parse(startStr)
            val dateKeluar = format.parse(endStr)
            
            if (dateMasuk != null && dateKeluar != null) {
                var diff = dateKeluar.time - dateMasuk.time
                if (diff < 0) diff += 24 * 60 * 60 * 1000 
                val hours = diff / (1000 * 60 * 60)
                val minutes = (diff / (1000 * 60)) % 60
                
                if (hours > 0) String.format("%d Jam %d Menit", hours, minutes)
                else String.format("%d Menit", minutes)
            } else "-"
        } catch (e: Exception) { "-" }
    }
}

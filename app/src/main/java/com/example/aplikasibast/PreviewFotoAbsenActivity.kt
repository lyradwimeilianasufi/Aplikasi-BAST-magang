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

        binding.btnCancel.setOnClickListener { finish() }
        binding.btnConfirm.setOnClickListener { saveAttendanceAndFinish(photoPath) }
    }

    private fun saveAttendanceAndFinish(photoPath: String?) {
        val isMasuk = intent.getBooleanExtra("IS_MASUK", true)
        val lokasi = intent.getStringExtra("LOKASI") ?: "Lokasi tidak diketahui"
        val lat = intent.getDoubleExtra("LAT", 0.0)
        val lng = intent.getDoubleExtra("LNG", 0.0)
        
        val calendar = Calendar.getInstance()
        // Gunakan format ISO untuk database agar konsisten (yyyy-MM-dd)
        val tanggalDb = SimpleDateFormat(AppConstants.DATE_FORMAT_DB, Locale.US).format(calendar.time)
        val jamSekarang = SimpleDateFormat("HH:mm 'WIB'", Locale("id", "ID")).format(calendar.time)

        lifecycleScope.launch {
            // Ambil data absen hari ini jika sudah ada
            val allData = viewModel.allKehadiran.first()
            val existingKehadiran = allData.find { it.tanggal == tanggalDb }

            if (isMasuk) {
                if (existingKehadiran != null) {
                    Toast.makeText(this@PreviewFotoAbsenActivity, "Anda sudah melakukan absen masuk hari ini", Toast.LENGTH_LONG).show()
                    finish()
                    return@launch
                }

                // Logika Jam Masuk (Jam 9 pagi)
                val limit = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 9)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }
                val status = if (calendar.after(limit)) "Telat" else "Hadir"

                val kehadiran = KehadiranEntity(
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
                viewModel.insertKehadiran(kehadiran)
                finishWithSuccess(isMasuk)
            } else {
                // Logika Absen Keluar
                if (existingKehadiran != null) {
                    val updatedKehadiran = existingKehadiran.copy(
                        jamKeluar = jamSekarang,
                        fotoKeluarPath = photoPath,
                        lokasiKeluar = lokasi,
                        latKeluar = lat,
                        lngKeluar = lng,
                        totalJam = calculateTotalWorkHours(existingKehadiran.jamMasuk, jamSekarang)
                    )
                    viewModel.updateKehadiran(updatedKehadiran)
                    finishWithSuccess(isMasuk)
                } else {
                    Toast.makeText(this@PreviewFotoAbsenActivity, "Data absen masuk tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun finishWithSuccess(isMasuk: Boolean) {
        val message = if (isMasuk) "Absen Masuk Berhasil" else "Berhasil melakukan absen keluar"
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
            val startStr = jamMasuk.substringBefore(" WIB")
            val endStr = jamKeluar.substringBefore(" WIB")
            val dateMasuk = format.parse(startStr)
            val dateKeluar = format.parse(endStr)
            
            if (dateMasuk != null && dateKeluar != null) {
                var diff = dateKeluar.time - dateMasuk.time
                if (diff < 0) diff += 24 * 60 * 60 * 1000
                val hours = diff / (1000 * 60 * 60)
                val minutes = (diff / (1000 * 60)) % 60
                String.format("%02d Jam %02d Menit", hours, minutes)
            } else "-"
        } catch (e: Exception) { "-" }
    }
}

package com.example.aplikasibast

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasibast.databinding.ActivityPreviewFotoAbsenBinding
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
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale("id", "ID"))
        val timeFormat = SimpleDateFormat("HH:mm 'WIB'", Locale("id", "ID"))
        
        val tanggal = dateFormat.format(calendar.time)
        val jamSekarang = timeFormat.format(calendar.time)

        // Simpan data kehadiran ke Room Database
        // Catatan: Ini logika sederhana untuk "Absen Masuk"
        val kehadiran = KehadiranEntity(
            tanggal = tanggal,
            status = "Hadir",
            jamMasuk = jamSekarang,
            jamKeluar = "-", // Akan diupdate saat absen keluar
            totalJam = "-",
            fotoPath = photoPath,
            lokasi = "Gedung BAST" // Sesuai data dari LocationActivity nantinya
        )

        viewModel.insertKehadiran(kehadiran)

        Toast.makeText(this, "Absensi berhasil disimpan", Toast.LENGTH_SHORT).show()

        // Kembali ke MainActivity dan tampilkan dialog sukses
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra("SHOW_SUCCESS_DIALOG", true)
        startActivity(intent)
        finish()
    }
}

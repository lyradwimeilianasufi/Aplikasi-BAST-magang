package com.example.aplikasibast

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplikasibast.databinding.ActivityPengajuanIzinBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PengajuanIzinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPengajuanIzinBinding
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPengajuanIzinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupDropdown()
        setupDatePickers()
        setupListeners()
    }

    private fun setupDropdown() {
        val items = listOf("Sakit", "Izin", "Cuti")
        val adapter = ArrayAdapter(this, R.layout.item_dropdown_izin, items)
        binding.spinnerJenisIzin.setAdapter(adapter)
        binding.spinnerJenisIzin.setOnTouchListener { _, _ ->
            binding.spinnerJenisIzin.showDropDown()
            false
        }
    }

    private fun setupDatePickers() {
        binding.etTanggalMulai.setOnClickListener {
            showDatePicker { date -> binding.etTanggalMulai.setText(date) }
        }

        binding.etTanggalSelesai.setOnClickListener {
            showDatePicker { date -> binding.etTanggalSelesai.setText(date) }
        }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
            onDateSelected(formattedDate)
        }, year, month, day).show()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSubmit.setOnClickListener {
            val jenisIzin = binding.spinnerJenisIzin.text.toString()
            val tglMulai = binding.etTanggalMulai.text.toString()
            val tglSelesai = binding.etTanggalSelesai.text.toString()
            val alasan = binding.etAlasan.text.toString()

            if (jenisIzin.isEmpty() || tglMulai.isEmpty() || tglSelesai.isEmpty() || alasan.isEmpty()) {
                Toast.makeText(this, "Harap isi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Simpan ke Database via ViewModel
            viewModel.submitPengajuanIzin(
                jenisIzin = jenisIzin,
                tanggalMulai = tglMulai,
                tanggalSelesai = tglSelesai,
                alasan = alasan,
                tanggalPengajuan = getCurrentFormattedDate()
            )

            Toast.makeText(this, "Pengajuan berhasil dikirim", Toast.LENGTH_SHORT).show()
            
            // Navigasi ke halaman Daftar Pengajuan Izin
            val intent = Intent(this, DaftarPengajuanIzinActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getCurrentFormattedDate(): String {
        val sdf = SimpleDateFormat("EEEE, dd MMM yyyy", Locale("id", "ID"))
        return sdf.format(Calendar.getInstance().time)
    }
}

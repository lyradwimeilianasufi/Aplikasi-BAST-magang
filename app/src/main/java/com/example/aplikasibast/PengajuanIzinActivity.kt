package com.example.aplikasibast

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplikasibast.databinding.ActivityPengajuanIzinBinding
import java.util.Calendar

class PengajuanIzinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPengajuanIzinBinding

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
        // Daftar item sesuai urutan di gambar: Sakit, Izin, Cuti
        val items = listOf("Sakit", "Izin", "Cuti")
        
        // Menggunakan R.layout.item_dropdown_izin yang sudah diperbarui dengan warna teks hitam
        val adapter = ArrayAdapter(this, R.layout.item_dropdown_izin, items)
        
        // Memasang adapter ke AutoCompleteTextView
        binding.spinnerJenisIzin.setAdapter(adapter)
        
        // Memastikan dropdown muncul saat diklik dan tidak memfilter teks
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
            // Logika submit
        }
    }
}

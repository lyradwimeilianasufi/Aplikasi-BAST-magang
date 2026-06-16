package com.example.aplikasibast.features.permission.presentation.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.aplikasibast.R
import com.example.aplikasibast.core.utils.DateUtils
import com.example.aplikasibast.databinding.ActivityPengajuanIzinBinding
import com.example.aplikasibast.features.permission.presentation.viewmodel.PermissionViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PengajuanIzinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPengajuanIzinBinding
    private val viewModel: PermissionViewModel by viewModel()
    
    private var calendarMulai: Calendar? = null
    private var calendarSelesai: Calendar? = null
    private var lampiranPath: String? = null

    // Formatters
    private val dbDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val uiDateFormat = SimpleDateFormat("EEEE, d MMM yyyy", Locale("id", "ID"))

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { handleSelectedImage(it) }
    }

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
        observeViewModel()
    }

    private fun setupDropdown() {
        val items = listOf("Sakit", "Izin", "Cuti")
        val adapter = ArrayAdapter(this, R.layout.item_dropdown_izin, items)
        binding.spinnerJenisIzin.setAdapter(adapter)
        binding.spinnerJenisIzin.setOnClickListener { binding.spinnerJenisIzin.showDropDown() }
    }

    private fun setupDatePickers() {
        binding.etTanggalMulai.setOnClickListener {
            showDatePicker(minDate = System.currentTimeMillis()) { calendar ->
                calendarMulai = calendar
                binding.etTanggalMulai.setText(uiDateFormat.format(calendar.time))
                
                calendarSelesai?.let { selesai ->
                    if (calendar.after(selesai)) {
                        calendarSelesai = null
                        binding.etTanggalSelesai.setText("")
                    }
                }
            }
        }

        binding.etTanggalSelesai.setOnClickListener {
            if (calendarMulai == null) {
                Toast.makeText(this, "Pilih tanggal mulai terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            showDatePicker(minDate = calendarMulai?.timeInMillis) { calendar ->
                calendarSelesai = calendar
                binding.etTanggalSelesai.setText(uiDateFormat.format(calendar.time))
            }
        }
    }

    private fun showDatePicker(minDate: Long? = null, onDateSelected: (Calendar) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(this, { _, year, month, day ->
            val selectedCalendar = Calendar.getInstance().apply { set(year, month, day) }
            onDateSelected(selectedCalendar)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        minDate?.let { datePicker.datePicker.minDate = it }
        datePicker.show()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnUpload.setOnClickListener { pickImageLauncher.launch("image/*") }

        binding.btnSubmit.setOnClickListener {
            val jenisIzin = binding.spinnerJenisIzin.text.toString()
            val alasan = binding.etAlasan.text.toString()

            if (jenisIzin.isEmpty() || calendarMulai == null || calendarSelesai == null || alasan.isEmpty()) {
                Toast.makeText(this, "Harap isi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validasi Lampiran khusus untuk Sakit
            if (jenisIzin.equals("Sakit", true) && lampiranPath == null) {
                Toast.makeText(this, "Harap unggah surat keterangan dokter untuk pengajuan Sakit", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            binding.btnSubmit.isEnabled = false
            
            viewModel.submitPengajuanIzin(
                jenisIzin = jenisIzin,
                tanggalMulai = dbDateFormat.format(calendarMulai!!.time),
                tanggalSelesai = dbDateFormat.format(calendarSelesai!!.time),
                alasan = alasan,
                tanggalPengajuan = DateUtils.getTodayDb(),
                lampiranPath = lampiranPath
            )
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.submitResult.collect { result ->
                    binding.btnSubmit.isEnabled = true
                    result.onSuccess { message ->
                        Toast.makeText(this@PengajuanIzinActivity, message, Toast.LENGTH_SHORT).show()
                        finish()
                    }.onFailure { exception ->
                        Toast.makeText(this@PengajuanIzinActivity, exception.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun handleSelectedImage(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(filesDir, "lampiran_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            lampiranPath = file.absolutePath
            binding.ivPreviewLampiran.setImageURI(Uri.fromFile(file))
            binding.ivPreviewLampiran.visibility = View.VISIBLE
            binding.layoutPlaceholder.visibility = View.GONE
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal memproses gambar", Toast.LENGTH_SHORT).show()
        }
    }
}

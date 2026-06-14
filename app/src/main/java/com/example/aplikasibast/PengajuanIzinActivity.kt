package com.example.aplikasibast

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
import com.example.aplikasibast.databinding.ActivityPengajuanIzinBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PengajuanIzinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPengajuanIzinBinding
    private val viewModel: MainViewModel by viewModel()
    
    private var calendarMulai: Calendar? = null
    private var calendarSelesai: Calendar? = null
    private var lampiranPath: String? = null

    // Formatters
    private val dbDateFormat = SimpleDateFormat(AppConstants.DATE_FORMAT_DB, Locale.US)
    // PERBAIKAN: Menggunakan format Senin, 08 Jun 2026
    private val uiDateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale("id", "ID"))

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
    }

    private fun setupDropdown() {
        val items = listOf("Sakit", "Izin", "Cuti")
        val adapter = ArrayAdapter(this, R.layout.item_dropdown_izin, items)
        binding.spinnerJenisIzin.setAdapter(adapter)
        binding.spinnerJenisIzin.setOnClickListener { binding.spinnerJenisIzin.showDropDown() }
    }

    private fun setupDatePickers() {
        binding.etTanggalMulai.setOnClickListener {
            showDatePicker { calendar ->
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
        binding.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.btnUpload.setOnClickListener { pickImageLauncher.launch("image/*") }

        binding.btnSubmit.setOnClickListener {
            val jenisIzin = binding.spinnerJenisIzin.text.toString()
            val alasan = binding.etAlasan.text.toString()

            if (jenisIzin.isEmpty() || calendarMulai == null || calendarSelesai == null || alasan.isEmpty()) {
                Toast.makeText(this, "Harap isi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.submitPengajuanIzin(
                jenisIzin = jenisIzin,
                tanggalMulai = dbDateFormat.format(calendarMulai!!.time),
                tanggalSelesai = dbDateFormat.format(calendarSelesai!!.time),
                alasan = alasan,
                tanggalPengajuan = DateUtils.getTodayDb(),
                lampiranPath = lampiranPath
            )

            Toast.makeText(this, "Pengajuan berhasil dikirim", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, DaftarPengajuanActivity::class.java))
            finish()
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

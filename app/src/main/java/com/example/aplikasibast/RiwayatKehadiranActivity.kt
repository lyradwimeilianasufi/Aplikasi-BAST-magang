package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasibast.databinding.ActivityRiwayatKehadiranBinding

class RiwayatKehadiranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatKehadiranBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatKehadiranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupNavigation()
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        // Memastikan ikon Riwayat aktif saat halaman ini ditampilkan
        binding.bottomNavigation.bottomNavigation.selectedItemId = R.id.nav_riwayat
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupNavigation() {
        binding.bottomNavigation.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_beranda -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }
                R.id.nav_kehadiran -> {
                    val intent = Intent(this, KehadiranActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }
                R.id.nav_riwayat -> true // Tetap di halaman ini
                R.id.nav_akun -> {
                    // startActivity(Intent(this, AkunActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        val items = listOf(
            RiwayatItem.KehadiranData("Jumat, 06 Jan 2024", "Hadir", "08:45 WIB", "17:20 WIB", "9 Jam 45 Menit"),
            RiwayatItem.IzinData("Kamis, 05 Jan 2024", "-", "-", "-", "Izin"),
            RiwayatItem.AlpaData("Rabu, 04 Jan 2024", "Alpa"),
            RiwayatItem.KehadiranData("Selasa, 03 Des 2024 [Take Over]", "Hadir", "08:45 WIB", "18:00", "9 Jam 30 Menit"),
            RiwayatItem.KehadiranData("Senin, 02 Des 2024", "Hadir", "08:45 WIB", "17:20 WIB", "9 Jam 45 Menit"),
            RiwayatItem.LiburData("Minggu, 01 Des 2024", "Libur")
        )

        val adapter = RiwayatKehadiranAdapter(items) { item ->
            when (item) {
                is RiwayatItem.KehadiranData -> {
                    val intent = Intent(this, DetailKehadiranActivity::class.java)
                    startActivity(intent)
                }
                is RiwayatItem.IzinData -> {
                    val intent = Intent(this, DetailKehadiranIzinActivity::class.java)
                    startActivity(intent)
                }
                is RiwayatItem.AlpaData -> {
                    val intent = Intent(this, DetailKehadiranAlpaActivity::class.java)
                    startActivity(intent)
                }
                is RiwayatItem.LiburData -> {
                    val intent = Intent(this, DetailKehadiranLiburActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        binding.rvRiwayatKehadiran.apply {
            layoutManager = LinearLayoutManager(this@RiwayatKehadiranActivity)
            this.adapter = adapter
        }
    }
}

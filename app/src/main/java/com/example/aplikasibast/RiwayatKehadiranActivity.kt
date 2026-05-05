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
            RiwayatItem.KehadiranData("Jumat, 03 Jan 2025", "Hadir", "08:45 WIB", "17:20 WIB", "9 Jam 45 Menit"),
            RiwayatItem.KehadiranData("Kamis, 02 Jan 2025", "Hadir", "08:30 WIB", "17:00 WIB", "9 Jam 30 Menit"),
            RiwayatItem.IzinData("Rabu, 01 Jan 2025", "Cuti Tahunan", "01 Jan 2025 - 02 Jan 2025", "2 Hari", "Izin"),
            RiwayatItem.AlpaData("Selasa, 31 Des 2024", "Alpa"),
            RiwayatItem.LiburData("Senin, 30 Des 2024", "Libur"),
            RiwayatItem.KehadiranData("Minggu, 29 Des 2024", "Hadir", "08:40 WIB", "17:05 WIB", "9 Jam 25 Menit")
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
                    // Navigasi ke halaman Detail Libur yang baru dibuat
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

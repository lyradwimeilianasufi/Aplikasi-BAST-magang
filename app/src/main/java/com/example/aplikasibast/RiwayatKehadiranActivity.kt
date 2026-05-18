package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasibast.databinding.ActivityRiwayatKehadiranBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RiwayatKehadiranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatKehadiranBinding
    private val viewModel: MainViewModel by viewModel()
    private lateinit var riwayatAdapter: RiwayatKehadiranAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatKehadiranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupNavigation()
        setupRecyclerView()
        observeData()
    }

    override fun onResume() {
        super.onResume()
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
                R.id.nav_riwayat -> true
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        riwayatAdapter = RiwayatKehadiranAdapter(emptyList()) { item ->
            val intent = when (item) {
                is RiwayatItem.KehadiranData -> Intent(this, DetailKehadiranActivity::class.java)
                is RiwayatItem.IzinData -> Intent(this, DetailKehadiranIzinActivity::class.java)
                is RiwayatItem.AlpaData -> Intent(this, DetailKehadiranAlpaActivity::class.java)
                is RiwayatItem.LiburData -> Intent(this, DetailKehadiranLiburActivity::class.java)
            }
            
            // Kirim ID agar halaman detail bisa mengambil data dari DB
            val id = when(item) {
                is RiwayatItem.KehadiranData -> item.id
                is RiwayatItem.IzinData -> item.id
                is RiwayatItem.AlpaData -> item.id
                is RiwayatItem.LiburData -> item.id
            }
            intent.putExtra("KEHADIRAN_ID", id)
            startActivity(intent)
        }

        binding.rvRiwayatKehadiran.apply {
            layoutManager = LinearLayoutManager(this@RiwayatKehadiranActivity)
            adapter = riwayatAdapter
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.allKehadiran.collect { listKehadiran ->
                val items = listKehadiran.map { entity ->
                    when (entity.status) {
                        "Hadir" -> RiwayatItem.KehadiranData(
                            id = entity.id,
                            tanggal = entity.tanggal,
                            status = entity.status,
                            jamMasuk = entity.jamMasuk,
                            jamKeluar = entity.jamKeluar,
                            totalJam = entity.totalJam
                        )
                        "Izin" -> RiwayatItem.IzinData(
                            id = entity.id,
                            tanggal = entity.tanggal,
                            jenisIzin = "Izin",
                            periode = "-",
                            durasi = "-",
                            status = "Izin"
                        )
                        "Alpa" -> RiwayatItem.AlpaData(
                            id = entity.id,
                            tanggal = entity.tanggal,
                            status = "Alpa"
                        )
                        else -> RiwayatItem.LiburData(
                            id = entity.id,
                            tanggal = entity.tanggal,
                            status = entity.status
                        )
                    }
                }
                riwayatAdapter.updateData(items)
            }
        }
    }
}

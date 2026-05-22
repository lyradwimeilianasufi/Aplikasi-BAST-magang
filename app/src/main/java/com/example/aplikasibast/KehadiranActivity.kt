package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasibast.databinding.ActivityKehadiranBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class KehadiranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKehadiranBinding
    private val viewModel: MainViewModel by viewModel()
    private lateinit var adapter: RiwayatKehadiranAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityKehadiranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            binding.bottomNavigationInclude.bottomNavigation.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupNavigation()
        setupListeners()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = RiwayatKehadiranAdapter(emptyList()) { item ->
            val intent = when (item) {
                is RiwayatItem.KehadiranData -> Intent(this, DetailKehadiranActivity::class.java)
                is RiwayatItem.IzinData -> Intent(this, DetailKehadiranIzinActivity::class.java)
                is RiwayatItem.AlpaData -> Intent(this, DetailKehadiranAlpaActivity::class.java)
                is RiwayatItem.LiburData -> Intent(this, DetailKehadiranLiburActivity::class.java)
            }
            
            val id = when(item) {
                is RiwayatItem.KehadiranData -> item.id
                is RiwayatItem.IzinData -> item.id
                is RiwayatItem.AlpaData -> item.id
                is RiwayatItem.LiburData -> item.id
            }
            intent.putExtra("KEHADIRAN_ID", id)
            startActivity(intent)
        }
        
        binding.rvKehadiranPreview.apply {
            layoutManager = LinearLayoutManager(this@KehadiranActivity)
            this.adapter = this@KehadiranActivity.adapter
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.allKehadiran.collect { listKehadiran ->
                val items = listKehadiran.map { entity ->
                    when (entity.status) {
                        "Hadir", "Telat" -> RiwayatItem.KehadiranData(
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
                adapter.updateData(items)
                updateSummary(listKehadiran)
            }
        }
    }

    private fun updateSummary(list: List<KehadiranEntity>) {
        val hadirCount = list.count { it.status.equals("Hadir", ignoreCase = true) || it.status.equals("Telat", ignoreCase = true) }
        val izinCount = list.count { it.status.equals("Izin", ignoreCase = true) }
        val alpaCount = list.count { it.status.equals("Alpa", ignoreCase = true) }

        binding.tvCountHadir.text = "$hadirCount Hari"
        binding.tvCountIzin.text = "$izinCount Hari"
        binding.tvCountAlpa.text = "$alpaCount Hari"
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigationInclude.bottomNavigation.selectedItemId = R.id.nav_kehadiran
    }

    private fun setupListeners() {
        binding.tvLihatSemuaKehadiran.setOnClickListener {
            val intent = Intent(this, RiwayatKehadiranActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
    }

    private fun setupNavigation() {
        binding.bottomNavigationInclude.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_beranda -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }
                R.id.nav_kehadiran -> true
                R.id.nav_riwayat -> {
                    val intent = Intent(this, RiwayatKehadiranActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }
                R.id.nav_akun -> true
                else -> false
            }
        }
    }
}

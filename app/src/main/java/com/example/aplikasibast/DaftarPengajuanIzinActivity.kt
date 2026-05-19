package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasibast.databinding.ActivityDaftarPengajuanIzinDiajukanBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DaftarPengajuanIzinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDaftarPengajuanIzinDiajukanBinding
    private val viewModel: MainViewModel by viewModel()
    private lateinit var adapter: PengajuanIzinAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. Aktifkan mode Edge-to-Edge
        enableEdgeToEdge()
        
        binding = ActivityDaftarPengajuanIzinDiajukanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Tangani Insets secara menyeluruh pada root layout
        // Ini memastikan tombol di bawah otomatis terangkat di atas navigasi HP
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            // Berikan padding (kiri, atas, kanan, bawah) sesuai area aman sistem
            // Kita biarkan padding top tetap 0 karena kita menggunakan statusBarSpacer secara manual
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            
            // Sesuaikan tinggi spacer status bar di bagian atas agar toolbar tidak tertutup notch/jam
            binding.statusBarSpacer.updateLayoutParams {
                height = systemBars.top
            }
            
            insets
        }

        setupRecyclerView()
        observeData()
        setupUI()
    }

    private fun setupRecyclerView() {
        adapter = PengajuanIzinAdapter { item ->
            val intent = Intent(this, DetailPengajuanIzinActivity::class.java)
            intent.putExtra("PENGAJUAN_ID", item.id)
            startActivity(intent)
        }
        binding.rvPengajuan.layoutManager = LinearLayoutManager(this)
        binding.rvPengajuan.adapter = adapter
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.getPengajuanByStatus("DIAJUKAN").collect { list ->
                adapter.submitList(list)
            }
        }
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnTambahPengajuan.setOnClickListener {
            val intent = Intent(this, PengajuanIzinActivity::class.java)
            startActivity(intent)
        }

        binding.tabDitolak.setOnClickListener {
            val intent = Intent(this, DaftarPengajuanIzinDitolakActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

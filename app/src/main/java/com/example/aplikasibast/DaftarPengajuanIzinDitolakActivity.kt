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
import com.example.aplikasibast.databinding.ActivityDaftarPengajuanIzinDitolakBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DaftarPengajuanIzinDitolakActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDaftarPengajuanIzinDitolakBinding
    private val viewModel: MainViewModel by viewModel()
    private lateinit var adapter: PengajuanIzinAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. Aktifkan mode Edge-to-Edge
        enableEdgeToEdge()
        
        binding = ActivityDaftarPengajuanIzinDitolakBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Tangani Insets secara menyeluruh pada root layout
        // Ini memastikan tombol di bawah otomatis terangkat di atas navigasi HP
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            // Memberikan padding pada root layout mengikuti area aman sistem
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            
            // Sesuaikan tinggi spacer status bar agar toolbar tidak tertutup
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
            val intent = Intent(this, DetailIzinDitolakActivity::class.java)
            intent.putExtra("PENGAJUAN_ID", item.id)
            startActivity(intent)
        }
        binding.rvPengajuan.layoutManager = LinearLayoutManager(this)
        binding.rvPengajuan.adapter = adapter
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.getPengajuanByStatus("DITOLAK").collect { list ->
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

        // Navigasi Tab
        binding.tabDiajukan.setOnClickListener {
            val intent = Intent(this, DaftarPengajuanIzinActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.tabDisetujui.setOnClickListener {
            // Arahkan ke DaftarPengajuanBaruActivity jika itu digunakan untuk tab Disetujui
            val intent = Intent(this, DaftarPengajuanBaruActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

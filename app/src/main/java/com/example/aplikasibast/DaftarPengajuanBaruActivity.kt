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
import com.example.aplikasibast.databinding.ActivityDaftarPengajuanIzinDisetujuiBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DaftarPengajuanBaruActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDaftarPengajuanIzinDisetujuiBinding
    private val viewModel: MainViewModel by viewModel()
    private lateinit var adapter: PengajuanIzinAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. Aktifkan mode Edge-to-Edge
        enableEdgeToEdge()
        
        binding = ActivityDaftarPengajuanIzinDisetujuiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Tangani Insets agar tombol tidak tertutup navigasi bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            // Atur padding root agar otomatis menyesuaikan dengan area aman sistem
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            
            // Atur tinggi spacer status bar di bagian atas
            binding.toolbar.setPadding(0, systemBars.top, 0, 0)
            
            insets
        }

        setupRecyclerView()
        observeData()
        setupUI()
    }

    private fun setupRecyclerView() {
        adapter = PengajuanIzinAdapter { item ->
            val intent = Intent(this, DetailIzinDisetujuiActivity::class.java)
            intent.putExtra("PENGAJUAN_ID", item.id)
            startActivity(intent)
        }
        binding.rvPengajuan.layoutManager = LinearLayoutManager(this)
        binding.rvPengajuan.adapter = adapter
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.getPengajuanByStatus("DISETUJUI").collect { list ->
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

        binding.tabDiajukan.setOnClickListener {
            val intent = Intent(this, DaftarPengajuanIzinActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.tabDitolak.setOnClickListener {
            val intent = Intent(this, DaftarPengajuanIzinDitolakActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

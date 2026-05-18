package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
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
        enableEdgeToEdge()
        binding = ActivityDaftarPengajuanIzinDitolakBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            // Atur padding atas Toolbar menggunakan statusBarSpacer agar tidak tertutup sistem
            val spacerParams = binding.statusBarSpacer.layoutParams
            spacerParams.height = systemBars.top
            binding.statusBarSpacer.layoutParams = spacerParams
            
            // Atur padding bawah container tombol
            val paddingNormal = (20 * resources.displayMetrics.density).toInt()
            binding.btnTambahContainer.updatePadding(bottom = systemBars.bottom + paddingNormal)
            
            insets
        }

        setupRecyclerView()
        observeData()
        setupUI()
    }

    private fun setupRecyclerView() {
        adapter = PengajuanIzinAdapter { item ->
            // Navigasi ke halaman Detail Izin Ditolak saat kartu diklik
            val intent = Intent(this, DetailIzinDitolakActivity::class.java)
            startActivity(intent)
        }
        binding.rvPengajuan.layoutManager = LinearLayoutManager(this)
        binding.rvPengajuan.adapter = adapter
    }

    private fun observeData() {
        lifecycleScope.launch {
            // Ambil data dengan status DITOLAK dari Database Room
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
            val intent = Intent(this, DaftarPengajuanBaruActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

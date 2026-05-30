package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
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

        // 2. Tangani Insets secara presisi
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            // Atur tinggi spacer status bar di bagian atas agar toolbar tidak tertutup
            binding.statusBarSpacer.updateLayoutParams {
                height = systemBars.top
            }
            
            // Angkat kontainer tombol di atas navigasi bar HP
            // Kita ambil padding asli (20dp) dan tambahkan dengan tinggi navigasi bar sistem
            val density = resources.displayMetrics.density
            val padding20dp = (20 * density).toInt()
            binding.btnTambahContainer.updatePadding(bottom = systemBars.bottom + padding20dp)
            
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

        binding.tabDisetujui.setOnClickListener {
            startActivity(Intent(this, DaftarPengajuanBaruActivity::class.java))
            finish()
        }

        binding.tabDitolak.setOnClickListener {
            startActivity(Intent(this, DaftarPengajuanIzinDitolakActivity::class.java))
            finish()
        }
    }
}

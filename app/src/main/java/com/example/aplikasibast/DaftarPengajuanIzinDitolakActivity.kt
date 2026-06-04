package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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

        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            
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
            // SISI RIWAYAT: Diarahkan ke DetailIzinDitolakActivity (Read-Only)
            val intent = Intent(this, DetailIzinDitolakActivity::class.java)
            intent.putExtra("PENGAJUAN_ID", item.id)
            startActivity(intent)
        }
        binding.rvPengajuan.layoutManager = LinearLayoutManager(this)
        binding.rvPengajuan.adapter = adapter
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Mengambil data dengan status DITOLAK secara real-time
                viewModel.getPengajuanByStatus(AppConstants.STATUS_DITOLAK).collect { list ->
                    adapter.submitList(list)
                    
                    // Menangani visibilitas: Tampilkan list jika data ada, jika tidak tampilkan pesan kosong
                    if (list.isEmpty()) {
                        binding.rvPengajuan.visibility = View.GONE
                        binding.scrollViewContent.visibility = View.VISIBLE
                    } else {
                        binding.rvPengajuan.visibility = View.VISIBLE
                        binding.scrollViewContent.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Navigasi ke Halaman Approval Izin (Panel Kontrol)
        binding.btnApproval.setOnClickListener {
            startActivity(Intent(this, ApprovalIzinActivity::class.java))
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

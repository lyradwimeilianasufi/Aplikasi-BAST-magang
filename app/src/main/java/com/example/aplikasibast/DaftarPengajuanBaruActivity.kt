package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
        enableEdgeToEdge()
        binding = ActivityDaftarPengajuanIzinDisetujuiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            binding.toolbar.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        setupRecyclerView()
        observeData()
        setupUI()
    }

    private fun setupRecyclerView() {
        adapter = PengajuanIzinAdapter { item ->
            // Navigasi ke detail riwayat disetujui (Read-Only)
            val intent = Intent(this, DetailIzinDisetujuiActivity::class.java)
            intent.putExtra("PENGAJUAN_ID", item.id)
            startActivity(intent)
        }
        // Pastikan LayoutManager terpasang agar list muncul
        binding.rvPengajuan.layoutManager = LinearLayoutManager(this)
        binding.rvPengajuan.adapter = adapter
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Memantau status DISETUJUI secara real-time
                viewModel.getPengajuanByStatus(AppConstants.STATUS_DISETUJUI).collect { list ->
                    adapter.submitList(list)
                    
                    // Logika Visibilitas: Tampilkan list jika data ada
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
        binding.btnBack.setOnClickListener { finish() }

        // Navigasi ke Halaman Approval Izin (Panel Kontrol)
        binding.btnApproval.setOnClickListener {
            startActivity(Intent(this, ApprovalIzinActivity::class.java))
        }

        binding.btnTambahPengajuan.setOnClickListener {
            val intent = Intent(this, PengajuanIzinActivity::class.java)
            startActivity(intent)
        }

        // Navigasi Antar Tab Riwayat
        binding.tabDiajukan.setOnClickListener {
            startActivity(Intent(this, DaftarPengajuanIzinActivity::class.java))
            finish()
        }

        binding.tabDitolak.setOnClickListener {
            startActivity(Intent(this, DaftarPengajuanIzinDitolakActivity::class.java))
            finish()
        }
    }
}

package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
        enableEdgeToEdge()
        binding = ActivityDaftarPengajuanIzinDiajukanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.statusBarSpacer.updateLayoutParams { height = systemBars.top }
            
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
            // SISI RIWAYAT: Diarahkan ke DetailIzinActivity (Read-Only)
            val intent = Intent(this, DetailIzinActivity::class.java)
            intent.putExtra("PENGAJUAN_ID", item.id)
            startActivity(intent)
        }
        binding.rvPengajuan.layoutManager = LinearLayoutManager(this)
        binding.rvPengajuan.adapter = adapter
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Memantau status DIAJUKAN secara real-time
                viewModel.getPengajuanByStatus(AppConstants.STATUS_DIAJUKAN).collect { list ->
                    adapter.submitList(list)
                    
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

        // PERBAIKAN: Listener Tab untuk pindah halaman
        binding.tabDisetujui.setOnClickListener {
            val intent = Intent(this, DaftarPengajuanBaruActivity::class.java)
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

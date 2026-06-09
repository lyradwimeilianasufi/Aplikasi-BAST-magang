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
import com.example.aplikasibast.databinding.ActivityApprovalIzinDisetujuiBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ApprovalIzinSelesaiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApprovalIzinDisetujuiBinding
    private val viewModel: MainViewModel by viewModel()
    private lateinit var adapter: PengajuanIzinAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityApprovalIzinDisetujuiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        observeData()
        setupUI()
    }

    private fun setupRecyclerView() {
        // Tampilkan Nama Teknisi di halaman Approval
        adapter = PengajuanIzinAdapter(showNamaTeknisi = true) { item ->
            // Menuju detail pengajuan yang sudah disetujui
            val intent = Intent(this, DetailIzinDisetujuiActivity::class.java)
            intent.putExtra("PENGAJUAN_ID", item.id)
            startActivity(intent)
        }
        binding.rvApprovalDisetujui.layoutManager = LinearLayoutManager(this)
        binding.rvApprovalDisetujui.adapter = adapter
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getPengajuanByStatus("DISETUJUI").collect { list ->
                    adapter.submitList(list)
                    binding.rvApprovalDisetujui.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
                    binding.emptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }

        binding.tabPengajuan.setOnClickListener {
            startActivity(Intent(this, ApprovalIzinActivity::class.java))
            finish()
        }
        
        binding.tabDitolak.setOnClickListener {
            startActivity(Intent(this, ApprovalIzinDitolakActivity::class.java))
            finish()
        }
    }
}

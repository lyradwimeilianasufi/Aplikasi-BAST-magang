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
import com.example.aplikasibast.databinding.ActivityApprovalIzinDiajukanBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ApprovalIzinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApprovalIzinDiajukanBinding
    private val viewModel: MainViewModel by viewModel()
    private lateinit var adapter: PengajuanIzinAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityApprovalIzinDiajukanBinding.inflate(layoutInflater)
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
        // Gunakan adapter yang sama, atau buat khusus approval jika layout berbeda
        adapter = PengajuanIzinAdapter { item ->
            val intent = Intent(this, DetailPengajuanIzinActivity::class.java)
            intent.putExtra("PENGAJUAN_ID", item.id)
            startActivity(intent)
        }
        binding.rvApproval.layoutManager = LinearLayoutManager(this)
        binding.rvApproval.adapter = adapter
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Admin melihat SEMUA pengajuan yang berstatus DIAJUKAN
                viewModel.getPengajuanByStatus("DIAJUKAN").collect { list ->
                    adapter.submitList(list)
                    binding.rvApproval.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
                    binding.emptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }
        
        binding.tabDisetujui.setOnClickListener {
            startActivity(Intent(this, ApprovalIzinSelesaiActivity::class.java))
            finish()
        }
        binding.tabDitolak.setOnClickListener {
            startActivity(Intent(this, ApprovalIzinDitolakActivity::class.java))
            finish()
        }
    }
}

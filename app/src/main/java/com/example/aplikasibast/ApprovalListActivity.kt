package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasibast.databinding.ActivityApprovalListBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ApprovalListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApprovalListBinding
    private val viewModel: MainViewModel by viewModel()
    private lateinit var adapter: PengajuanIzinAdapter
    
    private val selectedStatus = MutableStateFlow(AppConstants.STATUS_DIAJUKAN)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityApprovalListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInsets()
        setupRecyclerView()
        setupTabListeners()
        setupActionListeners()
        observeData()
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top, bottom = systemBars.bottom)
            insets
        }
    }

    private fun setupRecyclerView() {
        // Tampilkan Nama Teknisi di halaman Approval List
        adapter = PengajuanIzinAdapter(showNamaTeknisi = true) { item ->
            // Navigasi ke detail berdasarkan status untuk aksi admin
            val detailClass = when (item.status) {
                AppConstants.STATUS_DISETUJUI -> DetailPengajuanActivity::class.java
                AppConstants.STATUS_DITOLAK -> DetailPengajuanDitolakActivity::class.java
                else -> DetailPengajuanIzinActivity::class.java
            }
            startActivity(Intent(this, detailClass).apply {
                putExtra("PENGAJUAN_ID", item.id)
            })
        }
        binding.rvApproval.layoutManager = LinearLayoutManager(this)
        binding.rvApproval.adapter = adapter
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectedStatus.collectLatest { status ->
                    updateTabUI(status)
                    viewModel.getPengajuanByStatus(status).collect { list ->
                        adapter.submitList(list)
                        binding.rvApproval.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
                        binding.emptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                        
                        binding.tvEmptyMessage.text = when(status) {
                            AppConstants.STATUS_DIAJUKAN -> "Tidak ada pengajuan baru"
                            AppConstants.STATUS_DISETUJUI -> "Belum ada data disetujui"
                            else -> "Belum ada data ditolak"
                        }
                    }
                }
            }
        }
    }

    private fun setupTabListeners() {
        binding.tabDiajukan.setOnClickListener { selectedStatus.value = AppConstants.STATUS_DIAJUKAN }
        binding.tabDisetujui.setOnClickListener { selectedStatus.value = AppConstants.STATUS_DISETUJUI }
        binding.tabDitolak.setOnClickListener { selectedStatus.value = AppConstants.STATUS_DITOLAK }
    }

    private fun updateTabUI(activeStatus: String) {
        resetTabStyle(binding.tabDiajukan)
        resetTabStyle(binding.tabDisetujui)
        resetTabStyle(binding.tabDitolak)

        val activeView = when (activeStatus) {
            AppConstants.STATUS_DISETUJUI -> binding.tabDisetujui
            AppConstants.STATUS_DITOLAK -> binding.tabDitolak
            else -> binding.tabDiajukan
        }
        
        activeView.setBackgroundResource(R.drawable.bg_tab_selected)
        activeView.backgroundTintList = ContextCompat.getColorStateList(this, R.color.purple_dark)
        activeView.setTextColor(ContextCompat.getColor(this, R.color.white))
    }

    private fun resetTabStyle(textView: TextView) {
        textView.setBackgroundResource(R.drawable.bg_tab_unselected)
        textView.backgroundTintList = null
        textView.setTextColor(ContextCompat.getColor(this, R.color.purple_badge_text))
    }

    private fun setupActionListeners() {
        binding.btnBack.setOnClickListener { finish() }
    }
}

package com.example.aplikasibast.features.permission.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasibast.R
import com.example.aplikasibast.core.constants.AppConstants
import com.example.aplikasibast.databinding.ActivityDaftarPengajuanIzinBinding
import com.example.aplikasibast.features.approval.presentation.activity.ApprovalListActivity
import com.example.aplikasibast.features.permission.presentation.adapter.PengajuanIzinAdapter
import com.example.aplikasibast.features.permission.presentation.viewmodel.PermissionViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DaftarPengajuanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDaftarPengajuanIzinBinding
    private val viewModel: PermissionViewModel by viewModel()
    private lateinit var adapter: PengajuanIzinAdapter
    
    private val selectedStatus = MutableStateFlow(AppConstants.STATUS_DIAJUKAN)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDaftarPengajuanIzinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInsets()
        setupRecyclerView()
        setupTabListeners()
        setupActionListeners()
        observeData()
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.statusBarSpacer.updateLayoutParams { height = systemBars.top }
            
            val density = resources.displayMetrics.density
            val padding20dp = (20 * density).toInt()
            binding.btnTambahContainer.updatePadding(bottom = systemBars.bottom + padding20dp)
            insets
        }
    }

    private fun setupRecyclerView() {
        adapter = PengajuanIzinAdapter { item ->
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
                selectedStatus.collectLatest { status ->
                    updateTabUI(status)
                    viewModel.getPengajuanByStatus(status).collect { list ->
                        adapter.submitList(list)
                        binding.rvPengajuan.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
                        binding.scrollViewEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
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
        binding.btnApproval.setOnClickListener {
            startActivity(Intent(this, ApprovalListActivity::class.java))
        }
        binding.btnTambahPengajuan.setOnClickListener {
            startActivity(Intent(this, PengajuanIzinActivity::class.java))
        }
    }
}

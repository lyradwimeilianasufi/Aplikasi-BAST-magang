package com.example.aplikasibast.features.attendance.presentation.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasibast.R
import com.example.aplikasibast.core.utils.NavigationHelper
import com.example.aplikasibast.databinding.ActivityRiwayatKehadiranBinding
import com.example.aplikasibast.features.attendance.domain.model.RiwayatItem
import com.example.aplikasibast.features.attendance.presentation.adapter.RiwayatKehadiranAdapter
import com.example.aplikasibast.features.attendance.presentation.viewmodel.AttendanceViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RiwayatKehadiranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatKehadiranBinding
    private val viewModel: AttendanceViewModel by viewModel()
    private lateinit var riwayatAdapter: RiwayatKehadiranAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRiwayatKehadiranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbarLayout.updatePadding(top = systemBars.top)
            insets
        }

        setupUI()
        setupNavigation()
        setupRecyclerView()
        observeData()
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigation.bottomNavigation.selectedItemId = R.id.nav_riwayat
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupNavigation() {
        binding.bottomNavigation.bottomNavigation.setOnItemSelectedListener { item ->
            NavigationHelper.handleBottomNavigation(this, item.itemId)
        }
    }

    private fun setupRecyclerView() {
        riwayatAdapter = RiwayatKehadiranAdapter { item ->
            val intent = Intent(this, DetailKehadiranActivity::class.java)
            val id = when(item) {
                is RiwayatItem.KehadiranData -> item.id
                is RiwayatItem.IzinData -> item.id
                is RiwayatItem.AlpaData -> item.id
                is RiwayatItem.LiburData -> item.id
                is RiwayatItem.SakitData -> item.id
            }
            intent.putExtra("KEHADIRAN_ID", id)
            startActivity(intent)
        }

        binding.rvRiwayatKehadiran.layoutManager = LinearLayoutManager(this)
        binding.rvRiwayatKehadiran.adapter = riwayatAdapter
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.combinedRiwayat.collect { list ->
                riwayatAdapter.submitList(list)
            }
        }
    }
}

package com.example.aplikasibast.features.attendance.presentation.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasibast.R
import com.example.aplikasibast.core.utils.NavigationHelper
import com.example.aplikasibast.databinding.ActivityKehadiranBinding
import com.example.aplikasibast.features.attendance.domain.model.RiwayatItem
import com.example.aplikasibast.features.attendance.presentation.adapter.RiwayatKehadiranAdapter
import com.example.aplikasibast.features.attendance.presentation.viewmodel.AttendanceViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class KehadiranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKehadiranBinding
    private val viewModel: AttendanceViewModel by viewModel()
    private lateinit var adapter: RiwayatKehadiranAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityKehadiranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            binding.bottomNavigationInclude.bottomNavigation.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupNavigation()
        setupListeners()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = RiwayatKehadiranAdapter { item ->
            val intent = when (item) {
                is RiwayatItem.IzinData, is RiwayatItem.SakitData ->
                    Intent(this, DetailKehadiranIzinActivity::class.java)
                else ->
                    Intent(this, DetailKehadiranActivity::class.java)
            }

            val id = when(item) {
                is RiwayatItem.KehadiranData -> item.id
                is RiwayatItem.IzinData -> item.id
                is RiwayatItem.AlpaData -> item.id
                is RiwayatItem.LiburData -> item.id
                is RiwayatItem.SakitData -> item.id
            }
            intent.putExtra("KEHADIRAN_ID", id)
            intent.putExtra("TANGGAL", item.rawDate)
            startActivity(intent)
        }
        
        binding.rvKehadiranPreview.layoutManager = LinearLayoutManager(this)
        binding.rvKehadiranPreview.adapter = adapter
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.combinedRiwayat.collect { list ->
                adapter.submitList(list.take(5)) // Hanya tampilkan preview
                updateSummary(list)
            }
        }
    }

    private fun updateSummary(list: List<RiwayatItem>) {
        val hadirCount = list.count { it is RiwayatItem.KehadiranData }
        val izinCount = list.count { it is RiwayatItem.IzinData || it is RiwayatItem.SakitData }
        val alpaCount = list.count { it is RiwayatItem.AlpaData }

        binding.tvCountHadir.text = "$hadirCount Hari"
        binding.tvCountIzin.text = "$izinCount Hari"
        binding.tvCountAlpa.text = "$alpaCount Hari"
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigationInclude.bottomNavigation.selectedItemId = R.id.nav_kehadiran
    }

    private fun setupListeners() {
        binding.tvLihatSemuaKehadiran.setOnClickListener {
            startActivity(Intent(this, RiwayatKehadiranActivity::class.java))
        }
    }

    private fun setupNavigation() {
        binding.bottomNavigationInclude.bottomNavigation.setOnItemSelectedListener { item ->
            NavigationHelper.handleBottomNavigation(this, item.itemId)
        }
    }
}

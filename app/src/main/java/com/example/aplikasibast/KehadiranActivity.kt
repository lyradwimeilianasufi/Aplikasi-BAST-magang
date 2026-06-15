package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasibast.attendancehistory.presentation.activity.RiwayatKehadiranActivity
import com.example.aplikasibast.attendancehistory.presentation.adapter.RiwayatKehadiranAdapter
import com.example.aplikasibast.databinding.ActivityKehadiranBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class KehadiranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKehadiranBinding
    private val viewModel: MainViewModel by viewModel()
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
            val intent = Intent(this, DetailKehadiranActivity::class.java)
            val id = when (item) {
                is RiwayatItem.KehadiranData -> item.id
                is RiwayatItem.IzinData -> item.id
                is RiwayatItem.SakitData -> item.id
                is RiwayatItem.AlpaData -> item.id
                is RiwayatItem.LiburData -> item.id
            }
            intent.putExtra("KEHADIRAN_ID", id)
            startActivity(intent)
        }
        
        binding.rvKehadiranPreview.apply {
            layoutManager = LinearLayoutManager(this@KehadiranActivity)
            this.adapter = this@KehadiranActivity.adapter
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            // Menggunakan combinedRiwayat agar pengajuan izin/sakit yang disetujui juga muncul di preview
            viewModel.combinedRiwayat.collect { items ->
                adapter.submitList(items.take(5)) // Tampilkan 5 terbaru saja di halaman utama
                updateSummary(items)
            }
        }
    }

    private fun updateSummary(items: List<RiwayatItem>) {
        val hadirCount = items.count { 
            it is RiwayatItem.KehadiranData && (it.status.equals("Hadir", true) || it.status.equals("Telat", true)) 
        }
        val izinCount = items.count { it is RiwayatItem.IzinData || it is RiwayatItem.SakitData }
        val alpaCount = items.count { it is RiwayatItem.AlpaData }

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
            val intent = Intent(this, RiwayatKehadiranActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
    }

    private fun setupNavigation() {
        binding.bottomNavigationInclude.bottomNavigation.setOnItemSelectedListener { item ->
            NavigationHelper.handleBottomNavigation(this, item.itemId)
        }
    }
}

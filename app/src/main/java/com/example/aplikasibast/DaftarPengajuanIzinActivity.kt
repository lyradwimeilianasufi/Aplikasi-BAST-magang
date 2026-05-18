package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
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

        // Penanganan Insets secara spesifik agar elemen tidak tertutup sistem bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout) { _, insets ->
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            
            // 1. Atur tinggi spacer status bar agar toolbar tidak tertutup
            val spacerParams = binding.statusBarSpacer.layoutParams
            if (spacerParams.height != statusBars.top) {
                spacerParams.height = statusBars.top
                binding.statusBarSpacer.layoutParams = spacerParams
            }
            
            // 2. Angkat kontainer tombol di atas navigasi bar HP
            // Padding asli 20dp dari XML + tinggi navigasi bar sistem
            val density = resources.displayMetrics.density
            val paddingNormal = (20 * density).toInt()
            
            // Menggunakan setPadding untuk memastikan semua sisi konsisten dan bottom terangkat
            binding.btnTambahContainer.setPadding(
                paddingNormal, // left
                paddingNormal, // top
                paddingNormal, // right
                navBars.bottom + paddingNormal // bottom (nav bar + spacing)
            )
            
            insets
        }

        setupRecyclerView()
        observeData()
        setupUI()
    }

    private fun setupRecyclerView() {
        adapter = PengajuanIzinAdapter { item ->
            val intent = Intent(this, DetailPengajuanIzinActivity::class.java)
            startActivity(intent)
        }
        binding.rvPengajuan.layoutManager = LinearLayoutManager(this)
        binding.rvPengajuan.adapter = adapter
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.getPengajuanByStatus("DIAJUKAN").collect { list ->
                adapter.submitList(list)
            }
        }
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnTambahPengajuan.setOnClickListener {
            val intent = Intent(this, PengajuanIzinActivity::class.java)
            startActivity(intent)
        }

        // Tab click listeners
        binding.tabDisetujui.setOnClickListener {
            startActivity(Intent(this, DaftarPengajuanBaruActivity::class.java))
            finish()
        }
        
        binding.tabDitolak.setOnClickListener {
            startActivity(Intent(this, DaftarPengajuanIzinDitolakActivity::class.java))
            finish()
        }
    }
}

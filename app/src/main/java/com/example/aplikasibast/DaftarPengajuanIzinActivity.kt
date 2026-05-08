package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplikasibast.databinding.ActivityDaftarPengajuanIzinDiajukanBinding

class DaftarPengajuanIzinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDaftarPengajuanIzinDiajukanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDaftarPengajuanIzinDiajukanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupUI()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnTambahPengajuan.setOnClickListener {
            val intent = Intent(this, PengajuanIzinActivity::class.java)
            startActivity(intent)
        }

        // Navigasi ke Detail Izin (Halaman baru yang sesuai desain) saat kartu diklik
        binding.itemPengajuan1.cvItemRiwayat.setOnClickListener {
            val intent = Intent(this, DetailIzinActivity::class.java)
            startActivity(intent)
        }

        // Tab click listeners (UI Only for now)
        binding.tabDiajukan.setOnClickListener { updateTabs(0) }
        binding.tabDisetujui.setOnClickListener { updateTabs(1) }
        binding.tabDitolak.setOnClickListener { updateTabs(2) }
    }

    private fun updateTabs(position: Int) {
        // Reset Tabs
        val tabs = listOf(binding.tabDiajukan, binding.tabDisetujui, binding.tabDitolak)
        tabs.forEach {
            it.setBackgroundResource(R.drawable.bg_tab_unselected)
            it.setTextColor(getColor(R.color.purple_badge_text))
            it.backgroundTintList = null
        }

        // Set Selected
        tabs[position].setBackgroundResource(R.drawable.bg_tab_selected)
        tabs[position].backgroundTintList = android.content.res.ColorStateList.valueOf(getColor(R.color.purple_dark))
        tabs[position].setTextColor(getColor(R.color.white))
    }
}

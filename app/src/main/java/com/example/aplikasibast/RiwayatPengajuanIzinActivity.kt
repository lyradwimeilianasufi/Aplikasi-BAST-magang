package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplikasibast.databinding.ActivityRiwayatPengajuanIzinBinding

class RiwayatPengajuanIzinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatPengajuanIzinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRiwayatPengajuanIzinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnTambahPengajuan.setOnClickListener {
            val intent = Intent(this, PengajuanIzinActivity::class.java)
            startActivity(intent)
        }

        // Contoh switch tab sederhana (UI Only)
        binding.tabDiajukan.setOnClickListener {
            updateTabSelection(it.id)
        }
        binding.tabDisetujui.setOnClickListener {
            updateTabSelection(it.id)
        }
        binding.tabDitolak.setOnClickListener {
            updateTabSelection(it.id)
        }
    }

    private fun updateTabSelection(selectedId: Int) {
        // Reset all
        binding.tabDiajukan.setBackgroundResource(R.drawable.bg_tab_unselected)
        binding.tabDiajukan.setTextColor(getColor(R.color.purple_badge_text))
        
        binding.tabDisetujui.setBackgroundResource(R.drawable.bg_tab_unselected)
        binding.tabDisetujui.setTextColor(getColor(R.color.purple_badge_text))
        
        binding.tabDitolak.setBackgroundResource(R.drawable.bg_tab_unselected)
        binding.tabDitolak.setTextColor(getColor(R.color.purple_badge_text))

        // Set selected
        when (selectedId) {
            R.id.tabDiajukan -> {
                binding.tabDiajukan.setBackgroundResource(R.drawable.bg_tab_selected)
                binding.tabDiajukan.backgroundTintList = getColorStateList(R.color.purple_dark)
                binding.tabDiajukan.setTextColor(getColor(R.color.white))
            }
            R.id.tabDisetujui -> {
                binding.tabDisetujui.setBackgroundResource(R.drawable.bg_tab_selected)
                binding.tabDisetujui.backgroundTintList = getColorStateList(R.color.purple_dark)
                binding.tabDisetujui.setTextColor(getColor(R.color.white))
            }
            R.id.tabDitolak -> {
                binding.tabDitolak.setBackgroundResource(R.drawable.bg_tab_selected)
                binding.tabDitolak.backgroundTintList = getColorStateList(R.color.purple_dark)
                binding.tabDitolak.setTextColor(getColor(R.color.white))
            }
        }
    }
}

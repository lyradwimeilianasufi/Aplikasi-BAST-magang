package com.example.aplikasibast

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplikasibast.databinding.ActivityDaftarPengajuanIzinDiajukanBinding

class DaftarPengajuanBaruActivity : AppCompatActivity() {

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

        // Logic switch tab (UI Only)
        binding.tabDiajukan.setOnClickListener { updateTabSelection(0) }
        binding.tabDisetujui.setOnClickListener { updateTabSelection(1) }
        binding.tabDitolak.setOnClickListener { updateTabSelection(2) }
    }

    private fun updateTabSelection(index: Int) {
        val tabs = listOf(binding.tabDiajukan, binding.tabDisetujui, binding.tabDitolak)
        tabs.forEachIndexed { i, textView ->
            if (i == index) {
                textView.setBackgroundResource(R.drawable.bg_tab_selected)
                textView.backgroundTintList = android.content.res.ColorStateList.valueOf(getColor(R.color.purple_dark))
                textView.setTextColor(getColor(R.color.white))
            } else {
                textView.setBackgroundResource(R.drawable.bg_tab_unselected)
                textView.backgroundTintList = null
                textView.setTextColor(getColor(R.color.purple_badge_text))
            }
        }
    }
}

package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplikasibast.databinding.ActivityApprovalIzinSelesaiBinding

class ApprovalIzinSelesaiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApprovalIzinSelesaiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityApprovalIzinSelesaiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUI()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Tab click listeners untuk navigasi antar status
        binding.tabPengajuan.setOnClickListener {
            startActivity(Intent(this, ApprovalIzinActivity::class.java))
            finish()
        }
        
        binding.tabDitolak.setOnClickListener {
            startActivity(Intent(this, ApprovalIzinDitolakActivity::class.java))
            finish()
        }
    }
}

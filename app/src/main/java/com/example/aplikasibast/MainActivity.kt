package com.example.aplikasibast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.aplikasibast.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. Aktifkan Edge-to-Edge
        enableEdgeToEdge()
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 2. Handle Insets secara spesifik
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            // Berikan padding atas ke root agar tidak tertutup Status Bar
            v.updatePadding(top = systemBars.top)
            
            // Berikan padding bawah KHUSUS ke BottomNavigationView 
            // agar ikon tidak tertutup tombol navigasi sistem (ada yang menutupi)
            binding.bottomNavigation.updatePadding(bottom = systemBars.bottom)
            
            insets
        }

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        binding.tvUserName.text = viewModel.userName
        binding.tvUserRole.text = viewModel.userRole
        binding.tvCurrentDate.text = viewModel.currentDay
        binding.tvWorkHours.text = viewModel.workHours
        binding.bottomNavigation.selectedItemId = R.id.nav_beranda
    }

    private fun setupListeners() {
        binding.btnAbsenMasukMain.setOnClickListener {
            startActivity(Intent(this, LocationAbsenActivity::class.java))
        }
    }
}

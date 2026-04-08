package com.example.aplikasibast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasibast.databinding.ActivityPreviewFotoAbsenBinding

class PreviewFotoAbsenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreviewFotoAbsenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewFotoAbsenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Logika saat tombol X ditekan (kembali ke kamera)
        binding.btnCancel.setOnClickListener {
            finish()
        }

        // Logika saat tombol Check ditekan (simpan absen)
        binding.btnConfirm.setOnClickListener {
            // Tambahkan logika simpan atau kirim ke server di sini
            finish()
        }
    }
}
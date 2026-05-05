package com.example.aplikasibast

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasibast.databinding.ActivityPreviewFotoAbsenBinding
import java.io.File

class PreviewFotoAbsenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreviewFotoAbsenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewFotoAbsenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val photoPath = intent.getStringExtra("FILE_PATH")
        if (photoPath != null) {
            val photoFile = File(photoPath)
            if (photoFile.exists()) {
                binding.ivPreview.setImageURI(Uri.fromFile(photoFile))
            }
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnConfirm.setOnClickListener {
            // Kembali ke MainActivity dan tampilkan dialog sukses
            val intent = Intent(this, MainActivity::class.java)
            // FLAG_ACTIVITY_CLEAR_TOP akan menutup activity Lokasi & Kamera
            // FLAG_ACTIVITY_SINGLE_TOP memastikan kita kembali ke instance MainActivity yang sudah ada
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            intent.putExtra("SHOW_SUCCESS_DIALOG", true)
            startActivity(intent)
            finish()
        }
    }
}

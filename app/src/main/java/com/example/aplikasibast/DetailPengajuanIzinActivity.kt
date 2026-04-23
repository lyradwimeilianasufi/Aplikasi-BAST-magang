package com.example.aplikasibast

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplikasibast.databinding.ActivityDetailPengajuanIzinBinding
import com.example.aplikasibast.databinding.DialogTolakPengajuanBinding

class DetailPengajuanIzinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPengajuanIzinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailPengajuanIzinBinding.inflate(layoutInflater)
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
        
        binding.btnTolak.setOnClickListener {
            showTolakDialog()
        }
        
        binding.btnSetujui.setOnClickListener {
            // Logic for approving
        }
    }

    private fun showTolakDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = DialogTolakPengajuanBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        // 1. Buat background window dialog transparan agar rounded corner CardView terlihat
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.btnClose.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnBatal.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnSimpan.setOnClickListener {
            val alasan = dialogBinding.etAlasan.text.toString()
            if (alasan.isNotEmpty()) {
                dialog.dismiss()
            } else {
                dialogBinding.tilAlasan.error = "Alasan tidak boleh kosong"
            }
        }

        // 2. Tampilkan dialog terlebih dahulu
        dialog.show()

        // 3. Atur LayoutParams SETELAH dialog.show() untuk memastikan posisi di TENGAH
        dialog.window?.let { window ->
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(window.attributes)
            
            val displayMetrics = resources.displayMetrics
            // Lebar 85% dari layar agar proporsional
            layoutParams.width = (displayMetrics.widthPixels * 0.85).toInt()
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            
            // PAKSA POSISI KE TENGAH LAYAR
            layoutParams.gravity = Gravity.CENTER
            
            window.attributes = layoutParams
        }
    }
}

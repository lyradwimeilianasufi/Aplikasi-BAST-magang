package com.example.aplikasibast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasibast.databinding.ItemRiwayatPengajuanDiajukanBinding

class PengajuanIzinAdapter(private val onClick: (PengajuanIzinEntity) -> Unit) :
    ListAdapter<PengajuanIzinEntity, PengajuanIzinAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(val binding: ItemRiwayatPengajuanDiajukanBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRiwayatPengajuanDiajukanBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvTanggalPengajuan.text = item.tanggalPengajuan
            tvJenisIzin.text = item.jenisIzin
            tvPeriodeIzin.text = "${item.tanggalMulai} - ${item.tanggalSelesai}"
            tvStatusBadge.text = item.status
            
            // Set durasi (Contoh sederhana: durasi statis atau hitung selisih tanggal)
            tvDurationValue.text = "1" // Anda bisa menambahkan logika hitung hari nanti

            // Atur warna badge berdasarkan status
            when (item.status) {
                "DIAJUKAN" -> {
                    tvStatusBadge.backgroundTintList = ContextCompat.getColorStateList(root.context, R.color.yellow_badge_bg)
                    tvStatusBadge.setTextColor(ContextCompat.getColor(root.context, R.color.yellow_badge_text))
                }
                "DISETUJUI" -> {
                    tvStatusBadge.backgroundTintList = ContextCompat.getColorStateList(root.context, R.color.green_badge_bg)
                    tvStatusBadge.setTextColor(ContextCompat.getColor(root.context, R.color.green_badge_text))
                }
                "DITOLAK" -> {
                    tvStatusBadge.backgroundTintList = ContextCompat.getColorStateList(root.context, R.color.red_badge_bg)
                    tvStatusBadge.setTextColor(ContextCompat.getColor(root.context, R.color.red_badge_text))
                }
            }

            root.setOnClickListener { onClick(item) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<PengajuanIzinEntity>() {
        override fun areItemsTheSame(oldItem: PengajuanIzinEntity, newItem: PengajuanIzinEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: PengajuanIzinEntity, newItem: PengajuanIzinEntity) = oldItem == newItem
    }
}

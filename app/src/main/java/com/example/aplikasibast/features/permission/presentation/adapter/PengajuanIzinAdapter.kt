package com.example.aplikasibast.features.permission.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasibast.R
import com.example.aplikasibast.core.constants.AppConstants
import com.example.aplikasibast.core.utils.DateUtils
import com.example.aplikasibast.databinding.ItemRiwayatPengajuanDiajukanBinding
import com.example.aplikasibast.features.permission.domain.model.PengajuanIzin

class PengajuanIzinAdapter(
    private val showNamaTeknisi: Boolean = false,
    private val onClick: (PengajuanIzin) -> Unit
) : ListAdapter<PengajuanIzin, PengajuanIzinAdapter.ViewHolder>(DiffCallback) {

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
            // Memformat Tanggal Pengajuan
            tvTanggalPengajuan.text = DateUtils.formatToUi(item.tanggalPengajuan)
            
            tvJenisIzin.text = item.jenisIzin
            
            // Memformat Periode Izin (Mulai - Selesai)
            val tglMulai = DateUtils.formatToUi(item.tanggalMulai)
            val tglSelesai = DateUtils.formatToUi(item.tanggalSelesai)
            tvPeriodeIzin.text = "$tglMulai - $tglSelesai"

            tvStatusBadge.text = item.status
            
            if (showNamaTeknisi) {
                tvNamaTeknisi.text = item.teknisiNama
                tvNamaTeknisi.visibility = View.VISIBLE
                lblNamaTeknisi.visibility = View.VISIBLE
            } else {
                tvNamaTeknisi.visibility = View.GONE
                lblNamaTeknisi.visibility = View.GONE
            }

            tvDurationValue.text = DateUtils.calculateDays(item.tanggalMulai, item.tanggalSelesai).toString()

            when (item.status) {
                AppConstants.STATUS_DIAJUKAN -> {
                    tvStatusBadge.backgroundTintList = ContextCompat.getColorStateList(root.context, R.color.yellow_badge_bg)
                    tvStatusBadge.setTextColor(ContextCompat.getColor(root.context, R.color.yellow_badge_text))
                }
                AppConstants.STATUS_DISETUJUI -> {
                    tvStatusBadge.backgroundTintList = ContextCompat.getColorStateList(root.context, R.color.green_badge_bg)
                    tvStatusBadge.setTextColor(ContextCompat.getColor(root.context, R.color.green_badge_text))
                }
                AppConstants.STATUS_DITOLAK -> {
                    tvStatusBadge.backgroundTintList = ContextCompat.getColorStateList(root.context, R.color.red_badge_bg)
                    tvStatusBadge.setTextColor(ContextCompat.getColor(root.context, R.color.red_badge_text))
                }
            }

            root.setOnClickListener { onClick(item) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<PengajuanIzin>() {
        override fun areItemsTheSame(oldItem: PengajuanIzin, newItem: PengajuanIzin) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: PengajuanIzin, newItem: PengajuanIzin) = oldItem == newItem
    }
}

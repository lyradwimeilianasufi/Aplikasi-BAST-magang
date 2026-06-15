package com.example.aplikasibast.features.attendance.presentation.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasibast.databinding.ItemRiwayatAlpaBinding
import com.example.aplikasibast.databinding.ItemRiwayatHadirBinding
import com.example.aplikasibast.databinding.ItemRiwayatIzinBinding
import com.example.aplikasibast.databinding.ItemRiwayatLiburBinding
import com.example.aplikasibast.databinding.ItemRiwayatSakitBinding
import com.example.aplikasibast.features.attendance.domain.model.RiwayatItem

class RiwayatKehadiranAdapter(
    private val onItemClick: (RiwayatItem) -> Unit
) : ListAdapter<RiwayatItem, RecyclerView.ViewHolder>(DiffCallback) {

    companion object {
        private const val TYPE_KEHADIRAN = 0
        private const val TYPE_IZIN = 1
        private const val TYPE_ALPA = 2
        private const val TYPE_LIBUR = 3
        private const val TYPE_SAKIT = 4

        private val DiffCallback = object : DiffUtil.ItemCallback<RiwayatItem>() {
            override fun areItemsTheSame(oldItem: RiwayatItem, newItem: RiwayatItem): Boolean {
                return when {
                    oldItem is RiwayatItem.KehadiranData && newItem is RiwayatItem.KehadiranData -> oldItem.id == newItem.id
                    oldItem is RiwayatItem.IzinData && newItem is RiwayatItem.IzinData -> oldItem.id == newItem.id
                    oldItem is RiwayatItem.SakitData && newItem is RiwayatItem.SakitData -> oldItem.id == newItem.id
                    oldItem is RiwayatItem.AlpaData && newItem is RiwayatItem.AlpaData -> oldItem.id == newItem.id
                    oldItem is RiwayatItem.LiburData && newItem is RiwayatItem.LiburData -> oldItem.id == newItem.id
                    else -> false
                }
            }
            override fun areContentsTheSame(oldItem: RiwayatItem, newItem: RiwayatItem): Boolean = oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is RiwayatItem.KehadiranData -> TYPE_KEHADIRAN
            is RiwayatItem.IzinData -> TYPE_IZIN
            is RiwayatItem.AlpaData -> TYPE_ALPA
            is RiwayatItem.LiburData -> TYPE_LIBUR
            is RiwayatItem.SakitData -> TYPE_SAKIT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_KEHADIRAN -> KehadiranViewHolder(ItemRiwayatHadirBinding.inflate(inflater, parent, false))
            TYPE_IZIN -> IzinViewHolder(ItemRiwayatIzinBinding.inflate(inflater, parent, false))
            TYPE_SAKIT -> SakitViewHolder(ItemRiwayatSakitBinding.inflate(inflater, parent, false))
            TYPE_ALPA -> AlpaViewHolder(ItemRiwayatAlpaBinding.inflate(inflater, parent, false))
            TYPE_LIBUR -> LiburViewHolder(ItemRiwayatLiburBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is KehadiranViewHolder -> holder.bind(item as RiwayatItem.KehadiranData)
            is IzinViewHolder -> holder.bind(item as RiwayatItem.IzinData)
            is SakitViewHolder -> holder.bind(item as RiwayatItem.SakitData)
            is AlpaViewHolder -> holder.bind(item as RiwayatItem.AlpaData)
            is LiburViewHolder -> holder.bind(item as RiwayatItem.LiburData)
        }
    }

    inner class KehadiranViewHolder(private val binding: ItemRiwayatHadirBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RiwayatItem.KehadiranData) {
            binding.tvTanggal.text = formatTakeOverText(item.tanggal)
            binding.tvStatus.text = item.status
            if (item.status.equals("Telat", true) || item.status.equals("Hadir", true)) {
                binding.tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#27AE60"))
            }
            binding.tvJamMasuk.text = item.jamMasuk
            binding.tvJamKeluar.text = item.jamKeluar
            binding.tvTotalJam.text = item.totalJam
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    inner class IzinViewHolder(private val binding: ItemRiwayatIzinBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RiwayatItem.IzinData) {
            binding.tvTanggal.text = item.tanggal
            binding.tvStatus.text = "Izin"
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    inner class SakitViewHolder(private val binding: ItemRiwayatSakitBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RiwayatItem.SakitData) {
            binding.tvTanggal.text = item.tanggal
            binding.tvStatus.text = "Sakit"
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    inner class AlpaViewHolder(private val binding: ItemRiwayatAlpaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RiwayatItem.AlpaData) {
            binding.tvTanggal.text = item.tanggal
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    inner class LiburViewHolder(private val binding: ItemRiwayatLiburBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RiwayatItem.LiburData) {
            binding.tvTanggal.text = item.tanggal
            binding.tvStatus.text = "Libur"
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    private fun formatTakeOverText(text: String): CharSequence {
        if (!text.contains("[Take Over]")) return text
        val spannable = SpannableString(text)
        val start = text.indexOf("[Take Over]")
        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#FFB422")), start, start + 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
    }
}

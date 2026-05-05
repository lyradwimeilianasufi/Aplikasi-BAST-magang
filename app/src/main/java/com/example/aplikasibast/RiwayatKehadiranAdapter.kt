package com.example.aplikasibast

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasibast.databinding.ItemRiwayatAlpaBinding
import com.example.aplikasibast.databinding.ItemRiwayatIzinBinding
import com.example.aplikasibast.databinding.ItemRiwayatKehadiranBinding
import com.example.aplikasibast.databinding.ItemRiwayatLiburBinding

class RiwayatKehadiranAdapter(
    private val list: List<RiwayatItem>,
    private val onItemClick: (RiwayatItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_KEHADIRAN = 0
        private const val TYPE_IZIN = 1
        private const val TYPE_ALPA = 2
        private const val TYPE_LIBUR = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is RiwayatItem.KehadiranData -> TYPE_KEHADIRAN
            is RiwayatItem.IzinData -> TYPE_IZIN
            is RiwayatItem.AlpaData -> TYPE_ALPA
            is RiwayatItem.LiburData -> TYPE_LIBUR
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_KEHADIRAN -> {
                val binding = ItemRiwayatKehadiranBinding.inflate(inflater, parent, false)
                KehadiranViewHolder(binding)
            }
            TYPE_IZIN -> {
                val binding = ItemRiwayatIzinBinding.inflate(inflater, parent, false)
                IzinViewHolder(binding)
            }
            TYPE_ALPA -> {
                val binding = ItemRiwayatAlpaBinding.inflate(inflater, parent, false)
                AlpaViewHolder(binding)
            }
            TYPE_LIBUR -> {
                val binding = ItemRiwayatLiburBinding.inflate(inflater, parent, false)
                LiburViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        when (holder) {
            is KehadiranViewHolder -> holder.bind(item as RiwayatItem.KehadiranData)
            is IzinViewHolder -> holder.bind(item as RiwayatItem.IzinData)
            is AlpaViewHolder -> holder.bind(item as RiwayatItem.AlpaData)
            is LiburViewHolder -> holder.bind(item as RiwayatItem.LiburData)
        }
    }

    override fun getItemCount(): Int = list.size

    inner class KehadiranViewHolder(private val binding: ItemRiwayatKehadiranBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RiwayatItem.KehadiranData) {
            val text = item.tanggal
            if (text.contains("[Take Over]")) {
                val spannable = SpannableString(text)
                val start = text.indexOf("[Take Over]")
                val end = start + "[Take Over]".length
                spannable.setSpan(
                    ForegroundColorSpan(Color.parseColor("#FFB422")), // Warna Kuning/Oranye sesuai gambar
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                binding.tvTanggal.text = spannable
            } else {
                binding.tvTanggal.text = text
            }

            binding.tvStatus.text = item.status
            binding.tvJamMasuk.text = item.jamMasuk
            binding.tvJamKeluar.text = item.jamKeluar
            binding.tvTotalJam.text = item.totalJam
            
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    inner class IzinViewHolder(private val binding: ItemRiwayatIzinBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RiwayatItem.IzinData) {
            binding.tvTanggal.text = item.tanggal
            binding.tvStatus.text = "Izin"
            
            binding.tvJamMasuk.text = " - "
            binding.tvJamKeluar.text = " - "
            binding.tvTotalJam.text = " - "
            
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    inner class AlpaViewHolder(private val binding: ItemRiwayatAlpaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RiwayatItem.AlpaData) {
            binding.tvTanggal.text = item.tanggal
            
            binding.tvJamMasuk.text = " - "
            binding.tvJamKeluar.text = " - "
            binding.tvTotalJam.text = " - "
            
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    inner class LiburViewHolder(private val binding: ItemRiwayatLiburBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RiwayatItem.LiburData) {
            binding.tvTanggal.text = item.tanggal
            binding.tvStatus.text = "Libur"

            binding.tvJamMasuk.text = " - "
            binding.tvJamKeluar.text = " - "
            binding.tvTotalJam.text = " - "
            
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}

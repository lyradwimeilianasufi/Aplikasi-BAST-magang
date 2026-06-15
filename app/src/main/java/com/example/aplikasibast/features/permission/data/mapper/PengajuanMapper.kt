package com.example.aplikasibast.features.permission.data.mapper

import com.example.aplikasibast.features.permission.data.source.local.PengajuanIzinEntity
import com.example.aplikasibast.features.permission.domain.model.PengajuanIzin

object PengajuanMapper {
    fun toDomain(entity: PengajuanIzinEntity): PengajuanIzin {
        return PengajuanIzin(
            id = entity.id,
            tanggalPengajuan = entity.tanggalPengajuan,
            jenisIzin = entity.jenisIzin,
            tanggalMulai = entity.tanggalMulai,
            tanggalSelesai = entity.tanggalSelesai,
            alasan = entity.alasan,
            status = entity.status,
            lampiranPath = entity.lampiranPath,
            teknisiNama = entity.teknisiNama,
            alasanPenolakan = entity.alasanPenolakan,
            tanggalDiproses = entity.tanggalDiproses
        )
    }

    fun toEntity(domain: PengajuanIzin): PengajuanIzinEntity {
        return PengajuanIzinEntity(
            id = domain.id,
            tanggalPengajuan = domain.tanggalPengajuan,
            jenisIzin = domain.jenisIzin,
            tanggalMulai = domain.tanggalMulai,
            tanggalSelesai = domain.tanggalSelesai,
            alasan = domain.alasan,
            status = domain.status,
            lampiranPath = domain.lampiranPath,
            teknisiNama = domain.teknisiNama,
            alasanPenolakan = domain.alasanPenolakan,
            tanggalDiproses = domain.tanggalDiproses
        )
    }
}

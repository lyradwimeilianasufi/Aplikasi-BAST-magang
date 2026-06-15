package com.example.aplikasibast.data.mapper

import com.example.aplikasibast.KehadiranEntity
import com.example.aplikasibast.domain.model.Kehadiran

object KehadiranMapper {
    fun toDomain(entity: KehadiranEntity): Kehadiran {
        return Kehadiran(
            id = entity.id,
            tanggal = entity.tanggal,
            status = entity.status,
            jamMasuk = entity.jamMasuk,
            jamKeluar = entity.jamKeluar,
            totalJam = entity.totalJam,
            fotoMasukPath = entity.fotoMasukPath,
            fotoKeluarPath = entity.fotoKeluarPath,
            lokasiMasuk = entity.lokasiMasuk,
            lokasiKeluar = entity.lokasiKeluar,
            latMasuk = entity.latMasuk,
            lngMasuk = entity.lngMasuk,
            latKeluar = entity.latKeluar,
            lngKeluar = entity.lngKeluar
        )
    }

    fun toEntity(domain: Kehadiran): KehadiranEntity {
        return KehadiranEntity(
            id = domain.id,
            tanggal = domain.tanggal,
            status = domain.status,
            jamMasuk = domain.jamMasuk,
            jamKeluar = domain.jamKeluar,
            totalJam = domain.totalJam,
            fotoMasukPath = domain.fotoMasukPath,
            fotoKeluarPath = domain.fotoKeluarPath,
            lokasiMasuk = domain.lokasiMasuk,
            lokasiKeluar = domain.lokasiKeluar,
            latMasuk = domain.latMasuk,
            lngMasuk = domain.lngMasuk,
            latKeluar = domain.latKeluar,
            lngKeluar = domain.lngKeluar
        )
    }
}

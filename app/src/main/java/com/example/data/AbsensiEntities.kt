package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val password: String, // Tanpa enkripsi sesuai spesifikasi
    val namaLengkap: String,
    val role: String
)

@Entity(tableName = "kelas")
data class KelasEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val kodeKelas: String,
    val namaKelas: String,
    val waliKelas: String,
    val tahunAjaran: String
)

@Entity(tableName = "siswa")
data class SiswaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nis: String,
    val namaSiswa: String,
    val jenisKelamin: String,
    val namaKelas: String,
    val alamat: String
)

@Entity(tableName = "kelompok")
data class KelompokEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val namaKelompok: String,
    val namaKelas: String,
    val namaSiswa: String,
    val peranKelompok: String,
    val topikTugas: String
)

@Entity(tableName = "absensi")
data class AbsensiEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tanggal: String,
    val namaKelas: String,
    val nis: String,
    val namaSiswa: String,
    val status: String, // Hadir, Izin, Sakit, Alfa
    val keterangan: String
)

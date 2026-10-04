package com.example.data

import kotlinx.coroutines.flow.Flow

class AbsensiRepository(private val dao: AbsensiDao) {

    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()
    val allKelas: Flow<List<KelasEntity>> = dao.getAllKelas()
    val allSiswa: Flow<List<SiswaEntity>> = dao.getAllSiswa()
    val allKelompok: Flow<List<KelompokEntity>> = dao.getAllKelompok()
    val allAbsensi: Flow<List<AbsensiEntity>> = dao.getAllAbsensi()

    /**
     * Otomatis membuat 2 data dummy di setiap tabel saat pertama kali dijalankan
     */
    suspend fun ensureDummyDataSeeded() {
        if (dao.getUsersCount() == 0) {
            seedUsersDummy()
        }
        if (dao.getKelasCount() == 0) {
            seedKelasDummy()
        }
        if (dao.getSiswaCount() == 0) {
            seedSiswaDummy()
        }
        if (dao.getKelompokCount() == 0) {
            seedKelompokDummy()
        }
        if (dao.getAbsensiCount() == 0) {
            seedAbsensiDummy()
        }
    }

    /**
     * Reset seluruh database kembali menjadi 2 data dummy per tabel
     */
    suspend fun resetAllToTwoDummyRows() {
        dao.clearUsers()
        dao.clearKelas()
        dao.clearSiswa()
        dao.clearKelompok()
        dao.clearAbsensi()

        seedUsersDummy()
        seedKelasDummy()
        seedSiswaDummy()
        seedKelompokDummy()
        seedAbsensiDummy()
    }

    private suspend fun seedUsersDummy() {
        dao.insertUser(
            UserEntity(
                username = "admin",
                password = "admin123",
                namaLengkap = "Drs. Hendra Wijaya",
                role = "Admin"
            )
        )
        dao.insertUser(
            UserEntity(
                username = "guru",
                password = "guru123",
                namaLengkap = "Rina Kartika, S.Pd",
                role = "Guru"
            )
        )
    }

    private suspend fun seedKelasDummy() {
        dao.insertKelas(
            KelasEntity(
                kodeKelas = "X-IPA-1",
                namaKelas = "Kelas X IPA 1",
                waliKelas = "Rina Kartika, S.Pd",
                tahunAjaran = "2026/2027"
            )
        )
        dao.insertKelas(
            KelasEntity(
                kodeKelas = "X-IPS-1",
                namaKelas = "Kelas X IPS 1",
                waliKelas = "Bambang Sutrisno, M.Pd",
                tahunAjaran = "2026/2027"
            )
        )
    }

    private suspend fun seedSiswaDummy() {
        dao.insertSiswa(
            SiswaEntity(
                nis = "2026001",
                namaSiswa = "Ahmad Fauzi",
                jenisKelamin = "Laki-laki",
                namaKelas = "Kelas X IPA 1",
                alamat = "Jl. Merdeka No. 12, Jakarta"
            )
        )
        dao.insertSiswa(
            SiswaEntity(
                nis = "2026002",
                namaSiswa = "Siti Nurhaliza",
                jenisKelamin = "Perempuan",
                namaKelas = "Kelas X IPS 1",
                alamat = "Jl. Sudirman No. 45, Jakarta"
            )
        )
    }

    private suspend fun seedKelompokDummy() {
        dao.insertKelompok(
            KelompokEntity(
                namaKelompok = "Kelompok 1 - Sains Terpadu",
                namaKelas = "Kelas X IPA 1",
                namaSiswa = "Ahmad Fauzi",
                peranKelompok = "Ketua Kelompok",
                topikTugas = "Praktikum Biologi Sel & Jaringan"
            )
        )
        dao.insertKelompok(
            KelompokEntity(
                namaKelompok = "Kelompok 2 - Ekonomi Nusantara",
                namaKelas = "Kelas X IPS 1",
                namaSiswa = "Siti Nurhaliza",
                peranKelompok = "Ketua Kelompok",
                topikTugas = "Analisis Pasar Modal Indonesia"
            )
        )
    }

    private suspend fun seedAbsensiDummy() {
        dao.insertAbsensi(
            AbsensiEntity(
                tanggal = "2026-10-04",
                namaKelas = "Kelas X IPA 1",
                nis = "2026001",
                namaSiswa = "Ahmad Fauzi",
                status = "Hadir",
                keterangan = "Hadir tepat waktu"
            )
        )
        dao.insertAbsensi(
            AbsensiEntity(
                tanggal = "2026-10-04",
                namaKelas = "Kelas X IPS 1",
                nis = "2026002",
                namaSiswa = "Siti Nurhaliza",
                status = "Izin",
                keterangan = "Izin mengikuti olimpiade tingkat provinsi"
            )
        )
    }

    // Auth
    suspend fun login(username: String, password: String): UserEntity? {
        ensureDummyDataSeeded()
        return dao.login(username.trim(), password.trim())
    }

    // Users CRUD
    suspend fun saveUser(user: UserEntity) {
        if (user.id == 0) dao.insertUser(user) else dao.updateUser(user)
    }
    suspend fun deleteUser(id: Int) = dao.deleteUserById(id)

    // Kelas CRUD
    suspend fun saveKelas(kelas: KelasEntity) {
        if (kelas.id == 0) dao.insertKelas(kelas) else dao.updateKelas(kelas)
    }
    suspend fun deleteKelas(id: Int) = dao.deleteKelasById(id)

    // Siswa CRUD
    suspend fun saveSiswa(siswa: SiswaEntity) {
        if (siswa.id == 0) dao.insertSiswa(siswa) else dao.updateSiswa(siswa)
    }
    suspend fun deleteSiswa(id: Int) = dao.deleteSiswaById(id)

    // Kelompok CRUD
    suspend fun saveKelompok(kelompok: KelompokEntity) {
        if (kelompok.id == 0) dao.insertKelompok(kelompok) else dao.updateKelompok(kelompok)
    }
    suspend fun deleteKelompok(id: Int) = dao.deleteKelompokById(id)

    // Absensi CRUD
    suspend fun saveAbsensi(absensi: AbsensiEntity) {
        if (absensi.id == 0) dao.insertAbsensi(absensi) else dao.updateAbsensi(absensi)
    }
    suspend fun deleteAbsensi(id: Int) = dao.deleteAbsensiById(id)
}

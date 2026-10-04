package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AbsensiEntity
import com.example.data.AbsensiRepository
import com.example.data.KelasEntity
import com.example.data.KelompokEntity
import com.example.data.SiswaEntity
import com.example.data.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen(val title: String, val subtitle: String) {
    DASHBOARD("Dashboard & Grafik", "Statistik kehadiran & ringkasan data sekolah"),
    MASTER_USERS("1a. Form User Akses", "Master data akun pengguna (tanpa enkripsi)"),
    MASTER_KELAS("1b. Form Kelas", "Master data kelas & wali kelas"),
    DATA_SISWA("2. Form Data Siswa", "Manajemen data induk siswa"),
    DATA_KELOMPOK("3. Kelompok Siswa / Kelas", "Data pembagian kelompok belajar per kelas"),
    DATA_ABSENSI("4. Absensi Siswa / Kelas", "Input & kelola kehadiran siswa per kelas"),
    LAPORAN("5. Laporan Data", "Rekapitulasi laporan absensi & kelompok"),
    APPS_SCRIPT("Code.gs & Index.html", "Kode Google Apps Script & Spreadsheet Otomatis")
}

class AbsensiViewModel(private val repository: AbsensiRepository) : ViewModel() {

    val users: StateFlow<List<UserEntity>> = repository.allUsers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val kelasList: StateFlow<List<KelasEntity>> = repository.allKelas.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val siswaList: StateFlow<List<SiswaEntity>> = repository.allSiswa.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val kelompokList: StateFlow<List<KelompokEntity>> = repository.allKelompok.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val absensiList: StateFlow<List<AbsensiEntity>> = repository.allAbsensi.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _loggedInUser = MutableStateFlow<UserEntity?>(null)
    val loggedInUser: StateFlow<UserEntity?> = _loggedInUser.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureDummyDataSeeded()
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun clearLoginError() {
        _loginError.value = null
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginError.value = "Username dan password wajib diisi!"
            return
        }
        viewModelScope.launch {
            val user = repository.login(username, password)
            if (user != null) {
                _loginError.value = null
                _loggedInUser.value = user
                _currentScreen.value = AppScreen.DASHBOARD
                _statusMessage.value = "Selamat datang, ${user.namaLengkap} (${user.role})"
            } else {
                _loginError.value = "Username atau password salah! Gunakan akun dummy: admin / admin123"
            }
        }
    }

    fun logout() {
        _loggedInUser.value = null
        _currentScreen.value = AppScreen.DASHBOARD
    }

    fun resetToTwoDummyRows() {
        viewModelScope.launch {
            repository.resetAllToTwoDummyRows()
            _statusMessage.value = "Database di-reset ke 2 data dummy di setiap tabel!"
        }
    }

    // ==================== 1A. CRUD USER AKSES ====================
    fun saveUser(id: Int, username: String, password: String, namaLengkap: String, role: String) {
        if (username.isBlank() || password.isBlank() || namaLengkap.isBlank()) {
            _statusMessage.value = "Mohon lengkapi semua kolom User Akses!"
            return
        }
        viewModelScope.launch {
            repository.saveUser(
                UserEntity(
                    id = id,
                    username = username.trim(),
                    password = password.trim(),
                    namaLengkap = namaLengkap.trim(),
                    role = role.trim()
                )
            )
            _statusMessage.value = if (id == 0) "User akses berhasil ditambahkan!" else "User akses berhasil diperbarui!"
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            repository.deleteUser(id)
            _statusMessage.value = "Data user akses dihapus."
        }
    }

    // ==================== 1B. CRUD KELAS ====================
    fun saveKelas(id: Int, kodeKelas: String, namaKelas: String, waliKelas: String, tahunAjaran: String) {
        if (kodeKelas.isBlank() || namaKelas.isBlank() || waliKelas.isBlank()) {
            _statusMessage.value = "Mohon lengkapi Kode Kelas, Nama Kelas, dan Wali Kelas!"
            return
        }
        viewModelScope.launch {
            repository.saveKelas(
                KelasEntity(
                    id = id,
                    kodeKelas = kodeKelas.trim(),
                    namaKelas = namaKelas.trim(),
                    waliKelas = waliKelas.trim(),
                    tahunAjaran = tahunAjaran.ifBlank { "2026/2027" }.trim()
                )
            )
            _statusMessage.value = if (id == 0) "Data kelas berhasil ditambahkan!" else "Data kelas berhasil diperbarui!"
        }
    }

    fun deleteKelas(id: Int) {
        viewModelScope.launch {
            repository.deleteKelas(id)
            _statusMessage.value = "Data kelas dihapus."
        }
    }

    // ==================== 2. CRUD DATA SISWA ====================
    fun saveSiswa(id: Int, nis: String, namaSiswa: String, jenisKelamin: String, namaKelas: String, alamat: String) {
        if (nis.isBlank() || namaSiswa.isBlank() || namaKelas.isBlank()) {
            _statusMessage.value = "Mohon lengkapi NIS, Nama Siswa, dan Kelas!"
            return
        }
        viewModelScope.launch {
            repository.saveSiswa(
                SiswaEntity(
                    id = id,
                    nis = nis.trim(),
                    namaSiswa = namaSiswa.trim(),
                    jenisKelamin = jenisKelamin.ifBlank { "Laki-laki" },
                    namaKelas = namaKelas.trim(),
                    alamat = alamat.trim()
                )
            )
            _statusMessage.value = if (id == 0) "Data siswa berhasil ditambahkan!" else "Data siswa berhasil diperbarui!"
        }
    }

    fun deleteSiswa(id: Int) {
        viewModelScope.launch {
            repository.deleteSiswa(id)
            _statusMessage.value = "Data siswa dihapus."
        }
    }

    // ==================== 3. CRUD KELOMPOK SISWA PER KELAS ====================
    fun saveKelompok(id: Int, namaKelompok: String, namaKelas: String, namaSiswa: String, peranKelompok: String, topikTugas: String) {
        if (namaKelompok.isBlank() || namaKelas.isBlank() || namaSiswa.isBlank()) {
            _statusMessage.value = "Mohon lengkapi Nama Kelompok, Kelas, dan Siswa!"
            return
        }
        viewModelScope.launch {
            repository.saveKelompok(
                KelompokEntity(
                    id = id,
                    namaKelompok = namaKelompok.trim(),
                    namaKelas = namaKelas.trim(),
                    namaSiswa = namaSiswa.trim(),
                    peranKelompok = peranKelompok.ifBlank { "Anggota" },
                    topikTugas = topikTugas.trim()
                )
            )
            _statusMessage.value = if (id == 0) "Kelompok siswa berhasil ditambahkan!" else "Kelompok siswa berhasil diperbarui!"
        }
    }

    fun deleteKelompok(id: Int) {
        viewModelScope.launch {
            repository.deleteKelompok(id)
            _statusMessage.value = "Data kelompok siswa dihapus."
        }
    }

    // ==================== 4. CRUD ABSENSI SISWA PER KELAS ====================
    fun saveAbsensi(id: Int, tanggal: String, namaKelas: String, nis: String, namaSiswa: String, status: String, keterangan: String) {
        if (tanggal.isBlank() || namaKelas.isBlank() || namaSiswa.isBlank()) {
            _statusMessage.value = "Mohon lengkapi Tanggal, Kelas, dan Nama Siswa!"
            return
        }
        viewModelScope.launch {
            repository.saveAbsensi(
                AbsensiEntity(
                    id = id,
                    tanggal = tanggal.trim(),
                    namaKelas = namaKelas.trim(),
                    nis = nis.ifBlank { "-" }.trim(),
                    namaSiswa = namaSiswa.trim(),
                    status = status.ifBlank { "Hadir" },
                    keterangan = keterangan.trim()
                )
            )
            _statusMessage.value = if (id == 0) "Data absensi berhasil disimpan!" else "Data absensi berhasil diperbarui!"
        }
    }

    fun deleteAbsensi(id: Int) {
        viewModelScope.launch {
            repository.deleteAbsensi(id)
            _statusMessage.value = "Data absensi dihapus."
        }
    }

    class Factory(private val repository: AbsensiRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AbsensiViewModel(repository) as T
        }
    }
}

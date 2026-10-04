package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.KelasEntity
import com.example.data.SiswaEntity
import com.example.data.UserEntity

// ============================================================================
// 1.a FORM USER AKSES (CRUD - Username & Password Tanpa Enkripsi)
// ============================================================================
@Composable
fun UserAksesScreen(
    users: List<UserEntity>,
    onSaveUser: (Int, String, String, String, String) -> Unit,
    onDeleteUser: (Int) -> Unit,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackToDashboard)

    var editingId by rememberSaveable { mutableIntStateOf(0) }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var namaLengkap by rememberSaveable { mutableStateOf("") }
    var role by rememberSaveable { mutableStateOf("Admin") }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val roles = listOf("Admin", "Guru", "Wali Kelas", "Operator")
    val filteredUsers = users.filter {
        searchQuery.isBlank() ||
            it.username.contains(searchQuery, ignoreCase = true) ||
            it.namaLengkap.contains(searchQuery, ignoreCase = true) ||
            it.role.contains(searchQuery, ignoreCase = true)
    }

    fun resetForm() {
        editingId = 0
        username = ""
        password = ""
        namaLengkap = ""
        role = "Admin"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (editingId == 0) "Tambah User Akses Baru" else "Edit User Akses (#$editingId)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Password disimpan tanpa enkripsi sesuai spesifikasi sistem",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("user_username_field")
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password (Tanpa Enkripsi)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("user_password_field")
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = namaLengkap,
                        onValueChange = { namaLengkap = it },
                        label = { Text("Nama Lengkap") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("user_nama_field")
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Pilih Role Akses:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        roles.forEach { r ->
                            FilterChip(
                                selected = role == r,
                                onClick = { role = r },
                                label = { Text(r) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                onSaveUser(editingId, username, password, namaLengkap, role)
                                resetForm()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("save_user_btn")
                        ) {
                            Icon(
                                imageVector = if (editingId == 0) Icons.Default.Add else Icons.Default.Save,
                                contentDescription = "Simpan User"
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (editingId == 0) "Simpan User" else "Update User")
                        }
                        if (editingId != 0 || username.isNotBlank() || password.isNotBlank()) {
                            OutlinedButton(
                                onClick = { resetForm() },
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("cancel_user_btn")
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = "Batal")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Batal")
                            }
                        }
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cari User Akses (${filteredUsers.size} data)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cari User") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_user_field")
            )
        }

        items(filteredUsers, key = { it.id }) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("user_item_card_${item.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = item.role,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.namaLengkap,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Username: ${item.username}  |  Password: ${item.password}",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = {
                            editingId = item.id
                            username = item.username
                            password = item.password
                            namaLengkap = item.namaLengkap
                            role = item.role
                        },
                        modifier = Modifier.testTag("edit_user_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit ${item.username}",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { onDeleteUser(item.id) },
                        modifier = Modifier.testTag("delete_user_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus ${item.username}",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

// ============================================================================
// 1.b FORM KELAS (CRUD)
// ============================================================================
@Composable
fun KelasScreen(
    kelasList: List<KelasEntity>,
    onSaveKelas: (Int, String, String, String, String) -> Unit,
    onDeleteKelas: (Int) -> Unit,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackToDashboard)

    var editingId by rememberSaveable { mutableIntStateOf(0) }
    var kodeKelas by rememberSaveable { mutableStateOf("") }
    var namaKelas by rememberSaveable { mutableStateOf("") }
    var waliKelas by rememberSaveable { mutableStateOf("") }
    var tahunAjaran by rememberSaveable { mutableStateOf("2026/2027") }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val filteredKelas = kelasList.filter {
        searchQuery.isBlank() ||
            it.kodeKelas.contains(searchQuery, ignoreCase = true) ||
            it.namaKelas.contains(searchQuery, ignoreCase = true) ||
            it.waliKelas.contains(searchQuery, ignoreCase = true)
    }

    fun resetForm() {
        editingId = 0
        kodeKelas = ""
        namaKelas = ""
        waliKelas = ""
        tahunAjaran = "2026/2027"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (editingId == 0) "Tambah Data Kelas Baru" else "Edit Data Kelas (#$editingId)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = kodeKelas,
                        onValueChange = { kodeKelas = it },
                        label = { Text("Kode Kelas (Cth: X-IPA-1)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("kelas_kode_field")
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = namaKelas,
                        onValueChange = { namaKelas = it },
                        label = { Text("Nama Kelas (Cth: Kelas X IPA 1)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("kelas_nama_field")
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = waliKelas,
                        onValueChange = { waliKelas = it },
                        label = { Text("Nama Wali Kelas") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("kelas_wali_field")
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = tahunAjaran,
                        onValueChange = { tahunAjaran = it },
                        label = { Text("Tahun Ajaran") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("kelas_tahun_field")
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                onSaveKelas(editingId, kodeKelas, namaKelas, waliKelas, tahunAjaran)
                                resetForm()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("save_kelas_btn")
                        ) {
                            Icon(
                                imageVector = if (editingId == 0) Icons.Default.Add else Icons.Default.Save,
                                contentDescription = "Simpan Kelas"
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (editingId == 0) "Simpan Kelas" else "Update Kelas")
                        }
                        if (editingId != 0 || kodeKelas.isNotBlank() || namaKelas.isNotBlank()) {
                            OutlinedButton(
                                onClick = { resetForm() },
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("cancel_kelas_btn")
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = "Batal")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Batal")
                            }
                        }
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cari Kelas (${filteredKelas.size} data)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cari Kelas") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_kelas_field")
            )
        }

        items(filteredKelas, key = { it.id }) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("kelas_item_card_${item.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = item.kodeKelas,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.namaKelas,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Wali Kelas: ${item.waliKelas} • TA: ${item.tahunAjaran}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = {
                            editingId = item.id
                            kodeKelas = item.kodeKelas
                            namaKelas = item.namaKelas
                            waliKelas = item.waliKelas
                            tahunAjaran = item.tahunAjaran
                        },
                        modifier = Modifier.testTag("edit_kelas_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit ${item.namaKelas}",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { onDeleteKelas(item.id) },
                        modifier = Modifier.testTag("delete_kelas_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus ${item.namaKelas}",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

// ============================================================================
// 2. FORM DATA SISWA (CRUD)
// ============================================================================
@Composable
fun SiswaScreen(
    siswaList: List<SiswaEntity>,
    kelasList: List<KelasEntity>,
    onSaveSiswa: (Int, String, String, String, String, String) -> Unit,
    onDeleteSiswa: (Int) -> Unit,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackToDashboard)

    var editingId by rememberSaveable { mutableIntStateOf(0) }
    var nis by rememberSaveable { mutableStateOf("") }
    var namaSiswa by rememberSaveable { mutableStateOf("") }
    var jenisKelamin by rememberSaveable { mutableStateOf("Laki-laki") }
    var selectedKelas by rememberSaveable {
        mutableStateOf(kelasList.firstOrNull()?.namaKelas ?: "Kelas X IPA 1")
    }
    var alamat by rememberSaveable { mutableStateOf("") }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val availableKelas = if (kelasList.isNotEmpty()) {
        kelasList.map { it.namaKelas }
    } else {
        listOf("Kelas X IPA 1", "Kelas X IPS 1")
    }

    val filteredSiswa = siswaList.filter {
        searchQuery.isBlank() ||
            it.nis.contains(searchQuery, ignoreCase = true) ||
            it.namaSiswa.contains(searchQuery, ignoreCase = true) ||
            it.namaKelas.contains(searchQuery, ignoreCase = true)
    }

    fun resetForm() {
        editingId = 0
        nis = ""
        namaSiswa = ""
        jenisKelamin = "Laki-laki"
        selectedKelas = availableKelas.first()
        alamat = ""
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (editingId == 0) "Tambah Data Siswa Baru" else "Edit Data Siswa (#$editingId)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = nis,
                        onValueChange = { nis = it },
                        label = { Text("Nomor Induk Siswa (NIS)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("siswa_nis_field")
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = namaSiswa,
                        onValueChange = { namaSiswa = it },
                        label = { Text("Nama Lengkap Siswa") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("siswa_nama_field")
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Jenis Kelamin:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Laki-laki", "Perempuan").forEach { jk ->
                            FilterChip(
                                selected = jenisKelamin == jk,
                                onClick = { jenisKelamin = jk },
                                label = { Text(jk) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pilih Kelas Siswa:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableKelas.forEach { kls ->
                            FilterChip(
                                selected = selectedKelas == kls,
                                onClick = { selectedKelas = kls },
                                label = { Text(kls) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = alamat,
                        onValueChange = { alamat = it },
                        label = { Text("Alamat Lengkap") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("siswa_alamat_field")
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                onSaveSiswa(editingId, nis, namaSiswa, jenisKelamin, selectedKelas, alamat)
                                resetForm()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("save_siswa_btn")
                        ) {
                            Icon(
                                imageVector = if (editingId == 0) Icons.Default.Add else Icons.Default.Save,
                                contentDescription = "Simpan Siswa"
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (editingId == 0) "Simpan Siswa" else "Update Siswa")
                        }
                        if (editingId != 0 || nis.isNotBlank() || namaSiswa.isNotBlank()) {
                            OutlinedButton(
                                onClick = { resetForm() },
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("cancel_siswa_btn")
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = "Batal")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Batal")
                            }
                        }
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cari Siswa (${filteredSiswa.size} data)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cari Siswa") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_siswa_field")
            )
        }

        items(filteredSiswa, key = { it.id }) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("siswa_item_card_${item.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "NIS: ${item.nis}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.namaSiswa,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${item.namaKelas} • ${item.jenisKelamin}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (item.alamat.isNotBlank()) {
                            Text(
                                text = item.alamat,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            editingId = item.id
                            nis = item.nis
                            namaSiswa = item.namaSiswa
                            jenisKelamin = item.jenisKelamin
                            selectedKelas = item.namaKelas
                            alamat = item.alamat
                        },
                        modifier = Modifier.testTag("edit_siswa_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit ${item.namaSiswa}",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { onDeleteSiswa(item.id) },
                        modifier = Modifier.testTag("delete_siswa_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus ${item.namaSiswa}",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

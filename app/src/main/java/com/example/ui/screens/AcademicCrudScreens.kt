package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.AbsensiEntity
import com.example.data.KelasEntity
import com.example.data.KelompokEntity
import com.example.data.SiswaEntity
import com.example.ui.theme.StatusAlfa
import com.example.ui.theme.StatusHadir
import com.example.ui.theme.StatusIzin
import com.example.ui.theme.StatusSakit

// ============================================================================
// 3. FORM DATA KELOMPOK SISWA PER KELAS (CRUD)
// ============================================================================
@Composable
fun KelompokScreen(
    kelompokList: List<KelompokEntity>,
    kelasList: List<KelasEntity>,
    siswaList: List<SiswaEntity>,
    onSaveKelompok: (Int, String, String, String, String, String) -> Unit,
    onDeleteKelompok: (Int) -> Unit,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackToDashboard)

    val availableKelas = if (kelasList.isNotEmpty()) {
        kelasList.map { it.namaKelas }
    } else {
        listOf("Kelas X IPA 1", "Kelas X IPS 1")
    }

    var editingId by rememberSaveable { mutableIntStateOf(0) }
    var namaKelompok by rememberSaveable { mutableStateOf("") }
    var selectedKelas by rememberSaveable { mutableStateOf(availableKelas.first()) }
    var selectedSiswa by rememberSaveable {
        mutableStateOf(siswaList.firstOrNull()?.namaSiswa ?: "Ahmad Fauzi")
    }
    var peranKelompok by rememberSaveable { mutableStateOf("Ketua Kelompok") }
    var topikTugas by rememberSaveable { mutableStateOf("") }
    var filterKelas by rememberSaveable { mutableStateOf("SEMUA") }

    val siswaInSelectedKelas = siswaList.filter { it.namaKelas == selectedKelas }
    val siswaOptions = if (siswaInSelectedKelas.isNotEmpty()) {
        siswaInSelectedKelas.map { it.namaSiswa }
    } else if (siswaList.isNotEmpty()) {
        siswaList.map { it.namaSiswa }
    } else {
        listOf("Ahmad Fauzi", "Siti Nurhaliza")
    }

    val filteredKelompok = kelompokList.filter {
        filterKelas == "SEMUA" || it.namaKelas == filterKelas
    }

    fun resetForm() {
        editingId = 0
        namaKelompok = ""
        peranKelompok = "Ketua Kelompok"
        topikTugas = ""
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
                        text = if (editingId == 0) "Tambah Kelompok Siswa per Kelas" else "Edit Kelompok Siswa (#$editingId)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = namaKelompok,
                        onValueChange = { namaKelompok = it },
                        label = { Text("Nama Kelompok (Cth: Kelompok 1 - Sains)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("kelompok_nama_field")
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Pilih Kelas:",
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
                                onClick = {
                                    selectedKelas = kls
                                    val firstSiswaInClass = siswaList.firstOrNull { it.namaKelas == kls }?.namaSiswa
                                    if (firstSiswaInClass != null) selectedSiswa = firstSiswaInClass
                                },
                                label = { Text(kls) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pilih Siswa di Kelas Ini:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        siswaOptions.forEach { sName ->
                            FilterChip(
                                selected = selectedSiswa == sName,
                                onClick = { selectedSiswa = sName },
                                label = { Text(sName) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Peran dalam Kelompok:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Ketua Kelompok", "Sekretaris", "Anggota").forEach { peran ->
                            FilterChip(
                                selected = peranKelompok == peran,
                                onClick = { peranKelompok = peran },
                                label = { Text(peran) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = topikTugas,
                        onValueChange = { topikTugas = it },
                        label = { Text("Topik / Proyek Tugas Kelompok") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("kelompok_topik_field")
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                onSaveKelompok(
                                    editingId,
                                    namaKelompok,
                                    selectedKelas,
                                    selectedSiswa,
                                    peranKelompok,
                                    topikTugas
                                )
                                resetForm()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("save_kelompok_btn")
                        ) {
                            Icon(
                                imageVector = if (editingId == 0) Icons.Default.Add else Icons.Default.Save,
                                contentDescription = "Simpan Kelompok"
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (editingId == 0) "Simpan Kelompok" else "Update Kelompok")
                        }
                        if (editingId != 0 || namaKelompok.isNotBlank()) {
                            OutlinedButton(
                                onClick = { resetForm() },
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("cancel_kelompok_btn")
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

        // Filter per Kelas
        item {
            Column {
                Text(
                    text = "Filter Kelompok Berdasarkan Kelas (${filteredKelompok.size} data):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = filterKelas == "SEMUA",
                        onClick = { filterKelas = "SEMUA" },
                        label = { Text("Semua Kelas") }
                    )
                    availableKelas.forEach { kls ->
                        FilterChip(
                            selected = filterKelas == kls,
                            onClick = { filterKelas = kls },
                            label = { Text(kls) }
                        )
                    }
                }
            }
        }

        items(filteredKelompok, key = { it.id }) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("kelompok_item_card_${item.id}"),
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
                                    text = item.namaKelas,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.namaKelompok,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Siswa: ${item.namaSiswa} (${item.peranKelompok})",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (item.topikTugas.isNotBlank()) {
                            Text(
                                text = "Topik: ${item.topikTugas}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            editingId = item.id
                            namaKelompok = item.namaKelompok
                            selectedKelas = item.namaKelas
                            selectedSiswa = item.namaSiswa
                            peranKelompok = item.peranKelompok
                            topikTugas = item.topikTugas
                        },
                        modifier = Modifier.testTag("edit_kelompok_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit ${item.namaKelompok}",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { onDeleteKelompok(item.id) },
                        modifier = Modifier.testTag("delete_kelompok_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus ${item.namaKelompok}",
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
// 4. FORM DATA ABSENSI SISWA PER KELAS (CRUD)
// ============================================================================
@Composable
fun AbsensiScreen(
    absensiList: List<AbsensiEntity>,
    kelasList: List<KelasEntity>,
    siswaList: List<SiswaEntity>,
    onSaveAbsensi: (Int, String, String, String, String, String, String) -> Unit,
    onDeleteAbsensi: (Int) -> Unit,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackToDashboard)

    val availableKelas = if (kelasList.isNotEmpty()) {
        kelasList.map { it.namaKelas }
    } else {
        listOf("Kelas X IPA 1", "Kelas X IPS 1")
    }

    var editingId by rememberSaveable { mutableIntStateOf(0) }
    var tanggal by rememberSaveable { mutableStateOf("2026-10-04") }
    var selectedKelas by rememberSaveable { mutableStateOf(availableKelas.first()) }
    var selectedSiswa by rememberSaveable {
        mutableStateOf(siswaList.firstOrNull()?.namaSiswa ?: "Ahmad Fauzi")
    }
    var status by rememberSaveable { mutableStateOf("Hadir") }
    var keterangan by rememberSaveable { mutableStateOf("") }
    var filterKelas by rememberSaveable { mutableStateOf("SEMUA") }

    val siswaInSelectedKelas = siswaList.filter { it.namaKelas == selectedKelas }
    val activeSiswaOptions = if (siswaInSelectedKelas.isNotEmpty()) {
        siswaInSelectedKelas
    } else {
        siswaList
    }

    val filteredAbsensi = absensiList.filter {
        filterKelas == "SEMUA" || it.namaKelas == filterKelas
    }

    fun resetForm() {
        editingId = 0
        status = "Hadir"
        keterangan = ""
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
                        text = if (editingId == 0) "Input Absensi Siswa per Kelas" else "Edit Absensi Siswa (#$editingId)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = tanggal,
                        onValueChange = { tanggal = it },
                        label = { Text("Tanggal Absensi (YYYY-MM-DD)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("absensi_tanggal_field")
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Pilih Kelas:",
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
                                onClick = {
                                    selectedKelas = kls
                                    val firstInClass = siswaList.firstOrNull { it.namaKelas == kls }
                                    if (firstInClass != null) selectedSiswa = firstInClass.namaSiswa
                                },
                                label = { Text(kls) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pilih Siswa:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        activeSiswaOptions.forEach { sObj ->
                            FilterChip(
                                selected = selectedSiswa == sObj.namaSiswa,
                                onClick = { selectedSiswa = sObj.namaSiswa },
                                label = { Text("${sObj.nis} - ${sObj.namaSiswa}") }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Status Kehadiran:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Hadir", "Izin", "Sakit", "Alfa").forEach { st ->
                            FilterChip(
                                selected = status == st,
                                onClick = { status = st },
                                label = { Text(st) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = keterangan,
                        onValueChange = { keterangan = it },
                        label = { Text("Keterangan / Catatan Kehadiran") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("absensi_keterangan_field")
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val matchedSiswa = siswaList.firstOrNull { it.namaSiswa == selectedSiswa }
                                val nis = matchedSiswa?.nis ?: "2026000"
                                onSaveAbsensi(
                                    editingId,
                                    tanggal,
                                    selectedKelas,
                                    nis,
                                    selectedSiswa,
                                    status,
                                    keterangan.ifBlank { "Dicatat melalui Form Absensi" }
                                )
                                resetForm()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("save_absensi_btn")
                        ) {
                            Icon(
                                imageVector = if (editingId == 0) Icons.Default.Add else Icons.Default.Save,
                                contentDescription = "Simpan Absensi"
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (editingId == 0) "Simpan Absensi" else "Update Absensi")
                        }
                        if (editingId != 0 || keterangan.isNotBlank()) {
                            OutlinedButton(
                                onClick = { resetForm() },
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("cancel_absensi_btn")
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

        // Filter Absensi per Kelas
        item {
            Column {
                Text(
                    text = "Filter Absensi per Kelas (${filteredAbsensi.size} data):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = filterKelas == "SEMUA",
                        onClick = { filterKelas = "SEMUA" },
                        label = { Text("Semua Kelas") }
                    )
                    availableKelas.forEach { kls ->
                        FilterChip(
                            selected = filterKelas == kls,
                            onClick = { filterKelas = kls },
                            label = { Text(kls) }
                        )
                    }
                }
            }
        }

        items(filteredAbsensi, key = { it.id }) { item ->
            val statusColor = when (item.status) {
                "Hadir" -> StatusHadir
                "Izin" -> StatusIzin
                "Sakit" -> StatusSakit
                else -> StatusAlfa
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("absensi_item_card_${item.id}"),
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
                                color = statusColor.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = item.status.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = statusColor,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${item.namaSiswa} (${item.nis})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${item.tanggal} • ${item.namaKelas}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (item.keterangan.isNotBlank()) {
                            Text(
                                text = item.keterangan,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            editingId = item.id
                            tanggal = item.tanggal
                            selectedKelas = item.namaKelas
                            selectedSiswa = item.namaSiswa
                            status = item.status
                            keterangan = item.keterangan
                        },
                        modifier = Modifier.testTag("edit_absensi_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Absensi ${item.namaSiswa}",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { onDeleteAbsensi(item.id) },
                        modifier = Modifier.testTag("delete_absensi_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus Absensi ${item.namaSiswa}",
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
// 5. LAPORAN DATA (REKAPITULASI FILTER & EKSPOR)
// ============================================================================
@Composable
fun LaporanScreen(
    absensiList: List<AbsensiEntity>,
    kelasList: List<KelasEntity>,
    kelompokList: List<KelompokEntity>,
    onShowMessage: (String) -> Unit,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackToDashboard)

    val context = LocalContext.current
    var filterKelas by rememberSaveable { mutableStateOf("SEMUA") }
    var filterStatus by rememberSaveable { mutableStateOf("SEMUA") }

    val availableKelas = kelasList.map { it.namaKelas }
    val filteredLaporan = absensiList.filter { item ->
        val matchKelas = filterKelas == "SEMUA" || item.namaKelas == filterKelas
        val matchStatus = filterStatus == "SEMUA" || item.status.equals(filterStatus, ignoreCase = true)
        matchKelas && matchStatus
    }

    val hadir = filteredLaporan.count { it.status == "Hadir" }
    val izin = filteredLaporan.count { it.status == "Izin" }
    val sakit = filteredLaporan.count { it.status == "Sakit" }
    val alfa = filteredLaporan.count { it.status == "Alfa" }

    fun buildCsvReport(): String {
        val sb = StringBuilder()
        sb.appendLine("ID,Tanggal,Kelas,NIS,Nama Siswa,Kelompok Belajar,Status,Keterangan")
        filteredLaporan.forEach { a ->
            val klp = kelompokList.firstOrNull { it.namaSiswa == a.namaSiswa }?.namaKelompok ?: "-"
            sb.appendLine("${a.id},${a.tanggal},${a.namaKelas},${a.nis},${a.namaSiswa},$klp,${a.status},${a.keterangan}")
        }
        return sb.toString()
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
                        text = "Filter & Ekspor Laporan Absensi Siswa",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Filter Kelas:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = filterKelas == "SEMUA",
                            onClick = { filterKelas = "SEMUA" },
                            label = { Text("Semua Kelas") }
                        )
                        availableKelas.forEach { kls ->
                            FilterChip(
                                selected = filterKelas == kls,
                                onClick = { filterKelas = kls },
                                label = { Text(kls) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Filter Status Kehadiran:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("SEMUA", "Hadir", "Izin", "Sakit", "Alfa").forEach { st ->
                            FilterChip(
                                selected = filterStatus == st,
                                onClick = { filterStatus = st },
                                label = { Text(if (st == "SEMUA") "Semua Status" else st) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilledTonalButton(
                            onClick = {
                                val csv = buildCsvReport()
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Laporan_Absensi.csv", csv))
                                onShowMessage("Laporan CSV berhasil disalin ke Clipboard!")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("copy_laporan_csv_btn")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Salin CSV")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Salin CSV")
                        }

                        Button(
                            onClick = {
                                val csv = buildCsvReport()
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "Laporan Absensi Siswa")
                                    putExtra(Intent.EXTRA_TEXT, csv)
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Bagikan Laporan"))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("share_laporan_btn")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Bagikan Laporan")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Bagikan")
                        }
                    }
                }
            }
        }

        // Ringkasan Rekapitulasi
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MiniStatBadge("Hadir", hadir, StatusHadir, Modifier.weight(1f))
                MiniStatBadge("Izin", izin, StatusIzin, Modifier.weight(1f))
                MiniStatBadge("Sakit", sakit, StatusSakit, Modifier.weight(1f))
                MiniStatBadge("Alfa", alfa, StatusAlfa, Modifier.weight(1f))
            }
        }

        items(filteredLaporan, key = { it.id }) { item ->
            val kelompokInfo = kelompokList.firstOrNull { it.namaSiswa == item.namaSiswa }
            val statusColor = when (item.status) {
                "Hadir" -> StatusHadir
                "Izin" -> StatusIzin
                "Sakit" -> StatusSakit
                else -> StatusAlfa
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("laporan_row_${item.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${item.namaSiswa} (${item.nis})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            color = statusColor.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = item.status,
                                style = MaterialTheme.typography.labelSmall,
                                color = statusColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tanggal: ${item.tanggal}  •  Kelas: ${item.namaKelas}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Kelompok: ${kelompokInfo?.namaKelompok ?: "Belum masuk kelompok"} (${kelompokInfo?.peranKelompok ?: "-"})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (item.keterangan.isNotBlank()) {
                        Text(
                            text = "Catatan: ${item.keterangan}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun MiniStatBadge(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.14f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

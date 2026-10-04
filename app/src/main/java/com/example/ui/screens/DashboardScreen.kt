package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.AbsensiEntity
import com.example.data.KelasEntity
import com.example.data.KelompokEntity
import com.example.data.SiswaEntity
import com.example.data.UserEntity
import com.example.ui.theme.StatusAlfa
import com.example.ui.theme.StatusHadir
import com.example.ui.theme.StatusIzin
import com.example.ui.theme.StatusSakit
import com.example.viewmodel.AppScreen

@Composable
fun DashboardScreen(
    currentUser: UserEntity?,
    users: List<UserEntity>,
    kelasList: List<KelasEntity>,
    siswaList: List<SiswaEntity>,
    kelompokList: List<KelompokEntity>,
    absensiList: List<AbsensiEntity>,
    onNavigate: (AppScreen) -> Unit,
    onResetDummy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hadirCount = absensiList.count { it.status.equals("Hadir", ignoreCase = true) }
    val izinCount = absensiList.count { it.status.equals("Izin", ignoreCase = true) }
    val sakitCount = absensiList.count { it.status.equals("Sakit", ignoreCase = true) }
    val alfaCount = absensiList.count { it.status.equals("Alfa", ignoreCase = true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Banner Selamat Datang & Reset 2 Data Dummy
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Selamat Datang, ${currentUser?.namaLengkap ?: "Administrator"}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "Role: ${currentUser?.role ?: "Admin"} • Username: ${currentUser?.username ?: "admin"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFD8E9FF)
                            )
                        }
                        Surface(
                            color = Color.White.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "DB Aktif",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilledTonalButton(
                            onClick = { onNavigate(AppScreen.APPS_SCRIPT) },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("open_gas_code_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = "Lihat Code.gs dan Index.html",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Code.gs & Index.html", style = MaterialTheme.typography.labelMedium)
                        }

                        FilledTonalButton(
                            onClick = onResetDummy,
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("dashboard_reset_dummy_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset 2 Data Dummy",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("2 Dummy", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        // Statistik Utama (KPI Grid)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatSummaryCard(
                        title = "Total Siswa",
                        value = siswaList.size.toString(),
                        subtitle = "${kelasList.size} Kelas Aktif",
                        accentColor = Color(0xFF0F4C81),
                        modifier = Modifier.weight(1f)
                    )
                    StatSummaryCard(
                        title = "Total Kelas",
                        value = kelasList.size.toString(),
                        subtitle = "${kelompokList.size} Kelompok",
                        accentColor = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatSummaryCard(
                        title = "Data Absensi",
                        value = absensiList.size.toString(),
                        subtitle = "$hadirCount Hadir • $izinCount Izin",
                        accentColor = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f)
                    )
                    StatSummaryCard(
                        title = "User Akses",
                        value = users.size.toString(),
                        subtitle = "Login Tanpa Enkripsi",
                        accentColor = Color(0xFF8B5CF6),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Grafik Donut Kehadiran Siswa
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Grafik Distribusi Status Kehadiran",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Visualisasi real-time dari tabel Absensi Siswa",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        AttendanceDonutChart(
                            hadir = hadirCount,
                            izin = izinCount,
                            sakit = sakitCount,
                            alfa = alfaCount,
                            modifier = Modifier.size(130.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ChartLegendRow("Hadir", hadirCount, absensiList.size, StatusHadir)
                            ChartLegendRow("Izin", izinCount, absensiList.size, StatusIzin)
                            ChartLegendRow("Sakit", sakitCount, absensiList.size, StatusSakit)
                            ChartLegendRow("Alfa", alfaCount, absensiList.size, StatusAlfa)
                        }
                    }
                }
            }
        }

        // Grafik Batang Siswa & Absensi per Kelas
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Grafik Rekapitulasi Siswa & Kelompok per Kelas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val maxSiswaInClass = (kelasList.maxOfOrNull { k ->
                        siswaList.count { it.namaKelas == k.namaKelas }
                    } ?: 1).coerceAtLeast(1)

                    kelasList.forEach { kls ->
                        val jmlSiswa = siswaList.count { it.namaKelas == kls.namaKelas }
                        val jmlKelompok = kelompokList.count { it.namaKelas == kls.namaKelas }
                        val jmlHadir = absensiList.count {
                            it.namaKelas == kls.namaKelas && it.status.equals("Hadir", ignoreCase = true)
                        }
                        val progress = (jmlSiswa.toFloat() / maxSiswaInClass.toFloat()).coerceIn(0.15f, 1f)

                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${kls.namaKelas} (${kls.kodeKelas})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "$jmlSiswa Siswa • $jmlKelompok Klp • $jmlHadir Hadir",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(50)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Daftar Modul Sesuai Spesifikasi
        item {
            Text(
                text = "Modul & Menu Aplikasi (Full CRUD)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        item {
            MenuNavCard(
                badge = "1.a • MASTER DATA",
                title = "Form User Akses",
                subtitle = "${users.size} data user • Username & Password tanpa enkripsi (CRUD)",
                icon = Icons.Default.ManageAccounts,
                testTag = "nav_card_users",
                onClick = { onNavigate(AppScreen.MASTER_USERS) }
            )
        }

        item {
            MenuNavCard(
                badge = "1.b • MASTER DATA",
                title = "Form Kelas",
                subtitle = "${kelasList.size} data kelas • Kode kelas, nama kelas & wali kelas (CRUD)",
                icon = Icons.Default.Class,
                testTag = "nav_card_kelas",
                onClick = { onNavigate(AppScreen.MASTER_KELAS) }
            )
        }

        item {
            MenuNavCard(
                badge = "MENU 2",
                title = "Form Data Siswa",
                subtitle = "${siswaList.size} data siswa • NIS, nama, jenis kelamin, kelas & alamat (CRUD)",
                icon = Icons.Default.People,
                testTag = "nav_card_siswa",
                onClick = { onNavigate(AppScreen.DATA_SISWA) }
            )
        }

        item {
            MenuNavCard(
                badge = "MENU 3",
                title = "Form Data Kelompok Siswa per Kelas",
                subtitle = "${kelompokList.size} data kelompok • Pembagian kelompok & topik tugas per kelas (CRUD)",
                icon = Icons.Default.Groups,
                testTag = "nav_card_kelompok",
                onClick = { onNavigate(AppScreen.DATA_KELOMPOK) }
            )
        }

        item {
            MenuNavCard(
                badge = "MENU 4",
                title = "Form Data Absensi Siswa per Kelas",
                subtitle = "${absensiList.size} data absensi • Input Hadir, Izin, Sakit, Alfa per kelas (CRUD)",
                icon = Icons.AutoMirrored.Filled.FactCheck,
                testTag = "nav_card_absensi",
                onClick = { onNavigate(AppScreen.DATA_ABSENSI) }
            )
        }

        item {
            MenuNavCard(
                badge = "MENU 5",
                title = "Laporan Data",
                subtitle = "Rekapitulasi kehadiran & kelompok per kelas + ekspor laporan",
                icon = Icons.Default.Assessment,
                testTag = "nav_card_laporan",
                onClick = { onNavigate(AppScreen.LAPORAN) }
            )
        }

        item {
            MenuNavCard(
                badge = "GOOGLE APPS SCRIPT & SPREADSHEET",
                title = "Kode Code.gs & Index.html + Live Web Preview",
                subtitle = "Salin kode Apps Script siap pakai (otomatis generate sheet & 2 dummy data)",
                icon = Icons.Default.Code,
                testTag = "nav_card_gas",
                onClick = { onNavigate(AppScreen.APPS_SCRIPT) }
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun StatSummaryCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AttendanceDonutChart(
    hadir: Int,
    izin: Int,
    sakit: Int,
    alfa: Int,
    modifier: Modifier = Modifier
) {
    val total = (hadir + izin + sakit + alfa).coerceAtLeast(1)
    val slices = listOf(
        Pair(hadir.toFloat() / total, StatusHadir),
        Pair(izin.toFloat() / total, StatusIzin),
        Pair(sakit.toFloat() / total, StatusSakit),
        Pair(alfa.toFloat() / total, StatusAlfa)
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 24.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
            var startAngle = -90f

            if (hadir + izin + sakit + alfa == 0) {
                drawArc(
                    color = Color.LightGray,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(diameter, diameter),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )
            } else {
                slices.forEach { (fraction, color) ->
                    val sweep = fraction * 360f
                    if (sweep > 0f) {
                        drawArc(
                            color = color,
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(diameter, diameter),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )
                        startAngle += sweep
                    }
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${hadir + izin + sakit + alfa}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Absensi",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ChartLegendRow(
    label: String,
    count: Int,
    total: Int,
    color: Color
) {
    val pct = if (total > 0) (count * 100) / total else 0
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
        }
        Text(
            text = "$count ($pct%)",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MenuNavCard(
    badge: String,
    title: String,
    subtitle: String,
    icon: ImageVector,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Buka $title",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

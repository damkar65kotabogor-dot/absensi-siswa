package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Html
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AppsScriptStudioScreen(
    onShowMessage: (String) -> Unit,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackToDashboard)

    val context = LocalContext.current
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val codeGsText = remember {
        runCatching {
            context.assets.open("Code.gs").bufferedReader().use { it.readText() }
        }.getOrDefault("// Code.gs tidak ditemukan")
    }

    val indexHtmlText = remember {
        runCatching {
            context.assets.open("Index.html").bufferedReader().use { it.readText() }
        }.getOrDefault("<!-- Index.html tidak ditemukan -->")
    }

    fun copyToClipboard(label: String, content: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, content))
        onShowMessage("Kode $label berhasil disalin ke Clipboard!")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Tab Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                leadingIcon = { Icon(Icons.Default.Code, contentDescription = "Code.gs") },
                label = { Text("1. File Code.gs") },
                modifier = Modifier.testTag("tab_code_gs")
            )
            FilterChip(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                leadingIcon = { Icon(Icons.Default.Html, contentDescription = "Index.html") },
                label = { Text("2. File Index.html") },
                modifier = Modifier.testTag("tab_index_html")
            )
            FilterChip(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = "Live Preview") },
                label = { Text("3. Live Preview Index.html") },
                modifier = Modifier.testTag("tab_live_preview")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (selectedTab) {
            0 -> {
                CodeViewerPanel(
                    fileName = "Code.gs (Backend Google Apps Script & Auto-Database Spreadsheet)",
                    description = "Otomatis membuat 5 sheet (Users, Kelas, Siswa, Kelompok, Absensi) beserta 2 data dummy di setiap tabel pada file Google Spreadsheet yang sama.",
                    codeContent = codeGsText,
                    onCopy = { copyToClipboard("Code.gs", codeGsText) }
                )
            }
            1 -> {
                CodeViewerPanel(
                    fileName = "Index.html (Frontend Modern HTML + CSS + JavaScript)",
                    description = "Tampilan Web App modern lengkap dengan Login tanpa enkripsi, Dashboard Grafik, dan Form CRUD 5 tabel.",
                    codeContent = indexHtmlText,
                    onCopy = { copyToClipboard("Index.html", indexHtmlText) }
                )
            }
            2 -> {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                webViewClient = WebViewClient()
                                loadUrl("file:///android_asset/Index.html")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CodeViewerPanel(
    fileName: String,
    description: String,
    codeContent: String,
    onCopy: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = fileName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Langkah Penggunaan di Google Spreadsheet:\n" +
                        "1. Buka file Google Spreadsheet baru -> menu Ekstensi > Apps Script.\n" +
                        "2. Tempel isi Code.gs ke file Code.gs di editor Apps Script.\n" +
                        "3. Buat file HTML baru bernama Index.html (tanpa ekstensi ganda) lalu tempel isi Index.html.\n" +
                        "4. Klik Terapkan (Deploy) > Deployment Baru > Web App (Akses: Siapa saja).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onCopy,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .testTag("copy_code_button")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Salin Kode")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Salin Seluruh Kode ke Clipboard")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = codeContent,
                    color = Color(0xFFE2E8F0),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

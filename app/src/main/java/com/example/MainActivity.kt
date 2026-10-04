package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AbsensiDatabase
import com.example.data.AbsensiRepository
import com.example.ui.screens.AbsensiScreen
import com.example.ui.screens.AppsScriptStudioScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.KelasScreen
import com.example.ui.screens.KelompokScreen
import com.example.ui.screens.LaporanScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.SiswaScreen
import com.example.ui.screens.UserAksesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AbsensiViewModel
import com.example.viewmodel.AppScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val repository = remember {
                    val db = AbsensiDatabase.getDatabase(context)
                    AbsensiRepository(db.absensiDao())
                }
                val viewModel: AbsensiViewModel = viewModel(
                    factory = AbsensiViewModel.Factory(repository)
                )
                AbsensiSiswaApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbsensiSiswaApp(viewModel: AbsensiViewModel) {
    val users by viewModel.users.collectAsStateWithLifecycle()
    val kelasList by viewModel.kelasList.collectAsStateWithLifecycle()
    val siswaList by viewModel.siswaList.collectAsStateWithLifecycle()
    val kelompokList by viewModel.kelompokList.collectAsStateWithLifecycle()
    val absensiList by viewModel.absensiList.collectAsStateWithLifecycle()
    val loggedInUser by viewModel.loggedInUser.collectAsStateWithLifecycle()
    val loginError by viewModel.loginError.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(statusMessage) {
        statusMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusMessage()
        }
    }

    if (loggedInUser == null) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            LoginScreen(
                users = users,
                loginError = loginError,
                onLogin = { u, p -> viewModel.login(u, p) },
                onClearError = { viewModel.clearLoginError() },
                onResetDummy = { viewModel.resetToTwoDummyRows() },
                modifier = Modifier.padding(innerPadding)
            )
        }
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 18.dp)
                    ) {
                        Text(
                            text = "Absensi Siswa",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${loggedInUser?.namaLengkap} (${loggedInUser?.role})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    val navItems = listOf(
                        Pair(AppScreen.DASHBOARD, Icons.Default.Dashboard),
                        Pair(AppScreen.MASTER_USERS, Icons.Default.ManageAccounts),
                        Pair(AppScreen.MASTER_KELAS, Icons.Default.Class),
                        Pair(AppScreen.DATA_SISWA, Icons.Default.People),
                        Pair(AppScreen.DATA_KELOMPOK, Icons.Default.Groups),
                        Pair(AppScreen.DATA_ABSENSI, Icons.AutoMirrored.Filled.FactCheck),
                        Pair(AppScreen.LAPORAN, Icons.Default.Assessment),
                        Pair(AppScreen.APPS_SCRIPT, Icons.Default.Code)
                    )

                    navItems.forEach { (screen, icon) ->
                        NavigationDrawerItem(
                            icon = { Icon(icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentScreen == screen,
                            onClick = {
                                viewModel.navigateTo(screen)
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 2.dp)
                                .testTag("drawer_item_${screen.name}")
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    HorizontalDivider()
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Keluar",
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        label = {
                            Text(
                                "Keluar (Logout)",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            viewModel.logout()
                        },
                        modifier = Modifier
                            .padding(12.dp)
                            .testTag("drawer_logout_btn")
                    )
                }
            }
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                contentWindowInsets = WindowInsets.safeDrawing,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = currentScreen.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = currentScreen.subtitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFD8E9FF)
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.testTag("open_drawer_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Buka Menu Navigasi"
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = { viewModel.logout() },
                                modifier = Modifier.testTag("top_logout_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = "Keluar dari Aplikasi"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = Color.White,
                            navigationIconContentColor = Color.White,
                            actionIconContentColor = Color.White
                        )
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Quick Scrollable Menu Bar
                    QuickModuleTabs(
                        currentScreen = currentScreen,
                        onSelectScreen = { viewModel.navigateTo(it) }
                    )

                    when (currentScreen) {
                        AppScreen.DASHBOARD -> DashboardScreen(
                            currentUser = loggedInUser,
                            users = users,
                            kelasList = kelasList,
                            siswaList = siswaList,
                            kelompokList = kelompokList,
                            absensiList = absensiList,
                            onNavigate = { viewModel.navigateTo(it) },
                            onResetDummy = { viewModel.resetToTwoDummyRows() }
                        )

                        AppScreen.MASTER_USERS -> UserAksesScreen(
                            users = users,
                            onSaveUser = { id, u, p, nama, role ->
                                viewModel.saveUser(id, u, p, nama, role)
                            },
                            onDeleteUser = { viewModel.deleteUser(it) },
                            onBackToDashboard = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                        )

                        AppScreen.MASTER_KELAS -> KelasScreen(
                            kelasList = kelasList,
                            onSaveKelas = { id, kode, nama, wali, ta ->
                                viewModel.saveKelas(id, kode, nama, wali, ta)
                            },
                            onDeleteKelas = { viewModel.deleteKelas(it) },
                            onBackToDashboard = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                        )

                        AppScreen.DATA_SISWA -> SiswaScreen(
                            siswaList = siswaList,
                            kelasList = kelasList,
                            onSaveSiswa = { id, nis, nama, jk, kls, alamat ->
                                viewModel.saveSiswa(id, nis, nama, jk, kls, alamat)
                            },
                            onDeleteSiswa = { viewModel.deleteSiswa(it) },
                            onBackToDashboard = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                        )

                        AppScreen.DATA_KELOMPOK -> KelompokScreen(
                            kelompokList = kelompokList,
                            kelasList = kelasList,
                            siswaList = siswaList,
                            onSaveKelompok = { id, namaKlp, kls, siswa, peran, topik ->
                                viewModel.saveKelompok(id, namaKlp, kls, siswa, peran, topik)
                            },
                            onDeleteKelompok = { viewModel.deleteKelompok(it) },
                            onBackToDashboard = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                        )

                        AppScreen.DATA_ABSENSI -> AbsensiScreen(
                            absensiList = absensiList,
                            kelasList = kelasList,
                            siswaList = siswaList,
                            onSaveAbsensi = { id, tgl, kls, nis, siswa, st, ket ->
                                viewModel.saveAbsensi(id, tgl, kls, nis, siswa, st, ket)
                            },
                            onDeleteAbsensi = { viewModel.deleteAbsensi(it) },
                            onBackToDashboard = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                        )

                        AppScreen.LAPORAN -> LaporanScreen(
                            absensiList = absensiList,
                            kelasList = kelasList,
                            kelompokList = kelompokList,
                            onShowMessage = { msg ->
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                            },
                            onBackToDashboard = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                        )

                        AppScreen.APPS_SCRIPT -> AppsScriptStudioScreen(
                            onShowMessage = { msg ->
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                            },
                            onBackToDashboard = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickModuleTabs(
    currentScreen: AppScreen,
    onSelectScreen: (AppScreen) -> Unit
) {
    val modules: List<Triple<AppScreen, String, ImageVector>> = listOf(
        Triple(AppScreen.DASHBOARD, "Dashboard", Icons.Default.Dashboard),
        Triple(AppScreen.MASTER_USERS, "1a. User", Icons.Default.ManageAccounts),
        Triple(AppScreen.MASTER_KELAS, "1b. Kelas", Icons.Default.Class),
        Triple(AppScreen.DATA_SISWA, "2. Siswa", Icons.Default.People),
        Triple(AppScreen.DATA_KELOMPOK, "3. Kelompok", Icons.Default.Groups),
        Triple(AppScreen.DATA_ABSENSI, "4. Absensi", Icons.AutoMirrored.Filled.FactCheck),
        Triple(AppScreen.LAPORAN, "5. Laporan", Icons.Default.Assessment),
        Triple(AppScreen.APPS_SCRIPT, "Code.gs & HTML", Icons.Default.Code)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        modules.forEach { (screen, shortLabel, icon) ->
            FilterChip(
                selected = currentScreen == screen,
                onClick = { onSelectScreen(screen) },
                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = shortLabel
                    )
                },
                label = { Text(shortLabel) },
                modifier = Modifier.testTag("quick_tab_${screen.name}")
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
    }
}

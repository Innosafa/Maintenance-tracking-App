package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AssetEntity
import com.example.ui.navigation.AppDestination
import com.example.ui.screens.assets.AddAssetBottomSheet
import com.example.ui.screens.assets.AssetDetailBottomSheet
import com.example.ui.screens.assets.AssetsScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.expenses.AddExpenseBottomSheet
import com.example.ui.screens.expenses.ExpensesScreen
import com.example.ui.screens.reminders.BroadcastReminderBottomSheet
import com.example.ui.screens.reminders.RemindersScreen
import com.example.ui.screens.repairs.AddRepairBottomSheet
import com.example.ui.screens.repairs.RepairsScreen
import com.example.ui.screens.schedules.AddScheduleBottomSheet
import com.example.ui.screens.schedules.SchedulesScreen
import com.example.ui.screens.technicians.AddTechnicianBottomSheet
import com.example.ui.screens.technicians.TechniciansScreen
import com.example.ui.theme.AssetOpsTheme
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.IndigoPrimaryContainer
import com.example.ui.theme.SlateSecondary
import com.example.ui.viewmodel.AssetOpsViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: AssetOpsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AssetOpsTheme {
                AssetOpsApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetOpsApp(viewModel: AssetOpsViewModel) {
    var currentDestination by remember { mutableStateOf(AppDestination.DASHBOARD) }
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    val assets by viewModel.assets.collectAsStateWithLifecycle()
    val schedules by viewModel.schedules.collectAsStateWithLifecycle()
    val repairLogs by viewModel.repairLogs.collectAsStateWithLifecycle()
    val technicians by viewModel.technicians.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()

    val unreadReminderCount = reminders.count { !it.isRead && !it.isDismissed }

    // Dialog control states
    var showAddAssetDialog by remember { mutableStateOf(false) }
    var selectedAssetForDetail by remember { mutableStateOf<AssetEntity?>(null) }
    var showAddScheduleDialog by remember { mutableStateOf(false) }
    var preselectedAssetForSchedule by remember { mutableStateOf<AssetEntity?>(null) }
    var showLogRepairDialog by remember { mutableStateOf(false) }
    var preselectedAssetForRepair by remember { mutableStateOf<AssetEntity?>(null) }
    var showAddTechDialog by remember { mutableStateOf(false) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var preselectedAssetForExpense by remember { mutableStateOf<AssetEntity?>(null) }
    var showBroadcastReminderDialog by remember { mutableStateOf(false) }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbarMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MAINTENX PRO",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary,
                            letterSpacing = 1.5.sp,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "AssetOps Hub",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 20.sp
                        )
                    }
                    IconButton(
                        onClick = { currentDestination = AppDestination.REMINDERS },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(IndigoPrimaryContainer)
                            .border(1.dp, IndigoPrimary.copy(alpha = 0.2f), CircleShape)
                            .testTag("btn_top_reminders")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadReminderCount > 0) {
                                    Badge(containerColor = MaterialTheme.colorScheme.error) {
                                        Text("$unreadReminderCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Reminders & Alerts",
                                tint = IndigoPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                    modifier = Modifier.border(
                        BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    )
                ) {
                    NavigationBarItem(
                        selected = currentDestination == AppDestination.DASHBOARD,
                        onClick = { currentDestination = AppDestination.DASHBOARD },
                        icon = { Icon(Icons.Default.GridView, contentDescription = "Dashboard") },
                        label = { Text("Dashboard", fontSize = 11.sp, fontWeight = if (currentDestination == AppDestination.DASHBOARD) FontWeight.Bold else FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndigoPrimary,
                            selectedTextColor = IndigoPrimary,
                            indicatorColor = IndigoPrimaryContainer,
                            unselectedIconColor = SlateSecondary,
                            unselectedTextColor = SlateSecondary
                        ),
                        modifier = Modifier.testTag("nav_dashboard")
                    )
                    NavigationBarItem(
                        selected = currentDestination == AppDestination.ASSETS,
                        onClick = { currentDestination = AppDestination.ASSETS },
                        icon = { Icon(Icons.Default.Inventory2, contentDescription = "Assets") },
                        label = { Text("Assets", fontSize = 11.sp, fontWeight = if (currentDestination == AppDestination.ASSETS) FontWeight.Bold else FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndigoPrimary,
                            selectedTextColor = IndigoPrimary,
                            indicatorColor = IndigoPrimaryContainer,
                            unselectedIconColor = SlateSecondary,
                            unselectedTextColor = SlateSecondary
                        ),
                        modifier = Modifier.testTag("nav_assets")
                    )
                    NavigationBarItem(
                        selected = currentDestination == AppDestination.SCHEDULES,
                        onClick = { currentDestination = AppDestination.SCHEDULES },
                        icon = { Icon(Icons.Default.EventNote, contentDescription = "Schedules") },
                        label = { Text("PM Plans", fontSize = 11.sp, fontWeight = if (currentDestination == AppDestination.SCHEDULES) FontWeight.Bold else FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndigoPrimary,
                            selectedTextColor = IndigoPrimary,
                            indicatorColor = IndigoPrimaryContainer,
                            unselectedIconColor = SlateSecondary,
                            unselectedTextColor = SlateSecondary
                        ),
                        modifier = Modifier.testTag("nav_schedules")
                    )
                    NavigationBarItem(
                        selected = currentDestination == AppDestination.REPAIRS,
                        onClick = { currentDestination = AppDestination.REPAIRS },
                        icon = { Icon(Icons.Default.Build, contentDescription = "Repairs") },
                        label = { Text("Repairs", fontSize = 11.sp, fontWeight = if (currentDestination == AppDestination.REPAIRS) FontWeight.Bold else FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndigoPrimary,
                            selectedTextColor = IndigoPrimary,
                            indicatorColor = IndigoPrimaryContainer,
                            unselectedIconColor = SlateSecondary,
                            unselectedTextColor = SlateSecondary
                        ),
                        modifier = Modifier.testTag("nav_repairs")
                    )
                    NavigationBarItem(
                        selected = currentDestination == AppDestination.TECHNICIANS,
                        onClick = { currentDestination = AppDestination.TECHNICIANS },
                        icon = { Icon(Icons.Default.Engineering, contentDescription = "Technicians") },
                        label = { Text("Field Techs", fontSize = 11.sp, fontWeight = if (currentDestination == AppDestination.TECHNICIANS) FontWeight.Bold else FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndigoPrimary,
                            selectedTextColor = IndigoPrimary,
                            indicatorColor = IndigoPrimaryContainer,
                            unselectedIconColor = SlateSecondary,
                            unselectedTextColor = SlateSecondary
                        ),
                        modifier = Modifier.testTag("nav_technicians")
                    )
                    NavigationBarItem(
                        selected = currentDestination == AppDestination.EXPENSES,
                        onClick = { currentDestination = AppDestination.EXPENSES },
                        icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Expenses") },
                        label = { Text("Expenses", fontSize = 11.sp, fontWeight = if (currentDestination == AppDestination.EXPENSES) FontWeight.Bold else FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndigoPrimary,
                            selectedTextColor = IndigoPrimary,
                            indicatorColor = IndigoPrimaryContainer,
                            unselectedIconColor = SlateSecondary,
                            unselectedTextColor = SlateSecondary
                        ),
                        modifier = Modifier.testTag("nav_expenses")
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = currentDestination,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { destination ->
                when (destination) {
                    AppDestination.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigateTo = { currentDestination = it },
                        onOpenAddAsset = { showAddAssetDialog = true },
                        onOpenAddSchedule = {
                            preselectedAssetForSchedule = null
                            showAddScheduleDialog = true
                        },
                        onOpenLogRepair = {
                            preselectedAssetForRepair = null
                            showLogRepairDialog = true
                        },
                        onOpenBroadcastReminder = { showBroadcastReminderDialog = true },
                        onAssetClick = { selectedAssetForDetail = it }
                    )
                    AppDestination.ASSETS -> AssetsScreen(
                        viewModel = viewModel,
                        onAssetClick = { selectedAssetForDetail = it },
                        onOpenAddAsset = { showAddAssetDialog = true }
                    )
                    AppDestination.SCHEDULES -> SchedulesScreen(
                        viewModel = viewModel,
                        onOpenAddSchedule = {
                            preselectedAssetForSchedule = null
                            showAddScheduleDialog = true
                        }
                    )
                    AppDestination.REPAIRS -> RepairsScreen(
                        viewModel = viewModel,
                        onOpenLogRepair = {
                            preselectedAssetForRepair = null
                            showLogRepairDialog = true
                        }
                    )
                    AppDestination.TECHNICIANS -> TechniciansScreen(
                        viewModel = viewModel,
                        onOpenAddTechnician = { showAddTechDialog = true }
                    )
                    AppDestination.EXPENSES -> ExpensesScreen(
                        viewModel = viewModel,
                        onOpenAddExpense = {
                            preselectedAssetForExpense = null
                            showAddExpenseDialog = true
                        }
                    )
                    AppDestination.REMINDERS -> RemindersScreen(
                        viewModel = viewModel,
                        onOpenBroadcast = { showBroadcastReminderDialog = true }
                    )
                }
            }
        }
    }

    // Modal Bottom Sheets
    if (showAddAssetDialog) {
        AddAssetBottomSheet(
            technicians = technicians,
            onDismiss = { showAddAssetDialog = false },
            onAddAsset = { tagId, name, category, location, dept, serial, cost, lifespan, stage, health, tech, notes ->
                viewModel.addAsset(tagId, name, category, location, dept, serial, cost, lifespan, stage, health, tech, notes)
            }
        )
    }

    selectedAssetForDetail?.let { asset ->
        val assetSchedules = schedules.filter { it.assetId == asset.id }
        val assetRepairs = repairLogs.filter { it.assetId == asset.id }
        val assetExpenses = expenses.filter { it.assetId == asset.id }

        AssetDetailBottomSheet(
            asset = asset,
            schedules = assetSchedules,
            repairLogs = assetRepairs,
            expenses = assetExpenses,
            onDismiss = { selectedAssetForDetail = null },
            onUpdateAsset = { updated ->
                viewModel.updateAsset(updated)
                selectedAssetForDetail = updated
            },
            onDeleteAsset = { toDelete ->
                viewModel.deleteAsset(toDelete)
            },
            onScheduleForAsset = { targetAsset ->
                preselectedAssetForSchedule = targetAsset
                showAddScheduleDialog = true
            },
            onRepairForAsset = { targetAsset ->
                preselectedAssetForRepair = targetAsset
                showLogRepairDialog = true
            }
        )
    }

    if (showAddScheduleDialog) {
        AddScheduleBottomSheet(
            assets = assets,
            technicians = technicians,
            preselectedAsset = preselectedAssetForSchedule,
            onDismiss = {
                showAddScheduleDialog = false
                preselectedAssetForSchedule = null
            },
            onAddSchedule = { asset, title, frequency, priority, schedDate, techName, hours, checklist, notes ->
                viewModel.addSchedule(asset, title, frequency, priority, schedDate, techName, hours, checklist, notes)
            }
        )
    }

    if (showLogRepairDialog) {
        AddRepairBottomSheet(
            assets = assets,
            technicians = technicians,
            preselectedAsset = preselectedAssetForRepair,
            onDismiss = {
                showLogRepairDialog = false
                preselectedAssetForRepair = null
            },
            onLogRepair = { asset, title, sev, rca, action, parts, downtime, techName, cost, status ->
                viewModel.logRepair(asset, title, sev, rca, action, parts, downtime, techName, cost, status)
            }
        )
    }

    if (showAddTechDialog) {
        AddTechnicianBottomSheet(
            onDismiss = { showAddTechDialog = false },
            onAddTechnician = { name, role, phone, email, loc ->
                viewModel.addTechnician(name, role, phone, email, loc)
            }
        )
    }

    if (showAddExpenseDialog) {
        AddExpenseBottomSheet(
            assets = assets,
            preselectedAsset = preselectedAssetForExpense,
            onDismiss = {
                showAddExpenseDialog = false
                preselectedAssetForExpense = null
            },
            onAddExpense = { asset, cat, amount, invoice, desc, vendor ->
                viewModel.addExpense(asset, cat, amount, invoice, desc, vendor)
            }
        )
    }

    if (showBroadcastReminderDialog) {
        BroadcastReminderBottomSheet(
            onDismiss = { showBroadcastReminderDialog = false },
            onSendReminder = { title, msg, priority, audience ->
                viewModel.sendReminder(title, msg, priority, audience)
            }
        )
    }
}

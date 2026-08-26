package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.EventNote
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    DASHBOARD("Dashboard", Icons.Filled.Assessment, Icons.Outlined.Assessment),
    ASSETS("Assets", Icons.Filled.Inventory2, Icons.Outlined.Inventory2),
    SCHEDULES("Schedules", Icons.Filled.EventNote, Icons.Outlined.EventNote),
    REPAIRS("Repairs", Icons.Filled.Build, Icons.Outlined.Build),
    TECHNICIANS("Technicians", Icons.Filled.Engineering, Icons.Outlined.Engineering),
    EXPENSES("Expenses", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong),
    REMINDERS("Alerts", Icons.Filled.NotificationsActive, Icons.Outlined.NotificationsActive)
}

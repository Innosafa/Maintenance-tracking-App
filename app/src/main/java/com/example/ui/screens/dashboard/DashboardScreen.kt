package com.example.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.MaintenanceReminderEntity
import com.example.data.model.MaintenanceScheduleEntity
import com.example.data.model.TechnicianEntity
import com.example.ui.components.HealthScoreBar
import com.example.ui.components.MetricSummaryCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatCurrency
import com.example.ui.components.formatDate
import com.example.ui.navigation.AppDestination
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.AmberTertiaryContainer
import com.example.ui.theme.BorderSubtleLight
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.IndigoPrimaryContainer
import com.example.ui.theme.SlateSecondary
import com.example.ui.theme.StatusActive
import com.example.ui.theme.StatusActiveBorder
import com.example.ui.theme.StatusActiveContainer
import com.example.ui.theme.StatusActiveText
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusCriticalBorder
import com.example.ui.theme.StatusCriticalContainer
import com.example.ui.theme.StatusCriticalText
import com.example.ui.theme.StatusInProgress
import com.example.ui.theme.StatusInProgressBorder
import com.example.ui.theme.StatusInProgressContainer
import com.example.ui.theme.StatusInProgressText
import com.example.ui.theme.StatusNeutral
import com.example.ui.theme.StatusNeutralBorder
import com.example.ui.theme.StatusNeutralContainer
import com.example.ui.theme.StatusNeutralText
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningBorder
import com.example.ui.theme.StatusWarningContainer
import com.example.ui.theme.StatusWarningText
import com.example.ui.viewmodel.AssetOpsViewModel

@Composable
fun DashboardScreen(
    viewModel: AssetOpsViewModel,
    onNavigateTo: (AppDestination) -> Unit,
    onOpenAddAsset: () -> Unit,
    onOpenAddSchedule: () -> Unit,
    onOpenLogRepair: () -> Unit,
    onOpenBroadcastReminder: () -> Unit,
    onAssetClick: (AssetEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val metrics by viewModel.dashboardMetrics.collectAsStateWithLifecycle()
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()
    val schedules by viewModel.schedules.collectAsStateWithLifecycle()
    val technicians by viewModel.technicians.collectAsStateWithLifecycle()
    val assets by viewModel.assets.collectAsStateWithLifecycle()

    val urgentReminders = reminders.filter { !it.isDismissed && (it.priority == "Critical" || it.priority == "Urgent") }
    val upcomingOrders = schedules.filter { it.status == "Scheduled" || it.status == "In Progress" || it.status == "Overdue" }.take(4)
    val activeTechs = technicians.take(6)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("screen_dashboard"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. Clean Minimal Hero Stats Grid (Design matching HTML: 2 large cards)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Live Status Badge Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .border(1.dp, StatusActiveBorder, RoundedCornerShape(50)),
                        color = StatusActiveContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(StatusActive)
                            )
                            Text(
                                text = "Telemetry Live • ${metrics.totalAssets} Monitored",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = StatusActiveText,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.simulateTelemetryWarning() },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(IndigoPrimaryContainer)
                            .testTag("btn_simulate_telemetry")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = "Simulate Telemetry Warning",
                            tint = IndigoPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Two Hero Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left Hero Card (Indigo solid)
                    MetricSummaryCard(
                        title = "Scheduled Today",
                        value = "${metrics.openWorkOrders}",
                        subtitle = "${metrics.overdueSchedules} Overdue",
                        icon = Icons.Default.EventNote,
                        accentColor = IndigoPrimary,
                        isPrimaryHighlight = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_work_orders")
                    )

                    // Right Hero Card (Clean White)
                    MetricSummaryCard(
                        title = "Total Expenses",
                        value = formatCurrency(metrics.totalMaintenanceCost),
                        subtitle = "YTD Budget Logged",
                        icon = Icons.Default.ReceiptLong,
                        accentColor = StatusActive,
                        isPrimaryHighlight = false,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_expenses")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Secondary Metrics Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricSummaryCard(
                        title = "Active Assets",
                        value = "${metrics.activeAssets}/${metrics.totalAssets}",
                        subtitle = "Avg Health: ${metrics.avgHealthScore}%",
                        icon = Icons.Default.Inventory2,
                        accentColor = IndigoPrimary,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_active_assets")
                    )
                    MetricSummaryCard(
                        title = "Field Technicians",
                        value = "${metrics.activeTechniciansOnDuty}",
                        subtitle = "Active on Duty",
                        icon = Icons.Default.Engineering,
                        accentColor = AmberTertiary,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_technicians")
                    )
                }
            }
        }

        // 2. Active Technicians Section (Matching HTML horizontal scrolling pill design)
        item {
            Spacer(modifier = Modifier.height(6.dp))
            SectionHeader(
                title = "Active Technicians",
                actionLabel = "View All",
                onActionClick = { onNavigateTo(AppDestination.TECHNICIANS) }
            )
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(activeTechs, key = { it.id }) { tech ->
                    val (bgColor, textColor, borderColor, dotColor, statusLabel) = when (tech.status) {
                        "Available", "Active", "Dispatched" ->
                            Tuple5(StatusActiveContainer, StatusActiveText, StatusActiveBorder, StatusActive, "On-site")
                        "In Repair", "On Break" ->
                            Tuple5(StatusWarningContainer, StatusWarningText, StatusWarningBorder, StatusWarning, "Logistics")
                        else ->
                            Tuple5(StatusNeutralContainer, StatusNeutralText, StatusNeutralBorder, StatusNeutral, "Off")
                    }

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(50))
                            .clickable { onNavigateTo(AppDestination.TECHNICIANS) },
                        color = bgColor
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(dotColor)
                            )
                            Text(
                                text = "${tech.name} ($statusLabel)",
                                color = textColor,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // 3. Critical Alert Carousel if any
        if (urgentReminders.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                CriticalAlertsCarousel(
                    reminders = urgentReminders,
                    onDismiss = { viewModel.dismissReminder(it.id) },
                    onNavigateToAlerts = { onNavigateTo(AppDestination.REMINDERS) }
                )
            }
        }

        // 4. Urgent Maintenance / Priority Work Orders (Matching HTML clean white rounded-2xl cards)
        item {
            Spacer(modifier = Modifier.height(14.dp))
            SectionHeader(
                title = "Urgent Maintenance",
                actionLabel = "View All (${schedules.size})",
                onActionClick = { onNavigateTo(AppDestination.SCHEDULES) }
            )
        }

        if (upcomingOrders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "All scheduled maintenance orders are up to date.",
                        modifier = Modifier.padding(20.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(upcomingOrders, key = { it.id }) { schedule ->
                UrgentMaintenanceCard(
                    schedule = schedule,
                    onStatusChange = { newStatus -> viewModel.updateScheduleStatus(schedule, newStatus) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }

        // 5. Quick Actions & Dispatch
        item {
            Spacer(modifier = Modifier.height(14.dp))
            SectionHeader(title = "Quick Dispatch & Registry")
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onOpenLogRepair,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_quick_log_repair"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Repair", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    FilledTonalButton(
                        onClick = onOpenAddSchedule,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_quick_add_schedule"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = IndigoPrimaryContainer,
                            contentColor = IndigoPrimary
                        )
                    ) {
                        Icon(Icons.Default.EventNote, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Schedule", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onOpenAddAsset,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_quick_add_asset"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Register Asset", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                    OutlinedButton(
                        onClick = onOpenBroadcastReminder,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_quick_broadcast"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp), tint = AmberTertiary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send Alert", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }

        // 6. Asset Health Watchlist
        item {
            Spacer(modifier = Modifier.height(14.dp))
            SectionHeader(
                title = "Asset Health Watchlist",
                actionLabel = "All Assets",
                onActionClick = { onNavigateTo(AppDestination.ASSETS) }
            )
        }

        items(assets.sortedBy { it.healthScore }.take(3), key = { it.id }) { asset ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable { onAssetClick(asset) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = asset.tagId,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary
                            )
                            StatusBadge(status = asset.lifecycleStage)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = asset.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${asset.location} • Next Maint: ${formatDate(asset.nextScheduledDate)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        HealthScoreBar(score = asset.healthScore, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

private data class Tuple5<A, B, C, D, E>(val first: A, val second: B, val third: C, val fourth: D, val fifth: E)

@Composable
fun UrgentMaintenanceCard(
    schedule: MaintenanceScheduleEntity,
    onStatusChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isCritical = schedule.priority == "Critical" || schedule.status == "Overdue"
    val iconBgColor = if (isCritical) StatusCriticalContainer else IndigoPrimaryContainer
    val iconColor = if (isCritical) StatusCritical else IndigoPrimary
    val iconVector = if (isCritical) Icons.Default.Warning else Icons.Default.Bolt

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rounded icon block
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Main Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = schedule.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = "${schedule.assetName} • ${schedule.assignedTechName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Status & Time block
            Column(horizontalAlignment = Alignment.End) {
                StatusBadge(status = if (isCritical) "Critical" else "Routine")
                Spacer(modifier = Modifier.height(4.dp))
                if (schedule.status != "Completed") {
                    TextButton(
                        onClick = {
                            val nextStatus = if (schedule.status == "Scheduled") "In Progress" else "Completed"
                            onStatusChange(nextStatus)
                        },
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("btn_update_schedule_${schedule.id}"),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = if (schedule.status == "Scheduled") "Start" else "Done",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary
                        )
                    }
                } else {
                    Text(
                        text = "Done",
                        fontSize = 11.sp,
                        color = StatusActiveText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CriticalAlertsCarousel(
    reminders: List<MaintenanceReminderEntity>,
    onDismiss: (MaintenanceReminderEntity) -> Unit,
    onNavigateToAlerts: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        reminders.take(2).forEach { alert ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (alert.priority == "Critical") StatusCriticalContainer else StatusWarningContainer
                ),
                border = BorderStroke(
                    1.dp,
                    if (alert.priority == "Critical") StatusCriticalBorder else StatusWarningBorder
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (alert.priority == "Critical") StatusCritical.copy(alpha = 0.15f) else StatusWarning.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (alert.priority == "Critical") StatusCritical else StatusWarning,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = alert.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (alert.priority == "Critical") StatusCriticalText else StatusWarningText
                        )
                        Text(
                            text = alert.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    TextButton(
                        onClick = { onDismiss(alert) },
                        modifier = Modifier.testTag("btn_dismiss_alert_${alert.id}")
                    ) {
                        Text("Dismiss", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

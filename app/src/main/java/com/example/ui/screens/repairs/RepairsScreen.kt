package com.example.ui.screens.repairs

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.RepairLogEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatCurrency
import com.example.ui.components.formatDate
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.StatusActive
import com.example.ui.theme.StatusCritical
import com.example.ui.viewmodel.AssetOpsViewModel

@Composable
fun RepairsScreen(
    viewModel: AssetOpsViewModel,
    onOpenLogRepair: () -> Unit,
    modifier: Modifier = Modifier
) {
    val repairLogs by viewModel.repairLogs.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf("All") }

    val tabs = listOf("All", "Open", "Parts Pending", "Resolved", "Critical Breakdown")

    val filteredLogs = repairLogs.filter { log ->
        when (selectedTab) {
            "All" -> true
            "Open" -> log.status == "Open" || log.status == "Under Investigation"
            "Parts Pending" -> log.status == "Parts Pending"
            "Resolved" -> log.status == "Resolved"
            "Critical Breakdown" -> log.severity == "Critical Breakdown"
            else -> true
        }
    }

    val totalDowntimeMinutes = repairLogs.sumOf { it.downtimeMinutes }
    val totalRepairCost = repairLogs.sumOf { it.totalCost }

    Scaffold(
        modifier = modifier.testTag("screen_repairs"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenLogRepair,
                containerColor = IndigoPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_log_repair")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Log Incident")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Downtime & Cost Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total Downtime Recorded", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(3.dp))
                        Text("${totalDowntimeMinutes / 60}h ${totalDowntimeMinutes % 60}m", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = StatusCritical)
                    }
                    Box(modifier = Modifier.height(32.dp).width(1.dp).padding(vertical = 2.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total Incident Repair Cost", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(formatCurrency(totalRepairCost), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = IndigoPrimary)
                    }
                }
            }

            // Tabs
            ScrollableTabRow(
                selectedTabIndex = tabs.indexOf(selectedTab).coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp,
                containerColor = Color.Transparent,
                divider = {}
            ) {
                tabs.forEach { tab ->
                    val isSelected = tab == selectedTab
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        text = {
                            Text(
                                text = tab,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            if (filteredLogs.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Build,
                    title = "No Repair Logs Found",
                    description = "No maintenance breakdown or repair records found in '$selectedTab'."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredLogs, key = { it.id }) { log ->
                        RepairLogCard(
                            log = log,
                            onResolve = {
                                viewModel.logRepair(
                                    asset = viewModel.assets.value.find { it.id == log.assetId } ?: return@RepairLogCard,
                                    issueTitle = log.issueTitle,
                                    severity = log.severity,
                                    rootCause = log.rootCause,
                                    actionTaken = log.actionTaken.ifEmpty { "Repaired and verified under operational load." },
                                    partsReplaced = log.partsReplaced,
                                    downtimeMinutes = log.downtimeMinutes,
                                    technicianName = log.technicianName,
                                    totalCost = log.totalCost,
                                    status = "Resolved"
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RepairLogCard(
    log: RepairLogEntity,
    onResolve: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_repair_${log.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = log.assetTag,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = IndigoPrimary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusBadge(status = log.severity)
                    StatusBadge(status = log.status)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = log.issueTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "${log.assetName} • Reported ${formatDate(log.reportedDate)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            if (log.rootCause.isNotBlank()) {
                Text(
                    text = "Root Cause Analysis (RCA):",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = log.rootCause,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            if (log.actionTaken.isNotBlank()) {
                Text(
                    text = "Corrective Action:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = log.actionTaken,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            if (log.partsReplaced.isNotBlank()) {
                Text(
                    text = "Parts Replaced: ${log.partsReplaced}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Downtime: ${log.downtimeMinutes} mins",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (log.downtimeMinutes > 120) StatusCritical else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Cost: ${formatCurrency(log.totalCost)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = IndigoPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (log.status != "Resolved") {
                Button(
                    onClick = onResolve,
                    modifier = Modifier.fillMaxWidth().testTag("btn_resolve_repair_${log.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusActive),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Certify Fix & Mark Resolved", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

package com.example.ui.screens.assets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.data.model.AssetEntity
import com.example.data.model.ExpenseRecordEntity
import com.example.data.model.MaintenanceScheduleEntity
import com.example.data.model.RepairLogEntity
import com.example.ui.components.HealthScoreBar
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatCurrency
import com.example.ui.components.formatDate
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.StatusActive
import com.example.ui.theme.StatusCritical

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetDetailBottomSheet(
    asset: AssetEntity,
    schedules: List<MaintenanceScheduleEntity>,
    repairLogs: List<RepairLogEntity>,
    expenses: List<ExpenseRecordEntity>,
    onDismiss: () -> Unit,
    onUpdateAsset: (AssetEntity) -> Unit,
    onDeleteAsset: (AssetEntity) -> Unit,
    onScheduleForAsset: (AssetEntity) -> Unit,
    onRepairForAsset: (AssetEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isEditingLifecycle by remember { mutableStateOf(false) }
    var selectedStage by remember { mutableStateOf(asset.lifecycleStage) }
    var healthScoreValue by remember { mutableFloatStateOf(asset.healthScore.toFloat()) }
    var stageMenuExpanded by remember { mutableStateOf(false) }
    var notesText by remember { mutableStateOf(asset.notes) }

    val lifecycleStages = listOf("Procured", "Active", "In Maintenance", "Degraded", "Decommissioned")
    val totalExpense = expenses.sumOf { it.amount }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 40.dp)
                .testTag("dialog_asset_detail")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = asset.tagId,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusBadge(status = selectedStage)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = asset.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Health Score & Lifecycle Adjuster
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Asset Condition & Lifecycle Status",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HealthScoreBar(score = healthScoreValue.toInt())
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Adjust Condition Index:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${healthScoreValue.toInt()}%",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary
                        )
                    }
                    Slider(
                        value = healthScoreValue,
                        onValueChange = { healthScoreValue = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = IndigoPrimary, activeTrackColor = IndigoPrimary)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Lifecycle stage picker
                    ExposedDropdownMenuBox(
                        expanded = stageMenuExpanded,
                        onExpandedChange = { stageMenuExpanded = !stageMenuExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedStage,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Lifecycle Stage") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stageMenuExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = stageMenuExpanded,
                            onDismissRequest = { stageMenuExpanded = false }
                        ) {
                            lifecycleStages.forEach { stage ->
                                DropdownMenuItem(
                                    text = { Text(stage) },
                                    onClick = {
                                        selectedStage = stage
                                        stageMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            onUpdateAsset(
                                asset.copy(
                                    lifecycleStage = selectedStage,
                                    healthScore = healthScoreValue.toInt(),
                                    notes = notesText
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth().testTag("btn_save_asset_condition"),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save Status Changes", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Specs Grid
            Text(
                text = "Asset Specifications",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    SpecRow(label = "Category", value = asset.category)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    SpecRow(label = "Location", value = asset.location)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    SpecRow(label = "Department", value = asset.department)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    SpecRow(label = "Serial Number", value = asset.serialNumber)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    SpecRow(label = "Purchase Cost", value = formatCurrency(asset.purchaseCost))
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    SpecRow(label = "Expected Lifespan", value = "${asset.expectedLifespanMonths} months")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    SpecRow(label = "Assigned Lead", value = asset.assignedTechName)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    SpecRow(label = "Last Maintenance", value = formatDate(asset.lastMaintenanceDate))
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    SpecRow(label = "Next Scheduled", value = formatDate(asset.nextScheduledDate))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Maintenance Financials for this asset
            Text(
                text = "Maintenance Expenses Recorded",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Logged Expenses:", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = formatCurrency(totalExpense),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary
                        )
                    }
                    if (expenses.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        expenses.take(3).forEach { exp ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${exp.expenseCategory} (${formatDate(exp.date)})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = formatCurrency(exp.amount),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Repair & Maintenance History
            Text(
                text = "Repair & Maintenance History (${schedules.size + repairLogs.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (schedules.isEmpty() && repairLogs.isEmpty()) {
                Text(
                    text = "No repair logs or maintenance records for this asset yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                schedules.forEach { sch ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(sch.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "Scheduled: ${formatDate(sch.scheduledDate)} • ${sch.assignedTechName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            StatusBadge(status = sch.status)
                        }
                    }
                }
                repairLogs.forEach { log ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(log.issueTitle, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                StatusBadge(status = log.status)
                            }
                            Text(
                                text = "Action: ${log.actionTaken} (${log.downtimeMinutes}m downtime)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        onDismiss()
                        onScheduleForAsset(asset)
                    },
                    modifier = Modifier.weight(1f).testTag("btn_schedule_for_asset"),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.EventNote, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Schedule", fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = {
                        onDismiss()
                        onRepairForAsset(asset)
                    },
                    modifier = Modifier.weight(1f).testTag("btn_repair_for_asset"),
                    colors = ButtonDefaults.buttonColors(containerColor = AmberTertiary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Log Repair", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    onDeleteAsset(asset)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().testTag("btn_delete_asset"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusCritical),
                border = BorderStroke(1.dp, StatusCritical.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Decommission & Remove Asset", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

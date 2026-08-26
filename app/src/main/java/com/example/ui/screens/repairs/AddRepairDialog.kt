package com.example.ui.screens.repairs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.AssetEntity
import com.example.data.model.TechnicianEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRepairBottomSheet(
    assets: List<AssetEntity>,
    technicians: List<TechnicianEntity>,
    preselectedAsset: AssetEntity? = null,
    onDismiss: () -> Unit,
    onLogRepair: (
        asset: AssetEntity,
        issueTitle: String,
        severity: String,
        rootCause: String,
        actionTaken: String,
        partsReplaced: String,
        downtimeMinutes: Int,
        technicianName: String,
        totalCost: Double,
        status: String
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val severities = listOf("Minor", "Moderate", "Critical Breakdown")
    val statuses = listOf("Open", "Under Investigation", "Parts Pending", "Resolved")

    var selectedAsset by remember { mutableStateOf(preselectedAsset ?: assets.firstOrNull()) }
    var issueTitle by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf(severities[1]) } // Moderate
    var rootCause by remember { mutableStateOf("") }
    var actionTaken by remember { mutableStateOf("") }
    var partsReplaced by remember { mutableStateOf("") }
    var downtimeMinutesText by remember { mutableStateOf("60") }
    var totalCostText by remember { mutableStateOf("250") }
    var assignedTech by remember {
        mutableStateOf(selectedAsset?.assignedTechName?.ifEmpty { technicians.firstOrNull()?.name } ?: technicians.firstOrNull()?.name ?: "Field Tech")
    }
    var status by remember { mutableStateOf(statuses[0]) }

    var assetExpanded by remember { mutableStateOf(false) }
    var sevExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }
    var techExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 40.dp)
                .testTag("dialog_log_repair")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Log Incident & Repair Order",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Asset picker
            ExposedDropdownMenuBox(
                expanded = assetExpanded,
                onExpandedChange = { assetExpanded = !assetExpanded }
            ) {
                OutlinedTextField(
                    value = selectedAsset?.let { "${it.tagId} - ${it.name}" } ?: "Select Asset",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Affected Asset *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = assetExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth().testTag("input_repair_asset")
                )
                ExposedDropdownMenu(
                    expanded = assetExpanded,
                    onDismissRequest = { assetExpanded = false }
                ) {
                    assets.forEach { a ->
                        DropdownMenuItem(
                            text = { Text("${a.tagId} - ${a.name}") },
                            onClick = {
                                selectedAsset = a
                                assignedTech = a.assignedTechName
                                assetExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = issueTitle,
                onValueChange = { issueTitle = it },
                label = { Text("Issue / Breakdown Summary *") },
                placeholder = { Text("e.g. Hydraulic Line Pressure Spike & Hose Burst") },
                modifier = Modifier.fillMaxWidth().testTag("input_repair_title"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Severity
                ExposedDropdownMenuBox(
                    expanded = sevExpanded,
                    onExpandedChange = { sevExpanded = !sevExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = severity,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Severity") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sevExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = sevExpanded,
                        onDismissRequest = { sevExpanded = false }
                    ) {
                        severities.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s) },
                                onClick = {
                                    severity = s
                                    sevExpanded = false
                                }
                            )
                        }
                    }
                }

                // Status
                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = !statusExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        statuses.forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st) },
                                onClick = {
                                    status = st
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = rootCause,
                onValueChange = { rootCause = it },
                label = { Text("Root Cause Analysis (RCA)") },
                placeholder = { Text("e.g. High ambient temperature weakened rubber hose lining") },
                modifier = Modifier.fillMaxWidth().testTag("input_repair_rca"),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = actionTaken,
                onValueChange = { actionTaken = it },
                label = { Text("Corrective Action Taken / Planned") },
                placeholder = { Text("e.g. Swapped high-pressure hose, bled air, pressure tested") },
                modifier = Modifier.fillMaxWidth().testTag("input_repair_action"),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = partsReplaced,
                onValueChange = { partsReplaced = it },
                label = { Text("Spare Parts Replaced / Part Numbers") },
                placeholder = { Text("e.g. Parker 2\" High-Temp Steel Braid Hose #PK-991") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = downtimeMinutesText,
                    onValueChange = { downtimeMinutesText = it },
                    label = { Text("Downtime (Mins)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag("input_repair_downtime"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = totalCostText,
                    onValueChange = { totalCostText = it },
                    label = { Text("Total Repair Cost ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag("input_repair_cost"),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tech assigned
            ExposedDropdownMenuBox(
                expanded = techExpanded,
                onExpandedChange = { techExpanded = !techExpanded }
            ) {
                OutlinedTextField(
                    value = assignedTech,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Lead Technician / Field Engineer") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = techExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = techExpanded,
                    onDismissRequest = { techExpanded = false }
                ) {
                    technicians.forEach { tech ->
                        DropdownMenuItem(
                            text = { Text("${tech.name} (${tech.role})") },
                            onClick = {
                                assignedTech = tech.name
                                techExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val asset = selectedAsset
                    val downtime = downtimeMinutesText.toIntOrNull() ?: 0
                    val cost = totalCostText.toDoubleOrNull() ?: 0.0
                    if (asset != null && issueTitle.isNotBlank()) {
                        onLogRepair(
                            asset,
                            issueTitle,
                            severity,
                            rootCause,
                            actionTaken,
                            partsReplaced,
                            downtime,
                            assignedTech,
                            cost,
                            status
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("btn_submit_log_repair"),
                enabled = selectedAsset != null && issueTitle.isNotBlank(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Repair Log & Ledger Entry")
            }
        }
    }
}

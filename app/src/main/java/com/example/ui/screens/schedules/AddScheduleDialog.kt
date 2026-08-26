package com.example.ui.screens.schedules

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
fun AddScheduleBottomSheet(
    assets: List<AssetEntity>,
    technicians: List<TechnicianEntity>,
    preselectedAsset: AssetEntity? = null,
    onDismiss: () -> Unit,
    onAddSchedule: (
        asset: AssetEntity,
        title: String,
        frequency: String,
        priority: String,
        scheduledDate: Long,
        assignedTechName: String,
        estimatedHours: Double,
        checklistText: String,
        notes: String
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val frequencies = listOf("Weekly", "Bi-Weekly", "Monthly", "Quarterly", "Semi-Annual", "Annual")
    val priorities = listOf("Low", "Medium", "High", "Critical")

    var selectedAsset by remember { mutableStateOf(preselectedAsset ?: assets.firstOrNull()) }
    var title by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf(frequencies[2]) } // Monthly
    var priority by remember { mutableStateOf(priorities[1]) } // Medium
    var assignedTech by remember {
        mutableStateOf(selectedAsset?.assignedTechName?.ifEmpty { technicians.firstOrNull()?.name } ?: technicians.firstOrNull()?.name ?: "Field Tech")
    }
    var estimatedHoursText by remember { mutableStateOf("2.5") }
    var checklistText by remember { mutableStateOf("Inspect mechanical bearings & oil seals\nCheck operating temperatures\nClean debris & air filtration intake\nVerify electrical ground connections") }
    var notes by remember { mutableStateOf("") }

    var assetDropdownExpanded by remember { mutableStateOf(false) }
    var freqDropdownExpanded by remember { mutableStateOf(false) }
    var priorityDropdownExpanded by remember { mutableStateOf(false) }
    var techDropdownExpanded by remember { mutableStateOf(false) }

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
                .testTag("dialog_add_schedule")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "New Maintenance Schedule",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Asset selector
            ExposedDropdownMenuBox(
                expanded = assetDropdownExpanded,
                onExpandedChange = { assetDropdownExpanded = !assetDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedAsset?.let { "${it.tagId} - ${it.name}" } ?: "Select Asset",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Target Asset *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = assetDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth().testTag("input_schedule_asset")
                )
                ExposedDropdownMenu(
                    expanded = assetDropdownExpanded,
                    onDismissRequest = { assetDropdownExpanded = false }
                ) {
                    assets.forEach { a ->
                        DropdownMenuItem(
                            text = { Text("${a.tagId} - ${a.name}") },
                            onClick = {
                                selectedAsset = a
                                assignedTech = a.assignedTechName
                                assetDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Maintenance Task Title *") },
                placeholder = { Text("e.g. Quarterly Hydraulic Inspection & Valve Clean") },
                modifier = Modifier.fillMaxWidth().testTag("input_schedule_title"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Frequency
                ExposedDropdownMenuBox(
                    expanded = freqDropdownExpanded,
                    onExpandedChange = { freqDropdownExpanded = !freqDropdownExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = frequency,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Frequency") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = freqDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = freqDropdownExpanded,
                        onDismissRequest = { freqDropdownExpanded = false }
                    ) {
                        frequencies.forEach { f ->
                            DropdownMenuItem(
                                text = { Text(f) },
                                onClick = {
                                    frequency = f
                                    freqDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Priority
                ExposedDropdownMenuBox(
                    expanded = priorityDropdownExpanded,
                    onExpandedChange = { priorityDropdownExpanded = !priorityDropdownExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = priority,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Priority") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = priorityDropdownExpanded,
                        onDismissRequest = { priorityDropdownExpanded = false }
                    ) {
                        priorities.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p) },
                                onClick = {
                                    priority = p
                                    priorityDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Tech selector
                ExposedDropdownMenuBox(
                    expanded = techDropdownExpanded,
                    onExpandedChange = { techDropdownExpanded = !techDropdownExpanded },
                    modifier = Modifier.weight(1.3f)
                ) {
                    OutlinedTextField(
                        value = assignedTech,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Lead Technician") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = techDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = techDropdownExpanded,
                        onDismissRequest = { techDropdownExpanded = false }
                    ) {
                        technicians.forEach { tech ->
                            DropdownMenuItem(
                                text = { Text("${tech.name} (${tech.role})") },
                                onClick = {
                                    assignedTech = tech.name
                                    techDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = estimatedHoursText,
                    onValueChange = { estimatedHoursText = it },
                    label = { Text("Est. Hours") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(0.7f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = checklistText,
                onValueChange = { checklistText = it },
                label = { Text("Standard Procedure Checklist (1 item per line)") },
                modifier = Modifier.fillMaxWidth().testTag("input_schedule_checklist"),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Work Order Instructions & Safety Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val asset = selectedAsset
                    val hours = estimatedHoursText.toDoubleOrNull() ?: 2.0
                    val scheduleDate = System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000)
                    if (asset != null && title.isNotBlank()) {
                        onAddSchedule(
                            asset,
                            title,
                            frequency,
                            priority,
                            scheduleDate,
                            assignedTech,
                            hours,
                            checklistText,
                            notes
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("btn_submit_add_schedule"),
                enabled = selectedAsset != null && title.isNotBlank(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Create Maintenance Order")
            }
        }
    }
}

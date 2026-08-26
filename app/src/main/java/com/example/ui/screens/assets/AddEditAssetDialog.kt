package com.example.ui.screens.assets

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
import androidx.compose.material3.Slider
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.TechnicianEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAssetBottomSheet(
    technicians: List<TechnicianEntity>,
    onDismiss: () -> Unit,
    onAddAsset: (
        tagId: String,
        name: String,
        category: String,
        location: String,
        department: String,
        serialNumber: String,
        purchaseCost: Double,
        lifespanMonths: Int,
        lifecycleStage: String,
        healthScore: Int,
        assignedTech: String,
        notes: String
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val categories = listOf(
        "Manufacturing",
        "HVAC & Climate",
        "Fleet Vehicles",
        "Electrical & Power",
        "Hydraulics & Pumps",
        "IT & Data Center",
        "Facilities & Safety"
    )
    val stages = listOf("Procured", "Active", "In Maintenance", "Degraded", "Decommissioned")

    var tagId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(categories.first()) }
    var location by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var serialNumber by remember { mutableStateOf("") }
    var purchaseCostText by remember { mutableStateOf("") }
    var lifespanMonthsText by remember { mutableStateOf("120") }
    var stage by remember { mutableStateOf("Active") }
    var healthScore by remember { mutableFloatStateOf(95f) }
    var assignedTech by remember { mutableStateOf(technicians.firstOrNull()?.name ?: "Unassigned") }
    var notes by remember { mutableStateOf("") }

    var categoryExpanded by remember { mutableStateOf(false) }
    var stageExpanded by remember { mutableStateOf(false) }
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
                .testTag("dialog_add_asset")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Register Company Asset",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Asset / Machine Name *") },
                placeholder = { Text("e.g. CNC Lathe Axis-4") },
                modifier = Modifier.fillMaxWidth().testTag("input_asset_name"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = tagId,
                    onValueChange = { tagId = it },
                    label = { Text("Tag ID") },
                    placeholder = { Text("AST-8890") },
                    modifier = Modifier.weight(1f).testTag("input_asset_tag"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = serialNumber,
                    onValueChange = { serialNumber = it },
                    label = { Text("Serial Number") },
                    placeholder = { Text("SN-90928") },
                    modifier = Modifier.weight(1f).testTag("input_asset_serial"),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category dropdown
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                category = cat
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location / Bay *") },
                    placeholder = { Text("Plant 1 - Bay 2") },
                    modifier = Modifier.weight(1f).testTag("input_asset_location"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("Department") },
                    placeholder = { Text("Operations") },
                    modifier = Modifier.weight(1f).testTag("input_asset_dept"),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = purchaseCostText,
                    onValueChange = { purchaseCostText = it },
                    label = { Text("Purchase Cost ($)") },
                    placeholder = { Text("45000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag("input_asset_cost"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = lifespanMonthsText,
                    onValueChange = { lifespanMonthsText = it },
                    label = { Text("Lifespan (Months)") },
                    placeholder = { Text("120") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
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
                    label = { Text("Assigned Lead Technician") },
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

            Spacer(modifier = Modifier.height(10.dp))

            Text("Initial Health Condition (${healthScore.toInt()}%)", style = MaterialTheme.typography.labelMedium)
            Slider(
                value = healthScore,
                onValueChange = { healthScore = it },
                valueRange = 0f..100f
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Asset Notes & Maintenance Manual Info") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val cost = purchaseCostText.toDoubleOrNull() ?: 0.0
                    val lifespan = lifespanMonthsText.toIntOrNull() ?: 120
                    if (name.isNotBlank()) {
                        onAddAsset(
                            tagId,
                            name,
                            category,
                            location.ifEmpty { "General Plant" },
                            department.ifEmpty { "Operations" },
                            serialNumber,
                            cost,
                            lifespan,
                            stage,
                            healthScore.toInt(),
                            assignedTech,
                            notes
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("btn_submit_add_asset"),
                enabled = name.isNotBlank(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Register Asset")
            }
        }
    }
}

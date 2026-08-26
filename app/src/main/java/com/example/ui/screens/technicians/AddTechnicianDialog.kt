package com.example.ui.screens.technicians

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTechnicianBottomSheet(
    onDismiss: () -> Unit,
    onAddTechnician: (
        name: String,
        role: String,
        phone: String,
        email: String,
        location: String
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val standardRoles = listOf(
        "Lead Electrical & Automation",
        "HVAC & Thermal Systems Tech",
        "Hydraulics & Mechanical Lead",
        "Fleet & Diesel Senior Mechanic",
        "PLC & Instrumentation Specialist",
        "Plant Safety & Maintenance Tech"
    )

    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(standardRoles.first()) }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Plant 1 - Central Facility") }
    var roleExpanded by remember { mutableStateOf(false) }

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
                .testTag("dialog_add_technician")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add Field Technician",
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
                label = { Text("Full Name *") },
                placeholder = { Text("e.g. Jordan Miller") },
                modifier = Modifier.fillMaxWidth().testTag("input_tech_name"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            ExposedDropdownMenuBox(
                expanded = roleExpanded,
                onExpandedChange = { roleExpanded = !roleExpanded }
            ) {
                OutlinedTextField(
                    value = role,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Specialty / Trade Role") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = roleExpanded,
                    onDismissRequest = { roleExpanded = false }
                ) {
                    standardRoles.forEach { r ->
                        DropdownMenuItem(
                            text = { Text(r) },
                            onClick = {
                                role = r
                                roleExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                placeholder = { Text("+1 (555) 000-0000") },
                modifier = Modifier.fillMaxWidth().testTag("input_tech_phone"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Internal Email") },
                placeholder = { Text("j.miller@company.internal") },
                modifier = Modifier.fillMaxWidth().testTag("input_tech_email"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Primary Base / Station") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAddTechnician(
                            name,
                            role,
                            phone.ifEmpty { "+1 (555) 888-9900" },
                            email.ifEmpty { "${name.lowercase().replace(" ", ".")}@company.internal" },
                            location
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("btn_submit_add_tech"),
                enabled = name.isNotBlank(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Add to Technician Roster")
            }
        }
    }
}

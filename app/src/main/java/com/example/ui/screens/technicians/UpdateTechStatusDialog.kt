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
import com.example.data.model.TechnicianEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateTechStatusBottomSheet(
    technician: TechnicianEntity,
    onDismiss: () -> Unit,
    onUpdateStatus: (technician: TechnicianEntity, newStatus: String, assignment: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val statuses = listOf("Available", "Dispatched", "In Repair", "On Break", "Off Duty")

    var status by remember { mutableStateOf(technician.status) }
    var assignment by remember { mutableStateOf(technician.currentAssignment) }
    var statusExpanded by remember { mutableStateOf(false) }

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
                .testTag("dialog_update_tech_status")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Update Status: ${technician.name}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = technician.role,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status dropdown
            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = !statusExpanded }
            ) {
                OutlinedTextField(
                    value = status,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Real-Time Dispatch Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth().testTag("input_tech_new_status")
                )
                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    statuses.forEach { s ->
                        DropdownMenuItem(
                            text = { Text(s) },
                            onClick = {
                                status = s
                                statusExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = assignment,
                onValueChange = { assignment = it },
                label = { Text("Current Assigned Job / Location") },
                placeholder = { Text("e.g. Dispatched to Plant 2 - Chiller Inspection") },
                modifier = Modifier.fillMaxWidth().testTag("input_tech_assignment"),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    onUpdateStatus(technician, status, assignment)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().testTag("btn_submit_tech_status"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Broadcast Live Status Update")
            }
        }
    }
}

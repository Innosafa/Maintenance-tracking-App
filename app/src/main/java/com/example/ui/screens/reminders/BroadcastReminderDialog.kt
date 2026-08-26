package com.example.ui.screens.reminders

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
fun BroadcastReminderBottomSheet(
    onDismiss: () -> Unit,
    onSendReminder: (title: String, message: String, priority: String, targetAudience: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val priorities = listOf("Normal", "Urgent", "Critical")
    val audiences = listOf("All Technicians", "Shift Supervisor", "Plant 1 Floor Techs", "HVAC Team", "Fleet Mechanics")

    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(priorities[1]) } // Urgent
    var targetAudience by remember { mutableStateOf(audiences.first()) }

    var priorityExpanded by remember { mutableStateOf(false) }
    var audienceExpanded by remember { mutableStateOf(false) }

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
                .testTag("dialog_broadcast_reminder")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dispatch Maintenance Reminder",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Reminder Subject / Alert Title *") },
                placeholder = { Text("e.g. Mandatory Lockout-Tagout Audit at 14:00") },
                modifier = Modifier.fillMaxWidth().testTag("input_reminder_title"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Priority
                ExposedDropdownMenuBox(
                    expanded = priorityExpanded,
                    onExpandedChange = { priorityExpanded = !priorityExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = priority,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Priority Level") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = priorityExpanded,
                        onDismissRequest = { priorityExpanded = false }
                    ) {
                        priorities.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p) },
                                onClick = {
                                    priority = p
                                    priorityExpanded = false
                                }
                            )
                        }
                    }
                }

                // Audience
                ExposedDropdownMenuBox(
                    expanded = audienceExpanded,
                    onExpandedChange = { audienceExpanded = !audienceExpanded },
                    modifier = Modifier.weight(1.3f)
                ) {
                    OutlinedTextField(
                        value = targetAudience,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Target Team") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = audienceExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = audienceExpanded,
                        onDismissRequest = { audienceExpanded = false }
                    ) {
                        audiences.forEach { aud ->
                            DropdownMenuItem(
                                text = { Text(aud) },
                                onClick = {
                                    targetAudience = aud
                                    audienceExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Reminder Details & Directives *") },
                placeholder = { Text("e.g. All operators must verify safety guards and sensor trip wires before the shift handover.") },
                modifier = Modifier.fillMaxWidth().testTag("input_reminder_message"),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (title.isNotBlank() && message.isNotBlank()) {
                        onSendReminder(title, message, priority, targetAudience)
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("btn_submit_broadcast_reminder"),
                enabled = title.isNotBlank() && message.isNotBlank(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Broadcast Maintenance Reminder")
            }
        }
    }
}

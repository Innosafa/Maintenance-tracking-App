package com.example.ui.screens.schedules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MaintenanceScheduleEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatDate
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.StatusActive
import com.example.ui.viewmodel.AssetOpsViewModel

@Composable
fun SchedulesScreen(
    viewModel: AssetOpsViewModel,
    onOpenAddSchedule: () -> Unit,
    modifier: Modifier = Modifier
) {
    val schedules by viewModel.schedules.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.scheduleFilter.collectAsStateWithLifecycle()

    val filterTabs = listOf("All", "Scheduled", "In Progress", "Overdue", "Completed")

    val filteredSchedules = schedules.filter { schedule ->
        when (selectedFilter) {
            "All" -> true
            "Scheduled" -> schedule.status == "Scheduled"
            "In Progress" -> schedule.status == "In Progress"
            "Overdue" -> schedule.status == "Overdue"
            "Completed" -> schedule.status == "Completed"
            else -> true
        }
    }

    Scaffold(
        modifier = modifier.testTag("screen_schedules"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenAddSchedule,
                containerColor = IndigoPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_add_schedule")
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Schedule")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Status Tabs
            ScrollableTabRow(
                selectedTabIndex = filterTabs.indexOf(selectedFilter).coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp,
                containerColor = Color.Transparent,
                divider = {}
            ) {
                filterTabs.forEach { tab ->
                    val isSelected = tab == selectedFilter
                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.setScheduleFilter(tab) },
                        text = {
                            val count = schedules.count { if (tab == "All") true else it.status == tab }
                            Text(
                                text = "$tab ($count)",
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            if (filteredSchedules.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.EventNote,
                    title = "No Schedules in '$selectedFilter'",
                    description = "No preventative maintenance orders found in this category."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredSchedules, key = { it.id }) { schedule ->
                        ScheduleDetailedCard(
                            schedule = schedule,
                            onStatusChange = { newStatus -> viewModel.updateScheduleStatus(schedule, newStatus) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleDetailedCard(
    schedule: MaintenanceScheduleEntity,
    onStatusChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val checklistItems = remember(schedule.checklistText) {
        schedule.checklistText.lines().filter { it.isNotBlank() }
    }
    val checkedMap = remember(schedule.id) { mutableStateMapOf<Int, Boolean>() }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_schedule_${schedule.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = schedule.assetTag,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = IndigoPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${schedule.frequency}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusBadge(status = schedule.priority)
                    StatusBadge(status = schedule.status)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = schedule.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "${schedule.assetName} • Tech: ${schedule.assignedTechName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Scheduled: ${formatDate(schedule.scheduledDate)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Est: ${schedule.estimatedHours} hrs",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (checklistItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Standard Operating Procedure Checklist:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                checklistItems.forEachIndexed { index, item ->
                    val isChecked = checkedMap[index] == true || schedule.status == "Completed"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checkedMap[index] = it },
                            enabled = schedule.status != "Completed",
                            colors = CheckboxDefaults.colors(checkedColor = IndigoPrimary),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isChecked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (schedule.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Notes: ${schedule.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (schedule.status) {
                    "Scheduled" -> {
                        Button(
                            onClick = { onStatusChange("In Progress") },
                            modifier = Modifier.weight(1f).testTag("btn_start_work_${schedule.id}"),
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Start Work Order", fontWeight = FontWeight.SemiBold)
                        }
                    }
                    "In Progress", "Overdue" -> {
                        Button(
                            onClick = { onStatusChange("Completed") },
                            modifier = Modifier.weight(1f).testTag("btn_complete_work_${schedule.id}"),
                            colors = ButtonDefaults.buttonColors(containerColor = StatusActive),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sign Off & Complete", fontWeight = FontWeight.SemiBold)
                        }
                    }
                    "Completed" -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = StatusActive, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Completed & Certified on ${schedule.completedDate?.let { formatDate(it) } ?: "Record"}",
                                style = MaterialTheme.typography.labelMedium,
                                color = StatusActive,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

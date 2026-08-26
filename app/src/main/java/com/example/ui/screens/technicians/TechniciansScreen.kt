package com.example.ui.screens.technicians

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.TechnicianEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.IndigoPrimaryContainer
import com.example.ui.theme.StatusActive
import com.example.ui.viewmodel.AssetOpsViewModel

@Composable
fun TechniciansScreen(
    viewModel: AssetOpsViewModel,
    onOpenAddTechnician: () -> Unit,
    modifier: Modifier = Modifier
) {
    val technicians by viewModel.technicians.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.technicianFilter.collectAsStateWithLifecycle()
    var techToUpdate by remember { mutableStateOf<TechnicianEntity?>(null) }

    val filterTabs = listOf("All", "Available", "In Repair", "Dispatched", "On Break", "Off Duty")

    val filteredTechs = technicians.filter { tech ->
        if (selectedFilter == "All") true else tech.status.equals(selectedFilter, ignoreCase = true)
    }

    Scaffold(
        modifier = modifier.testTag("screen_technicians"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenAddTechnician,
                containerColor = IndigoPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_add_technician")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Tech")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Live Roster Filter Tabs
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
                        onClick = { viewModel.setTechnicianFilter(tab) },
                        text = {
                            val count = technicians.count { if (tab == "All") true else it.status.equals(tab, ignoreCase = true) }
                            Text(
                                text = "$tab ($count)",
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            if (filteredTechs.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Engineering,
                    title = "No Technicians in '$selectedFilter'",
                    description = "No technicians currently logged under this status."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredTechs, key = { it.id }) { tech ->
                        TechnicianCard(
                            tech = tech,
                            onUpdateStatus = { techToUpdate = tech }
                        )
                    }
                }
            }
        }
    }

    techToUpdate?.let { tech ->
        UpdateTechStatusBottomSheet(
            technician = tech,
            onDismiss = { techToUpdate = null },
            onUpdateStatus = { t, newStatus, assignment ->
                viewModel.updateTechnicianStatus(t, newStatus, assignment)
            }
        )
    }
}

@Composable
fun TechnicianCard(
    tech: TechnicianEntity,
    onUpdateStatus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val avatarColors = listOf(
        IndigoPrimary,
        Color(0xFF0D9488),
        AmberTertiary,
        Color(0xFF7C3AED),
        Color(0xFFE11D48)
    )
    val color = avatarColors[tech.avatarColorIndex % avatarColors.size]

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_tech_${tech.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tech.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                        fontWeight = FontWeight.Bold,
                        color = color,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tech.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = tech.role,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                StatusBadge(status = tech.status)
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = tech.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Assignment: ${tech.currentAssignment}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = AmberTertiary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("${tech.rating} • ${tech.tasksCompleted} Tasks Completed", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${tech.phone}"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f).testTag("btn_call_tech_${tech.id}"),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                }

                Button(
                    onClick = onUpdateStatus,
                    modifier = Modifier.weight(1.5f).testTag("btn_status_tech_${tech.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Update Live Status", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

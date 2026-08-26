package com.example.ui.screens.assets

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AssetEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.HealthScoreBar
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatCurrency
import com.example.ui.components.formatDate
import com.example.ui.theme.IndigoPrimary
import com.example.ui.viewmodel.AssetOpsViewModel

@Composable
fun AssetsScreen(
    viewModel: AssetOpsViewModel,
    onAssetClick: (AssetEntity) -> Unit,
    onOpenAddAsset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val assets by viewModel.assets.collectAsStateWithLifecycle()
    val searchQuery by viewModel.assetSearchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedAssetCategory.collectAsStateWithLifecycle()
    val selectedStage by viewModel.selectedLifecycleStage.collectAsStateWithLifecycle()

    val categories = listOf("All", "Manufacturing", "HVAC & Climate", "Fleet Vehicles", "Electrical & Power", "Hydraulics & Pumps", "IT & Data Center")
    val lifecycleStages = listOf("All", "Active", "In Maintenance", "Degraded", "Procured", "Decommissioned")

    val filteredAssets = assets.filter { asset ->
        val matchesSearch = searchQuery.isBlank() ||
                asset.name.contains(searchQuery, ignoreCase = true) ||
                asset.tagId.contains(searchQuery, ignoreCase = true) ||
                asset.location.contains(searchQuery, ignoreCase = true) ||
                asset.serialNumber.contains(searchQuery, ignoreCase = true)

        val matchesCategory = selectedCategory == "All" || asset.category.contains(selectedCategory, ignoreCase = true)
        val matchesStage = selectedStage == "All" || asset.lifecycleStage.equals(selectedStage, ignoreCase = true)

        matchesSearch && matchesCategory && matchesStage
    }

    Scaffold(
        modifier = modifier.testTag("screen_assets"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenAddAsset,
                containerColor = IndigoPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_add_asset")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Asset")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setAssetSearchQuery(it) },
                placeholder = { Text("Search assets by tag, model, location...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setAssetSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("input_search_assets"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedBorderColor = IndigoPrimary
                ),
                singleLine = true
            )

            // Category Tabs
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp,
                containerColor = Color.Transparent,
                divider = {}
            ) {
                categories.forEach { cat ->
                    val isSelected = cat == selectedCategory
                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.setSelectedAssetCategory(cat) },
                        text = {
                            Text(
                                text = cat,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            // Lifecycle Stage quick pills
            ScrollableTabRow(
                selectedTabIndex = lifecycleStages.indexOf(selectedStage).coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp,
                containerColor = Color.Transparent,
                divider = {}
            ) {
                lifecycleStages.forEach { st ->
                    val isSelected = st == selectedStage
                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.setSelectedLifecycleStage(st) },
                        text = {
                            Text(
                                text = "Stage: $st",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            // Asset list
            if (filteredAssets.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Inventory2,
                    title = "No Assets Found",
                    description = "No assets match your search or filter criteria. Try adjusting filters or register a new asset."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredAssets, key = { it.id }) { asset ->
                        AssetCard(
                            asset = asset,
                            onClick = { onAssetClick(asset) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AssetCard(
    asset: AssetEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("card_asset_${asset.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = asset.tagId,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = IndigoPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${asset.category}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusBadge(status = asset.lifecycleStage)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = asset.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "${asset.location} (${asset.department})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            HealthScoreBar(score = asset.healthScore)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lead: ${asset.assignedTechName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Next: ${formatDate(asset.nextScheduledDate)}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

package com.example.ui.screens.expenses

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ExpenseRecordEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.formatCurrency
import com.example.ui.components.formatDate
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.StatusActive
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusWarning
import com.example.ui.viewmodel.AssetOpsViewModel

@Composable
fun ExpensesScreen(
    viewModel: AssetOpsViewModel,
    onOpenAddExpense: () -> Unit,
    modifier: Modifier = Modifier
) {
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf(
        "All",
        "Parts & Materials",
        "Labor & Overtime",
        "Vendor/Contractor",
        "Emergency Dispatch",
        "Lubricants & Consumables"
    )

    val filteredExpenses = expenses.filter { exp ->
        if (selectedCategory == "All") true else exp.expenseCategory.equals(selectedCategory, ignoreCase = true)
    }

    val totalExpense = expenses.sumOf { it.amount }
    val categoryTotals = expenses.groupBy { it.expenseCategory }
        .mapValues { it.value.sumOf { exp -> exp.amount } }

    Scaffold(
        modifier = modifier.testTag("screen_expenses"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenAddExpense,
                containerColor = IndigoPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_add_expense")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Log Expense")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // YTD Ledger Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Total Maintenance Spend (YTD)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatCurrency(totalExpense),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = IndigoPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Spending Breakdown by Category",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    categoryTotals.entries.take(4).forEach { (cat, amount) ->
                        val percent = if (totalExpense > 0) (amount / totalExpense).toFloat() else 0f
                        Column(modifier = Modifier.padding(vertical = 3.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(cat, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("${formatCurrency(amount)} (${(percent * 100).toInt()}%)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            LinearProgressIndicator(
                                progress = { percent },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = when (cat) {
                                    "Emergency Dispatch" -> StatusCritical
                                    "Parts & Materials" -> IndigoPrimary
                                    "Vendor/Contractor" -> StatusWarning
                                    else -> StatusActive
                                },
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }

            // Category filter tabs
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
                        onClick = { selectedCategory = cat },
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

            if (filteredExpenses.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.ReceiptLong,
                    title = "No Expense Records",
                    description = "No maintenance or repair purchase receipts in '$selectedCategory'."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredExpenses, key = { it.id }) { expense ->
                        ExpenseRecordCard(
                            expense = expense,
                            onDelete = { viewModel.showMessage("Expense record #${expense.invoiceRef} preserved in company audit ledger.") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseRecordCard(
    expense: ExpenseRecordEntity,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_expense_${expense.id}"),
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
                Text(
                    text = expense.expenseCategory,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = IndigoPrimary
                )
                Text(
                    text = formatCurrency(expense.amount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = expense.description,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${expense.assetName} • Ref: ${expense.invoiceRef}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vendor: ${expense.vendor}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatDate(expense.date),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

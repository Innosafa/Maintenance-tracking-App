package com.example.ui.screens.expenses

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseBottomSheet(
    assets: List<AssetEntity>,
    preselectedAsset: AssetEntity? = null,
    onDismiss: () -> Unit,
    onAddExpense: (
        asset: AssetEntity,
        category: String,
        amount: Double,
        invoiceRef: String,
        description: String,
        vendor: String
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val categories = listOf(
        "Parts & Materials",
        "Labor & Overtime",
        "Vendor/Contractor",
        "Emergency Dispatch",
        "Lubricants & Consumables",
        "Inspection/Testing"
    )

    var selectedAsset by remember { mutableStateOf(preselectedAsset ?: assets.firstOrNull()) }
    var category by remember { mutableStateOf(categories.first()) }
    var amountText by remember { mutableStateOf("") }
    var invoiceRef by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var vendor by remember { mutableStateOf("") }

    var assetExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

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
                .testTag("dialog_add_expense")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Record Maintenance Expense",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Asset
            ExposedDropdownMenuBox(
                expanded = assetExpanded,
                onExpandedChange = { assetExpanded = !assetExpanded }
            ) {
                OutlinedTextField(
                    value = selectedAsset?.let { "${it.tagId} - ${it.name}" } ?: "Select Asset",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Associated Asset *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = assetExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth().testTag("input_expense_asset")
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
                                assetExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Expense Category") },
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
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount ($) *") },
                    placeholder = { Text("1250.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag("input_expense_amount"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = invoiceRef,
                    onValueChange = { invoiceRef = it },
                    label = { Text("Invoice / PO Ref") },
                    placeholder = { Text("INV-9902") },
                    modifier = Modifier.weight(1f).testTag("input_expense_invoice"),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = vendor,
                onValueChange = { vendor = it },
                label = { Text("Supplier / Vendor / Contractor") },
                placeholder = { Text("e.g. Grainger Industrial Supply") },
                modifier = Modifier.fillMaxWidth().testTag("input_expense_vendor"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Expense Item Description *") },
                placeholder = { Text("e.g. Synthetic hydraulic ISO-46 fluid bulk tote & filter assembly") },
                modifier = Modifier.fillMaxWidth().testTag("input_expense_desc"),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val asset = selectedAsset
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (asset != null && amount > 0 && description.isNotBlank()) {
                        onAddExpense(
                            asset,
                            category,
                            amount,
                            invoiceRef,
                            description,
                            vendor
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("btn_submit_add_expense"),
                enabled = selectedAsset != null && amountText.toDoubleOrNull() != null && description.isNotBlank(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Log to Expense Ledger")
            }
        }
    }
}

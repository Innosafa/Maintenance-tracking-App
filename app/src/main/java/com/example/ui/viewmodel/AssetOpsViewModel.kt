package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.AssetEntity
import com.example.data.model.ExpenseRecordEntity
import com.example.data.model.MaintenanceReminderEntity
import com.example.data.model.MaintenanceScheduleEntity
import com.example.data.model.RepairLogEntity
import com.example.data.model.TechnicianEntity
import com.example.data.repository.AssetOpsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardMetrics(
    val totalAssets: Int = 0,
    val activeAssets: Int = 0,
    val degradedAssets: Int = 0,
    val avgHealthScore: Int = 0,
    val openWorkOrders: Int = 0,
    val overdueSchedules: Int = 0,
    val activeTechniciansOnDuty: Int = 0,
    val totalMaintenanceCost: Double = 0.0,
    val criticalAlertsCount: Int = 0
)

class AssetOpsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AssetOpsRepository

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = AssetOpsRepository(db.assetOpsDao())
    }

    // --- State Streams ---
    val assets: StateFlow<List<AssetEntity>> = repository.allAssets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val schedules: StateFlow<List<MaintenanceScheduleEntity>> = repository.allSchedules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val repairLogs: StateFlow<List<RepairLogEntity>> = repository.allRepairLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val technicians: StateFlow<List<TechnicianEntity>> = repository.allTechnicians
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<ExpenseRecordEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reminders: StateFlow<List<MaintenanceReminderEntity>> = repository.activeReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Filter & Search States ---
    private val _assetSearchQuery = MutableStateFlow("")
    val assetSearchQuery: StateFlow<String> = _assetSearchQuery.asStateFlow()

    private val _selectedAssetCategory = MutableStateFlow("All")
    val selectedAssetCategory: StateFlow<String> = _selectedAssetCategory.asStateFlow()

    private val _selectedLifecycleStage = MutableStateFlow("All")
    val selectedLifecycleStage: StateFlow<String> = _selectedLifecycleStage.asStateFlow()

    private val _scheduleFilter = MutableStateFlow("All") // "All", "Scheduled", "In Progress", "Overdue", "Completed"
    val scheduleFilter: StateFlow<String> = _scheduleFilter.asStateFlow()

    private val _technicianFilter = MutableStateFlow("All") // "All", "Available", "In Repair", "Dispatched", "On Break", "Off Duty"
    val technicianFilter: StateFlow<String> = _technicianFilter.asStateFlow()

    // --- Selected Item for Detail Dialogs ---
    private val _selectedAssetForDetail = MutableStateFlow<AssetEntity?>(null)
    val selectedAssetForDetail: StateFlow<AssetEntity?> = _selectedAssetForDetail.asStateFlow()

    // --- User Feedback Messages ---
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // --- Dashboard Metrics ---
    val dashboardMetrics: StateFlow<DashboardMetrics> = combine(
        assets,
        schedules,
        technicians,
        expenses,
        reminders
    ) { assetList, scheduleList, techList, expenseList, reminderList ->
        val totalAssets = assetList.size
        val activeAssets = assetList.count { it.lifecycleStage == "Active" }
        val degraded = assetList.count { it.lifecycleStage == "Degraded" || it.lifecycleStage == "In Maintenance" }
        val avgHealth = if (assetList.isNotEmpty()) assetList.map { it.healthScore }.average().toInt() else 0
        val openOrders = scheduleList.count { it.status == "Scheduled" || it.status == "In Progress" || it.status == "Overdue" }
        val overdue = scheduleList.count { it.status == "Overdue" }
        val activeTechs = techList.count { it.status == "Available" || it.status == "Dispatched" || it.status == "In Repair" }
        val totalCost = expenseList.sumOf { it.amount }
        val criticalAlerts = reminderList.count { it.priority == "Critical" || it.priority == "Urgent" }

        DashboardMetrics(
            totalAssets = totalAssets,
            activeAssets = activeAssets,
            degradedAssets = degraded,
            avgHealthScore = avgHealth,
            openWorkOrders = openOrders,
            overdueSchedules = overdue,
            activeTechniciansOnDuty = activeTechs,
            totalMaintenanceCost = totalCost,
            criticalAlertsCount = criticalAlerts
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardMetrics())

    // --- Search & Filter Setters ---
    fun setAssetSearchQuery(query: String) {
        _assetSearchQuery.value = query
    }

    fun setSelectedAssetCategory(category: String) {
        _selectedAssetCategory.value = category
    }

    fun setSelectedLifecycleStage(stage: String) {
        _selectedLifecycleStage.value = stage
    }

    fun setScheduleFilter(filter: String) {
        _scheduleFilter.value = filter
    }

    fun setTechnicianFilter(filter: String) {
        _technicianFilter.value = filter
    }

    fun selectAssetForDetail(asset: AssetEntity?) {
        _selectedAssetForDetail.value = asset
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    // --- Asset Actions ---
    fun addAsset(
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
    ) {
        viewModelScope.launch {
            val asset = AssetEntity(
                tagId = tagId.trim().ifEmpty { "AST-${System.currentTimeMillis() % 10000}" },
                name = name.trim(),
                category = category,
                location = location.trim(),
                department = department.trim(),
                serialNumber = serialNumber.trim().ifEmpty { "SN-AUTO-${System.currentTimeMillis() % 10000}" },
                purchaseDate = System.currentTimeMillis(),
                purchaseCost = purchaseCost,
                expectedLifespanMonths = lifespanMonths,
                lifecycleStage = lifecycleStage,
                healthScore = healthScore.coerceIn(0, 100),
                assignedTechName = assignedTech.ifEmpty { "Unassigned" },
                lastMaintenanceDate = System.currentTimeMillis(),
                nextScheduledDate = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
                notes = notes
            )
            repository.insertAsset(asset)
            _snackbarMessage.value = "Asset '${asset.name}' added to registry."
        }
    }

    fun updateAsset(asset: AssetEntity) {
        viewModelScope.launch {
            repository.updateAsset(asset)
            _snackbarMessage.value = "Asset '${asset.name}' updated."
        }
    }

    fun deleteAsset(asset: AssetEntity) {
        viewModelScope.launch {
            repository.deleteAsset(asset.id)
            if (_selectedAssetForDetail.value?.id == asset.id) {
                _selectedAssetForDetail.value = null
            }
            _snackbarMessage.value = "Asset '${asset.name}' removed."
        }
    }

    // --- Maintenance Schedule Actions ---
    fun addSchedule(
        asset: AssetEntity,
        title: String,
        frequency: String,
        priority: String,
        scheduledDate: Long,
        assignedTechName: String,
        estimatedHours: Double,
        checklistText: String,
        notes: String
    ) {
        viewModelScope.launch {
            val schedule = MaintenanceScheduleEntity(
                assetId = asset.id,
                assetName = asset.name,
                assetTag = asset.tagId,
                title = title.trim(),
                frequency = frequency,
                priority = priority,
                status = "Scheduled",
                scheduledDate = scheduledDate,
                assignedTechName = assignedTechName,
                estimatedHours = estimatedHours,
                checklistText = checklistText,
                notes = notes
            )
            repository.insertSchedule(schedule)

            // Update next scheduled date on asset
            repository.updateAsset(asset.copy(nextScheduledDate = scheduledDate))

            // Auto-create reminder
            val reminder = MaintenanceReminderEntity(
                assetId = asset.id,
                title = "Scheduled: $title",
                message = "Maintenance due on ${asset.name} (${asset.tagId}) assigned to $assignedTechName.",
                triggerDate = scheduledDate,
                priority = if (priority == "Critical" || priority == "High") "Urgent" else "Normal",
                targetAudience = assignedTechName
            )
            repository.insertReminder(reminder)

            _snackbarMessage.value = "Schedule created and reminder queued."
        }
    }

    fun updateScheduleStatus(schedule: MaintenanceScheduleEntity, newStatus: String) {
        viewModelScope.launch {
            val updated = schedule.copy(
                status = newStatus,
                completedDate = if (newStatus == "Completed") System.currentTimeMillis() else schedule.completedDate
            )
            repository.updateSchedule(updated)

            if (newStatus == "Completed") {
                // Update asset last maintenance date and bump health score
                val currentAsset = assets.value.find { it.id == schedule.assetId }
                if (currentAsset != null) {
                    val improvedHealth = (currentAsset.healthScore + 10).coerceAtMost(100)
                    repository.updateAsset(
                        currentAsset.copy(
                            lastMaintenanceDate = System.currentTimeMillis(),
                            healthScore = improvedHealth,
                            lifecycleStage = if (currentAsset.lifecycleStage == "In Maintenance") "Active" else currentAsset.lifecycleStage
                        )
                    )
                }
            }
            _snackbarMessage.value = "Maintenance order marked as $newStatus."
        }
    }

    // --- Repair Log Actions ---
    fun logRepair(
        asset: AssetEntity,
        issueTitle: String,
        severity: String,
        rootCause: String,
        actionTaken: String,
        partsReplaced: String,
        downtimeMinutes: Int,
        technicianName: String,
        totalCost: Double,
        status: String
    ) {
        viewModelScope.launch {
            val log = RepairLogEntity(
                assetId = asset.id,
                assetName = asset.name,
                assetTag = asset.tagId,
                reportedDate = System.currentTimeMillis(),
                resolvedDate = if (status == "Resolved") System.currentTimeMillis() else null,
                issueTitle = issueTitle.trim(),
                severity = severity,
                rootCause = rootCause.trim(),
                actionTaken = actionTaken.trim(),
                partsReplaced = partsReplaced.trim(),
                downtimeMinutes = downtimeMinutes,
                technicianName = technicianName,
                totalCost = totalCost,
                status = status
            )
            repository.insertRepairLog(log)

            // Auto-record expense if cost incurred
            if (totalCost > 0) {
                val expense = ExpenseRecordEntity(
                    assetId = asset.id,
                    assetName = asset.name,
                    date = System.currentTimeMillis(),
                    expenseCategory = if (severity == "Critical Breakdown") "Emergency Dispatch" else "Parts & Materials",
                    amount = totalCost,
                    invoiceRef = "REP-${System.currentTimeMillis() % 100000}",
                    description = "Repair: $issueTitle ($partsReplaced)",
                    vendor = "Internal / Field Service"
                )
                repository.insertExpense(expense)
            }

            // Adjust asset lifecycle stage / health score based on repair status
            val newHealth = when {
                status == "Resolved" -> (asset.healthScore + 15).coerceAtMost(100)
                severity == "Critical Breakdown" -> (asset.healthScore - 25).coerceAtLeast(10)
                else -> (asset.healthScore - 10).coerceAtLeast(20)
            }
            val newStage = when {
                status == "Resolved" -> "Active"
                status == "Parts Pending" -> "In Maintenance"
                else -> "In Maintenance"
            }
            repository.updateAsset(
                asset.copy(
                    healthScore = newHealth,
                    lifecycleStage = newStage,
                    lastMaintenanceDate = System.currentTimeMillis()
                )
            )

            _snackbarMessage.value = "Repair log and expense record saved."
        }
    }

    // --- Technician Actions ---
    fun updateTechnicianStatus(technician: TechnicianEntity, newStatus: String, assignment: String) {
        viewModelScope.launch {
            repository.updateTechnicianStatus(technician.id, newStatus, assignment.ifEmpty { technician.currentAssignment })
            _snackbarMessage.value = "${technician.name} status changed to $newStatus."
        }
    }

    fun addTechnician(
        name: String,
        role: String,
        phone: String,
        email: String,
        location: String
    ) {
        viewModelScope.launch {
            val tech = TechnicianEntity(
                name = name.trim(),
                role = role.trim(),
                status = "Available",
                currentAssignment = "Ready for dispatch",
                location = location.trim(),
                phone = phone.trim(),
                email = email.trim(),
                tasksCompleted = 0,
                rating = 5.0,
                avatarColorIndex = (0..4).random()
            )
            repository.insertTechnician(tech)
            _snackbarMessage.value = "Technician ${tech.name} added to roster."
        }
    }

    // --- Expense Actions ---
    fun addExpense(
        asset: AssetEntity,
        category: String,
        amount: Double,
        invoiceRef: String,
        description: String,
        vendor: String
    ) {
        viewModelScope.launch {
            val expense = ExpenseRecordEntity(
                assetId = asset.id,
                assetName = asset.name,
                date = System.currentTimeMillis(),
                expenseCategory = category,
                amount = amount,
                invoiceRef = invoiceRef.trim().ifEmpty { "INV-${System.currentTimeMillis() % 100000}" },
                description = description.trim(),
                vendor = vendor.trim().ifEmpty { "General Supplier" }
            )
            repository.insertExpense(expense)
            _snackbarMessage.value = "Expense record of $$amount saved."
        }
    }

    // --- Reminder Actions ---
    fun sendReminder(
        title: String,
        message: String,
        priority: String,
        targetAudience: String
    ) {
        viewModelScope.launch {
            val reminder = MaintenanceReminderEntity(
                title = title.trim(),
                message = message.trim(),
                triggerDate = System.currentTimeMillis(),
                priority = priority,
                isRead = false,
                isDismissed = false,
                targetAudience = targetAudience
            )
            repository.insertReminder(reminder)
            _snackbarMessage.value = "Reminder broadcasted to $targetAudience."
        }
    }

    fun markReminderRead(reminderId: Long) {
        viewModelScope.launch {
            repository.markReminderRead(reminderId)
        }
    }

    fun dismissReminder(reminderId: Long) {
        viewModelScope.launch {
            repository.dismissReminder(reminderId)
            _snackbarMessage.value = "Reminder dismissed."
        }
    }

    // --- Quick Simulation / Diagnostics ---
    fun simulateTelemetryWarning() {
        viewModelScope.launch {
            val currentAssets = assets.value
            if (currentAssets.isNotEmpty()) {
                val targetAsset = currentAssets.random()
                val updatedHealth = (targetAsset.healthScore - 18).coerceAtLeast(15)
                repository.updateAsset(targetAsset.copy(healthScore = updatedHealth, lifecycleStage = "Degraded"))

                val reminder = MaintenanceReminderEntity(
                    assetId = targetAsset.id,
                    title = "TELEMETRY ALERT: ${targetAsset.name}",
                    message = "Vibration telemetry sensor #VT-99 exceeded safe 12.4 mm/s threshold on ${targetAsset.location}.",
                    triggerDate = System.currentTimeMillis(),
                    priority = "Critical",
                    targetAudience = "Shift Supervisor"
                )
                repository.insertReminder(reminder)
                _snackbarMessage.value = "Simulated sensor alert triggered for ${targetAsset.tagId}!"
            }
        }
    }
}

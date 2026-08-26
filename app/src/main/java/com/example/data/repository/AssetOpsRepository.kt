package com.example.data.repository

import com.example.data.dao.AssetOpsDao
import com.example.data.model.AssetEntity
import com.example.data.model.ExpenseRecordEntity
import com.example.data.model.MaintenanceReminderEntity
import com.example.data.model.MaintenanceScheduleEntity
import com.example.data.model.RepairLogEntity
import com.example.data.model.TechnicianEntity
import kotlinx.coroutines.flow.Flow

class AssetOpsRepository(private val dao: AssetOpsDao) {

    // Assets
    val allAssets: Flow<List<AssetEntity>> = dao.getAllAssets()
    fun getAssetById(id: Long): Flow<AssetEntity?> = dao.getAssetById(id)
    suspend fun insertAsset(asset: AssetEntity): Long = dao.insertAsset(asset)
    suspend fun updateAsset(asset: AssetEntity) = dao.updateAsset(asset)
    suspend fun deleteAsset(id: Long) = dao.deleteAsset(id)

    // Maintenance Schedules
    val allSchedules: Flow<List<MaintenanceScheduleEntity>> = dao.getAllSchedules()
    fun getSchedulesForAsset(assetId: Long): Flow<List<MaintenanceScheduleEntity>> = dao.getSchedulesForAsset(assetId)
    suspend fun insertSchedule(schedule: MaintenanceScheduleEntity): Long = dao.insertSchedule(schedule)
    suspend fun updateSchedule(schedule: MaintenanceScheduleEntity) = dao.updateSchedule(schedule)
    suspend fun deleteSchedule(id: Long) = dao.deleteSchedule(id)

    // Repair Logs
    val allRepairLogs: Flow<List<RepairLogEntity>> = dao.getAllRepairLogs()
    fun getRepairLogsForAsset(assetId: Long): Flow<List<RepairLogEntity>> = dao.getRepairLogsForAsset(assetId)
    suspend fun insertRepairLog(log: RepairLogEntity): Long = dao.insertRepairLog(log)
    suspend fun updateRepairLog(log: RepairLogEntity) = dao.updateRepairLog(log)
    suspend fun deleteRepairLog(id: Long) = dao.deleteRepairLog(id)

    // Technicians
    val allTechnicians: Flow<List<TechnicianEntity>> = dao.getAllTechnicians()
    fun getTechnicianById(id: Long): Flow<TechnicianEntity?> = dao.getTechnicianById(id)
    suspend fun insertTechnician(technician: TechnicianEntity): Long = dao.insertTechnician(technician)
    suspend fun updateTechnician(technician: TechnicianEntity) = dao.updateTechnician(technician)
    suspend fun updateTechnicianStatus(id: Long, status: String, assignment: String) = dao.updateTechnicianStatus(id, status, assignment)
    suspend fun deleteTechnician(id: Long) = dao.deleteTechnician(id)

    // Expenses
    val allExpenses: Flow<List<ExpenseRecordEntity>> = dao.getAllExpenses()
    fun getExpensesForAsset(assetId: Long): Flow<List<ExpenseRecordEntity>> = dao.getExpensesForAsset(assetId)
    suspend fun insertExpense(expense: ExpenseRecordEntity): Long = dao.insertExpense(expense)
    suspend fun deleteExpense(id: Long) = dao.deleteExpense(id)

    // Reminders
    val activeReminders: Flow<List<MaintenanceReminderEntity>> = dao.getActiveReminders()
    suspend fun insertReminder(reminder: MaintenanceReminderEntity): Long = dao.insertReminder(reminder)
    suspend fun markReminderRead(id: Long) = dao.markReminderRead(id)
    suspend fun dismissReminder(id: Long) = dao.dismissReminder(id)
    suspend fun deleteReminder(id: Long) = dao.deleteReminder(id)
}

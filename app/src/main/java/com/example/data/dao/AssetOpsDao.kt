package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AssetEntity
import com.example.data.model.ExpenseRecordEntity
import com.example.data.model.MaintenanceReminderEntity
import com.example.data.model.MaintenanceScheduleEntity
import com.example.data.model.RepairLogEntity
import com.example.data.model.TechnicianEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetOpsDao {

    // --- Assets ---
    @Query("SELECT * FROM assets ORDER BY id DESC")
    fun getAllAssets(): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE id = :id")
    fun getAssetById(id: Long): Flow<AssetEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: AssetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssets(assets: List<AssetEntity>)

    @Update
    suspend fun updateAsset(asset: AssetEntity)

    @Query("DELETE FROM assets WHERE id = :id")
    suspend fun deleteAsset(id: Long)

    @Query("SELECT COUNT(*) FROM assets")
    fun getAssetCount(): Flow<Int>

    // --- Maintenance Schedules ---
    @Query("SELECT * FROM maintenance_schedules ORDER BY scheduledDate ASC")
    fun getAllSchedules(): Flow<List<MaintenanceScheduleEntity>>

    @Query("SELECT * FROM maintenance_schedules WHERE assetId = :assetId ORDER BY scheduledDate ASC")
    fun getSchedulesForAsset(assetId: Long): Flow<List<MaintenanceScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: MaintenanceScheduleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(schedules: List<MaintenanceScheduleEntity>)

    @Update
    suspend fun updateSchedule(schedule: MaintenanceScheduleEntity)

    @Query("DELETE FROM maintenance_schedules WHERE id = :id")
    suspend fun deleteSchedule(id: Long)

    // --- Repair Logs ---
    @Query("SELECT * FROM repair_logs ORDER BY reportedDate DESC")
    fun getAllRepairLogs(): Flow<List<RepairLogEntity>>

    @Query("SELECT * FROM repair_logs WHERE assetId = :assetId ORDER BY reportedDate DESC")
    fun getRepairLogsForAsset(assetId: Long): Flow<List<RepairLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepairLog(log: RepairLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepairLogs(logs: List<RepairLogEntity>)

    @Update
    suspend fun updateRepairLog(log: RepairLogEntity)

    @Query("DELETE FROM repair_logs WHERE id = :id")
    suspend fun deleteRepairLog(id: Long)

    // --- Technicians ---
    @Query("SELECT * FROM technicians ORDER BY name ASC")
    fun getAllTechnicians(): Flow<List<TechnicianEntity>>

    @Query("SELECT * FROM technicians WHERE id = :id")
    fun getTechnicianById(id: Long): Flow<TechnicianEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTechnician(technician: TechnicianEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTechnicians(technicians: List<TechnicianEntity>)

    @Update
    suspend fun updateTechnician(technician: TechnicianEntity)

    @Query("UPDATE technicians SET status = :status, currentAssignment = :assignment WHERE id = :id")
    suspend fun updateTechnicianStatus(id: Long, status: String, assignment: String)

    @Query("DELETE FROM technicians WHERE id = :id")
    suspend fun deleteTechnician(id: Long)

    // --- Expenses ---
    @Query("SELECT * FROM expense_records ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseRecordEntity>>

    @Query("SELECT * FROM expense_records WHERE assetId = :assetId ORDER BY date DESC")
    fun getExpensesForAsset(assetId: Long): Flow<List<ExpenseRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<ExpenseRecordEntity>)

    @Query("DELETE FROM expense_records WHERE id = :id")
    suspend fun deleteExpense(id: Long)

    // --- Reminders ---
    @Query("SELECT * FROM maintenance_reminders WHERE isDismissed = 0 ORDER BY triggerDate DESC")
    fun getActiveReminders(): Flow<List<MaintenanceReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: MaintenanceReminderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminders(reminders: List<MaintenanceReminderEntity>)

    @Query("UPDATE maintenance_reminders SET isRead = 1 WHERE id = :id")
    suspend fun markReminderRead(id: Long)

    @Query("UPDATE maintenance_reminders SET isDismissed = 1 WHERE id = :id")
    suspend fun dismissReminder(id: Long)

    @Query("DELETE FROM maintenance_reminders WHERE id = :id")
    suspend fun deleteReminder(id: Long)
}

package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenance_reminders")
data class MaintenanceReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val scheduleId: Long? = null,
    val assetId: Long? = null,
    val title: String,
    val message: String,
    val triggerDate: Long = System.currentTimeMillis(),
    val priority: String = "Normal", // "Normal", "Urgent", "Critical"
    val isRead: Boolean = false,
    val isDismissed: Boolean = false,
    val targetAudience: String = "All Technicians" // "All Technicians", "Shift Supervisor", "Assigned Lead"
)

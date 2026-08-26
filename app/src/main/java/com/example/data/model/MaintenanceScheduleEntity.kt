package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenance_schedules")
data class MaintenanceScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val assetName: String,
    val assetTag: String,
    val title: String,
    val frequency: String, // "Weekly", "Bi-Weekly", "Monthly", "Quarterly", "Semi-Annual", "Annual"
    val priority: String, // "Low", "Medium", "High", "Critical"
    val status: String, // "Scheduled", "In Progress", "Completed", "Overdue"
    val scheduledDate: Long,
    val assignedTechName: String,
    val estimatedHours: Double,
    val checklistText: String = "", // newline-separated checklist items
    val completedDate: Long? = null,
    val notes: String = ""
)

package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repair_logs")
data class RepairLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val assetName: String,
    val assetTag: String,
    val reportedDate: Long = System.currentTimeMillis(),
    val resolvedDate: Long? = null,
    val issueTitle: String,
    val severity: String, // "Minor", "Moderate", "Critical Breakdown"
    val rootCause: String,
    val actionTaken: String,
    val partsReplaced: String,
    val downtimeMinutes: Int,
    val technicianName: String,
    val totalCost: Double,
    val status: String // "Open", "Under Investigation", "Parts Pending", "Resolved"
)

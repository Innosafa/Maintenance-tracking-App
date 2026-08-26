package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class AssetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tagId: String,
    val name: String,
    val category: String, // "Manufacturing", "HVAC & Climate", "Fleet Vehicles", "Electrical & Power", "Hydraulics & Pumps", "IT & Data Center"
    val location: String, // e.g. "Main Facility - Bay 3", "Server Room Alpha", "Depot West"
    val department: String, // e.g. "Operations", "Logistics", "Facilities"
    val serialNumber: String,
    val purchaseDate: Long, // timestamp
    val purchaseCost: Double,
    val expectedLifespanMonths: Int,
    val lifecycleStage: String, // "Procured", "Active", "In Maintenance", "Degraded", "Decommissioned"
    val healthScore: Int, // 0 - 100
    val assignedTechName: String = "Unassigned",
    val lastMaintenanceDate: Long = System.currentTimeMillis(),
    val nextScheduledDate: Long = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
    val notes: String = ""
)

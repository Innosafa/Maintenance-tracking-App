package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "technicians")
data class TechnicianEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val role: String, // e.g. "Lead Electrical Tech", "Hydraulics Specialist", "HVAC Certified Tech", "Fleet Senior Mechanic"
    val status: String, // "Available", "Dispatched", "In Repair", "On Break", "Off Duty"
    val currentAssignment: String, // e.g. "HVAC Chiller Overhaul - Roof Deck", "Available for dispatch"
    val location: String, // e.g. "Plant 1 - Facility", "Fleet Yard 2", "Warehouse B"
    val phone: String,
    val email: String,
    val tasksCompleted: Int,
    val rating: Double = 4.9,
    val avatarColorIndex: Int = 0
)

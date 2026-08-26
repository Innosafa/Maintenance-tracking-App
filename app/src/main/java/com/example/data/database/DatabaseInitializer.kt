package com.example.data.database

import com.example.data.dao.AssetOpsDao
import com.example.data.model.AssetEntity
import com.example.data.model.ExpenseRecordEntity
import com.example.data.model.MaintenanceReminderEntity
import com.example.data.model.MaintenanceScheduleEntity
import com.example.data.model.RepairLogEntity
import com.example.data.model.TechnicianEntity

object DatabaseInitializer {

    suspend fun populateInitialData(dao: AssetOpsDao) {
        val now = System.currentTimeMillis()
        val oneDay = 24L * 60 * 60 * 1000
        val oneMonth = 30L * oneDay

        // 1. Initial Technicians
        val technicians = listOf(
            TechnicianEntity(
                name = "Marcus Vance",
                role = "Lead Electrical & Automation",
                status = "In Repair",
                currentAssignment = "CNC Spindle Sensor Calibration - Bay 4",
                location = "Plant 1 - Manufacturing Floor",
                phone = "+1 (555) 234-8901",
                email = "m.vance@company.internal",
                tasksCompleted = 42,
                rating = 4.95,
                avatarColorIndex = 0
            ),
            TechnicianEntity(
                name = "Elena Rostova",
                role = "Senior HVAC & Chiller Specialist",
                status = "Dispatched",
                currentAssignment = "Server Room Cooling Loop Inspection",
                location = "HQ Data Center - Roof Tier 2",
                phone = "+1 (555) 456-1120",
                email = "e.rostova@company.internal",
                tasksCompleted = 38,
                rating = 4.9,
                avatarColorIndex = 1
            ),
            TechnicianEntity(
                name = "Devon Cole",
                role = "Hydraulics & Heavy Machinery Lead",
                status = "Available",
                currentAssignment = "Standby for Plant 2 Hydraulic Line Audit",
                location = "Plant 2 - Workshop",
                phone = "+1 (555) 789-3344",
                email = "d.cole@company.internal",
                tasksCompleted = 51,
                rating = 4.85,
                avatarColorIndex = 2
            ),
            TechnicianEntity(
                name = "Sarah Jenkins",
                role = "Fleet & Logistics Senior Mechanic",
                status = "Available",
                currentAssignment = "Ready for Fleet Preventive Maintenance",
                location = "Central Logistics Depot",
                phone = "+1 (555) 912-4455",
                email = "s.jenkins@company.internal",
                tasksCompleted = 29,
                rating = 4.88,
                avatarColorIndex = 3
            ),
            TechnicianEntity(
                name = "Kenji Takahashi",
                role = "Instrumentation & PLC Specialist",
                status = "On Break",
                currentAssignment = "Completed Substation Breaker Test",
                location = "Facility Operations Hub",
                phone = "+1 (555) 670-8899",
                email = "k.takahashi@company.internal",
                tasksCompleted = 34,
                rating = 4.92,
                avatarColorIndex = 4
            )
        )
        dao.insertTechnicians(technicians)

        // 2. Initial Assets
        val assets = listOf(
            AssetEntity(
                tagId = "AST-1042",
                name = "5-Axis CNC Milling Center VMC-800",
                category = "Manufacturing",
                location = "Plant 1 - Machine Bay 4",
                department = "Precision Fabrication",
                serialNumber = "SN-2022-CNC-9981",
                purchaseDate = now - (24 * oneMonth),
                purchaseCost = 145000.0,
                expectedLifespanMonths = 120,
                lifecycleStage = "Active",
                healthScore = 88,
                assignedTechName = "Marcus Vance",
                lastMaintenanceDate = now - (12 * oneDay),
                nextScheduledDate = now + (18 * oneDay),
                notes = "High-precision spindle upgraded with dual cooling jackets in 2024."
            ),
            AssetEntity(
                tagId = "AST-2088",
                name = "Carrier 120-Ton Industrial Rooftop Chiller",
                category = "HVAC & Climate",
                location = "Building A - Roof Tier 2",
                department = "Facilities & Infrastructure",
                serialNumber = "CAR-2020-CH-7742",
                purchaseDate = now - (36 * oneMonth),
                purchaseCost = 68000.0,
                expectedLifespanMonths = 180,
                lifecycleStage = "Active",
                healthScore = 74,
                assignedTechName = "Elena Rostova",
                lastMaintenanceDate = now - (45 * oneDay),
                nextScheduledDate = now + (3 * oneDay),
                notes = "Compressor 2 operates with slightly elevated head pressure during peak loads."
            ),
            AssetEntity(
                tagId = "AST-3105",
                name = "Schuler 500-Ton Hydraulic Stamping Press",
                category = "Hydraulics & Pumps",
                location = "Plant 2 - Heavy Press Row",
                department = "Structural Forming",
                serialNumber = "SCH-500-HP-004",
                purchaseDate = now - (48 * oneMonth),
                purchaseCost = 280000.0,
                expectedLifespanMonths = 240,
                lifecycleStage = "In Maintenance",
                healthScore = 62,
                assignedTechName = "Devon Cole",
                lastMaintenanceDate = now - (2 * oneDay),
                nextScheduledDate = now + (5 * oneDay),
                notes = "Main hydraulic seal package undergoing preventative overhaul."
            ),
            AssetEntity(
                tagId = "AST-4410",
                name = "Freightliner MT45 Delivery Van (Fleet #14)",
                category = "Fleet Vehicles",
                location = "Central Logistics Yard",
                department = "Supply Chain & Distribution",
                serialNumber = "1FVACWCT5LH12948",
                purchaseDate = now - (18 * oneMonth),
                purchaseCost = 52000.0,
                expectedLifespanMonths = 96,
                lifecycleStage = "Active",
                healthScore = 91,
                assignedTechName = "Sarah Jenkins",
                lastMaintenanceDate = now - (15 * oneDay),
                nextScheduledDate = now + (15 * oneDay),
                notes = "Brake pad thickness at 85%; tire rotation scheduled next cycle."
            ),
            AssetEntity(
                tagId = "AST-5520",
                name = "Cummins 750kVA Emergency Backup Generator",
                category = "Electrical & Power",
                location = "Power Substation Annex",
                department = "Site Reliability & Safety",
                serialNumber = "CUM-750-QSK23-G3",
                purchaseDate = now - (60 * oneMonth),
                purchaseCost = 95000.0,
                expectedLifespanMonths = 200,
                lifecycleStage = "Active",
                healthScore = 96,
                assignedTechName = "Kenji Takahashi",
                lastMaintenanceDate = now - (8 * oneDay),
                nextScheduledDate = now + (22 * oneDay),
                notes = "Weekly auto-crank test passed with 0.8s transfer switch response."
            ),
            AssetEntity(
                tagId = "AST-6019",
                name = "APC Symmetra LX 16kVA Modular UPS Unit",
                category = "IT & Data Center",
                location = "HQ Main Server Room 102",
                department = "IT Infrastructure",
                serialNumber = "APC-SYM-16K-9011",
                purchaseDate = now - (30 * oneMonth),
                purchaseCost = 24000.0,
                expectedLifespanMonths = 84,
                lifecycleStage = "Degraded",
                healthScore = 48,
                assignedTechName = "Marcus Vance",
                lastMaintenanceDate = now - (60 * oneDay),
                nextScheduledDate = now + (1 * oneDay),
                notes = "Battery module 3 internal resistance warning; battery replacement required."
            )
        )
        dao.insertAssets(assets)

        // 3. Maintenance Schedules
        val schedules = listOf(
            MaintenanceScheduleEntity(
                assetId = 1,
                assetName = "5-Axis CNC Milling Center VMC-800",
                assetTag = "AST-1042",
                title = "Spindle Taper Runout & Way-Lube Replenishment",
                frequency = "Monthly",
                priority = "High",
                status = "In Progress",
                scheduledDate = now + (1 * oneDay),
                assignedTechName = "Marcus Vance",
                estimatedHours = 3.5,
                checklistText = "Inspect way-lube reservoir levels\nMeasure spindle runout with dial indicator\nClean chip conveyor mesh filters\nCalibrate tool setter zero point\nVerify safety interlock switches",
                notes = "Assigned for morning shift calibration."
            ),
            MaintenanceScheduleEntity(
                assetId = 2,
                assetName = "Carrier 120-Ton Industrial Rooftop Chiller",
                assetTag = "AST-2088",
                title = "Quarterly Condenser Coil Wash & Refrigerant Check",
                frequency = "Quarterly",
                priority = "High",
                status = "Scheduled",
                scheduledDate = now + (3 * oneDay),
                assignedTechName = "Elena Rostova",
                estimatedHours = 5.0,
                checklistText = "Power down chiller safety disconnect\nChemical wash condenser aluminum fin banks\nCheck R-410A suction and discharge pressures\nInspect fan motor bearing thermal signatures\nTest chilled water flow switch contacts",
                notes = "Bring mobile wash rig and safety harness."
            ),
            MaintenanceScheduleEntity(
                assetId = 3,
                assetName = "Schuler 500-Ton Hydraulic Stamping Press",
                assetTag = "AST-3105",
                title = "Hydraulic Fluid Contamination Particle Analysis & Filter Swap",
                frequency = "Bi-Weekly",
                priority = "Critical",
                status = "Scheduled",
                scheduledDate = now + (5 * oneDay),
                assignedTechName = "Devon Cole",
                estimatedHours = 4.0,
                checklistText = "Draw fluid sample from active test port\nReplace 5-micron pressure line filters\nTorque main cylinder flange bolts\nVerify proportional valve response curves",
                notes = "Critical production machinery - lock out during active shift."
            ),
            MaintenanceScheduleEntity(
                assetId = 4,
                assetName = "Freightliner MT45 Delivery Van (Fleet #14)",
                assetTag = "AST-4410",
                title = "5,000-Mile Fleet Safety Inspection & Fluid Service",
                frequency = "Monthly",
                priority = "Medium",
                status = "Scheduled",
                scheduledDate = now + (15 * oneDay),
                assignedTechName = "Sarah Jenkins",
                estimatedHours = 2.0,
                checklistText = "Synthetic oil and oil filter change\nInspect front & rear brake friction material\nTire rotation & tire pressure check (80 PSI)\nInspect steering linkages and ball joints\nCheck DEF fluid level",
                notes = "Log mileage in vehicle maintenance passport."
            ),
            MaintenanceScheduleEntity(
                assetId = 6,
                assetName = "APC Symmetra LX 16kVA Modular UPS Unit",
                assetTag = "AST-6019",
                title = "Emergency UPS Battery Module Pack Replacement",
                frequency = "Semi-Annual",
                priority = "Critical",
                status = "Overdue",
                scheduledDate = now - (2 * oneDay),
                assignedTechName = "Marcus Vance",
                estimatedHours = 2.5,
                checklistText = "Verify maintenance bypass breaker engaged\nHot-swap degraded battery cassette #3\nRun self-calibration diagnostic test\nConfirm runtime battery capacity > 45 mins",
                notes = "Battery modules arrived from vendor yesterday."
            )
        )
        dao.insertSchedules(schedules)

        // 4. Repair Logs
        val repairLogs = listOf(
            RepairLogEntity(
                assetId = 1,
                assetName = "5-Axis CNC Milling Center VMC-800",
                assetTag = "AST-1042",
                reportedDate = now - (14 * oneDay),
                resolvedDate = now - (12 * oneDay),
                issueTitle = "X-Axis Ball Screw Thermal Expansion Error",
                severity = "Moderate",
                rootCause = "Blocked coolant return passage causing localized heat buildup.",
                actionTaken = "Flushed return line with high pressure solvent, purged debris, recalibrated linear encoder glass scales.",
                partsReplaced = "High-flow quick disconnect coupling, O-ring seal kit",
                downtimeMinutes = 180,
                technicianName = "Marcus Vance",
                totalCost = 640.0,
                status = "Resolved"
            ),
            RepairLogEntity(
                assetId = 3,
                assetName = "Schuler 500-Ton Hydraulic Stamping Press",
                assetTag = "AST-3105",
                reportedDate = now - (3 * oneDay),
                resolvedDate = null,
                issueTitle = "Primary Ram Pressure Drop Under Peak 450T Load",
                severity = "Critical Breakdown",
                rootCause = "High-pressure pilot directional valve spool micro-scoring causing internal bypass leakage.",
                actionTaken = "Isolated hydraulic bank, drained servo valve manifold, currently testing replacement proportional cartridge.",
                partsReplaced = "Rexroth 4WRPEH6 pilot valve cartridge (on order)",
                downtimeMinutes = 420,
                technicianName = "Devon Cole",
                totalCost = 3850.0,
                status = "Parts Pending"
            ),
            RepairLogEntity(
                assetId = 4,
                assetName = "Freightliner MT45 Delivery Van (Fleet #14)",
                assetTag = "AST-4410",
                reportedDate = now - (20 * oneDay),
                resolvedDate = now - (19 * oneDay),
                issueTitle = "Alternator Charging Voltage Fluctuation",
                severity = "Minor",
                rootCause = "Internal diode bridge rectifier failure caused intermittent 11.8V charging output.",
                actionTaken = "Replaced 160A heavy-duty alternator, tested charging circuit with 14.4V steady output.",
                partsReplaced = "Delco Remy 28SI 160-Amp Alternator",
                downtimeMinutes = 90,
                technicianName = "Sarah Jenkins",
                totalCost = 420.0,
                status = "Resolved"
            )
        )
        dao.insertRepairLogs(repairLogs)

        // 5. Expense Records
        val expenses = listOf(
            ExpenseRecordEntity(
                assetId = 3,
                assetName = "Schuler 500-Ton Hydraulic Stamping Press",
                date = now - (2 * oneDay),
                expenseCategory = "Parts & Materials",
                amount = 3200.0,
                invoiceRef = "INV-REX-88219",
                description = "Rexroth Proportional Directional Cartridge Valve and seal pack",
                vendor = "Bosch Rexroth Industrial Supplies"
            ),
            ExpenseRecordEntity(
                assetId = 1,
                assetName = "5-Axis CNC Milling Center VMC-800",
                date = now - (12 * oneDay),
                expenseCategory = "Parts & Materials",
                amount = 640.0,
                invoiceRef = "INV-FST-4410",
                description = "Coolant fitting assembly, filter cartridge, high-temp O-ring kit",
                vendor = "Fastenal Industrial"
            ),
            ExpenseRecordEntity(
                assetId = 2,
                assetName = "Carrier 120-Ton Industrial Rooftop Chiller",
                date = now - (45 * oneDay),
                expenseCategory = "Vendor/Contractor",
                amount = 1850.0,
                invoiceRef = "INV-HVAC-9011",
                description = "Annual refrigerant leak electronic sniffer audit and EPA certification compliance",
                vendor = "Trane/Carrier Certified Field Services"
            ),
            ExpenseRecordEntity(
                assetId = 4,
                assetName = "Freightliner MT45 Delivery Van (Fleet #14)",
                date = now - (19 * oneDay),
                expenseCategory = "Parts & Materials",
                amount = 420.0,
                invoiceRef = "INV-FLT-309",
                description = "Heavy duty 160-Amp alternator replacement unit",
                vendor = "Freightliner Commercial Parts"
            ),
            ExpenseRecordEntity(
                assetId = 5,
                assetName = "Cummins 750kVA Emergency Backup Generator",
                date = now - (8 * oneDay),
                expenseCategory = "Lubricants & Consumables",
                amount = 920.0,
                invoiceRef = "INV-OIL-771",
                description = "15W-40 Heavy Diesel engine oil bulk drum & dual primary fuel filters",
                vendor = "Shell Commercial Lubricants"
            ),
            ExpenseRecordEntity(
                assetId = 6,
                assetName = "APC Symmetra LX 16kVA Modular UPS Unit",
                date = now - (1 * oneDay),
                expenseCategory = "Parts & Materials",
                amount = 1480.0,
                invoiceRef = "INV-APC-5532",
                description = "SYBT5 Replacement Battery Module for Symmetra LX",
                vendor = "Schneider Electric Direct"
            )
        )
        dao.insertExpenses(expenses)

        // 6. Maintenance Reminders
        val reminders = listOf(
            MaintenanceReminderEntity(
                scheduleId = 5,
                assetId = 6,
                title = "OVERDUE: UPS Battery Module Replacement",
                message = "APC Symmetra LX UPS battery cassette replacement was due 2 days ago. HQ Server room redundancy at risk.",
                triggerDate = now - (2 * oneDay),
                priority = "Critical",
                isRead = false,
                isDismissed = false,
                targetAudience = "Shift Supervisor"
            ),
            MaintenanceReminderEntity(
                scheduleId = 1,
                assetId = 1,
                title = "Tomorrow: CNC Spindle Runout Inspection",
                message = "Scheduled precision check for 5-Axis Milling Center (AST-1042) at 08:30 AM with Marcus Vance.",
                triggerDate = now + (1 * oneDay),
                priority = "Urgent",
                isRead = false,
                isDismissed = false,
                targetAudience = "Marcus Vance"
            ),
            MaintenanceReminderEntity(
                scheduleId = 2,
                assetId = 2,
                title = "Upcoming: Rooftop Chiller Condenser Wash",
                message = "Building A rooftop chiller quarterly wash due in 3 days. Elena Rostova assigned lead.",
                triggerDate = now + (3 * oneDay),
                priority = "Normal",
                isRead = false,
                isDismissed = false,
                targetAudience = "All Technicians"
            ),
            MaintenanceReminderEntity(
                scheduleId = null,
                assetId = 3,
                title = "Parts Alert: Hydraulic Proportional Valve Arriving",
                message = "Tracking #FX-88912 for Schuler 500T Press valve cartridge estimated delivery by 14:00 today.",
                triggerDate = now,
                priority = "Urgent",
                isRead = false,
                isDismissed = false,
                targetAudience = "Devon Cole"
            )
        )
        dao.insertReminders(reminders)
    }
}

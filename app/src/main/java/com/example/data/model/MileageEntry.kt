package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mileage_entries")
data class MileageEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: Long,
    val vehicleName: String,
    val vehicleType: VehicleType,
    val fuelType: FuelType,
    val registrationNumber: String,
    val kilometersDriven: Double,
    val fuelLiters: Double,
    val totalPrice: Double,
    val calculatedMileage: Double,
    val dateMillis: Long,
    val notes: String = ""
)

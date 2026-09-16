package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: VehicleType,
    val fuelType: FuelType,
    val registrationNumber: String,
    val createdAt: Long = System.currentTimeMillis()
)

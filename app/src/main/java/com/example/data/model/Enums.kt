package com.example.data.model

enum class VehicleType(val displayName: String) {
    BIKE("Bike"),
    CAR("Car");

    companion object {
        fun fromString(value: String): VehicleType {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) }
                ?: CAR
        }
    }
}

enum class FuelType(val displayName: String) {
    PETROL("Petrol"),
    DIESEL("Diesel");

    companion object {
        fun fromString(value: String): FuelType {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) }
                ?: PETROL
        }
    }
}

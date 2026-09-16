package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.FuelType
import com.example.data.model.VehicleType

class Converters {
    @TypeConverter
    fun fromVehicleType(value: VehicleType?): String? = value?.name

    @TypeConverter
    fun toVehicleType(value: String?): VehicleType? = value?.let { VehicleType.fromString(it) }

    @TypeConverter
    fun fromFuelType(value: FuelType?): String? = value?.name

    @TypeConverter
    fun toFuelType(value: String?): FuelType? = value?.let { FuelType.fromString(it) }
}

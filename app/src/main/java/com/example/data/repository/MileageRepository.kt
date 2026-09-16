package com.example.data.repository

import com.example.data.local.MileageDao
import com.example.data.local.VehicleDao
import com.example.data.model.FuelType
import com.example.data.model.MileageEntry
import com.example.data.model.Vehicle
import kotlinx.coroutines.flow.Flow

class MileageRepository(
    private val vehicleDao: VehicleDao,
    private val mileageDao: MileageDao
) {
    val allVehicles: Flow<List<Vehicle>> = vehicleDao.getAllVehicles()
    val allEntries: Flow<List<MileageEntry>> = mileageDao.getAllEntries()

    fun getEntriesForVehicle(vehicleId: Long): Flow<List<MileageEntry>> =
        mileageDao.getEntriesForVehicle(vehicleId)

    val totalKilometers: Flow<Double?> = mileageDao.getTotalKilometers()
    val totalCost: Flow<Double?> = mileageDao.getTotalCost()
    val totalPetrolLiters: Flow<Double?> = mileageDao.getTotalLitersByFuelType(FuelType.PETROL)
    val totalDieselLiters: Flow<Double?> = mileageDao.getTotalLitersByFuelType(FuelType.DIESEL)

    suspend fun insertVehicle(vehicle: Vehicle): Long = vehicleDao.insertVehicle(vehicle)
    suspend fun updateVehicle(vehicle: Vehicle) = vehicleDao.updateVehicle(vehicle)
    suspend fun deleteVehicle(vehicle: Vehicle) = vehicleDao.deleteVehicle(vehicle)
    suspend fun deleteVehicleById(id: Long) = vehicleDao.deleteVehicleById(id)

    suspend fun insertEntry(entry: MileageEntry): Long = mileageDao.insertEntry(entry)
    suspend fun deleteEntry(entry: MileageEntry) = mileageDao.deleteEntry(entry)
    suspend fun deleteEntryById(id: Long) = mileageDao.deleteEntryById(id)
}

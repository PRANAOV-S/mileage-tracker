package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.FuelType
import com.example.data.model.MileageEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MileageDao {
    @Query("SELECT * FROM mileage_entries ORDER BY dateMillis DESC, id DESC")
    fun getAllEntries(): Flow<List<MileageEntry>>

    @Query("SELECT * FROM mileage_entries WHERE vehicleId = :vehicleId ORDER BY dateMillis DESC, id DESC")
    fun getEntriesForVehicle(vehicleId: Long): Flow<List<MileageEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: MileageEntry): Long

    @Delete
    suspend fun deleteEntry(entry: MileageEntry)

    @Query("DELETE FROM mileage_entries WHERE id = :id")
    suspend fun deleteEntryById(id: Long)

    @Query("SELECT SUM(kilometersDriven) FROM mileage_entries")
    fun getTotalKilometers(): Flow<Double?>

    @Query("SELECT SUM(totalPrice) FROM mileage_entries")
    fun getTotalCost(): Flow<Double?>

    @Query("SELECT SUM(fuelLiters) FROM mileage_entries WHERE fuelType = :fuelType")
    fun getTotalLitersByFuelType(fuelType: FuelType): Flow<Double?>
}

package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.FuelType
import com.example.data.model.MileageEntry
import com.example.data.model.Vehicle
import com.example.data.model.VehicleType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Vehicle::class, MileageEntry::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun mileageDao(): MileageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mileage_tracker_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.vehicleDao(), database.mileageDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(vehicleDao: VehicleDao, mileageDao: MileageDao) {
            if (vehicleDao.getVehicleCount() == 0) {
                val yezdi = Vehicle(
                    name = "Yezdi Roadster",
                    type = VehicleType.BIKE,
                    fuelType = FuelType.PETROL,
                    registrationNumber = "MH 12 AB 1973"
                )
                val ford = Vehicle(
                    name = "Ford EcoSport",
                    type = VehicleType.CAR,
                    fuelType = FuelType.DIESEL,
                    registrationNumber = "DL 03 CD 4567"
                )
                val thar = Vehicle(
                    name = "Thar Roxx",
                    type = VehicleType.CAR,
                    fuelType = FuelType.DIESEL,
                    registrationNumber = "KA 05 EF 8901"
                )
                val yezdiId = vehicleDao.insertVehicle(yezdi)
                val fordId = vehicleDao.insertVehicle(ford)
                val tharId = vehicleDao.insertVehicle(thar)

                val now = System.currentTimeMillis()
                val oneDay = 86400000L
                mileageDao.insertEntry(
                    MileageEntry(
                        vehicleId = yezdiId,
                        vehicleName = "Yezdi Roadster",
                        vehicleType = VehicleType.BIKE,
                        fuelType = FuelType.PETROL,
                        registrationNumber = "MH 12 AB 1973",
                        kilometersDriven = 320.0,
                        fuelLiters = 10.5,
                        totalPrice = 1100.0,
                        calculatedMileage = 320.0 / 10.5,
                        dateMillis = now - (3 * oneDay),
                        notes = "City & highway commute"
                    )
                )
                mileageDao.insertEntry(
                    MileageEntry(
                        vehicleId = fordId,
                        vehicleName = "Ford EcoSport",
                        vehicleType = VehicleType.CAR,
                        fuelType = FuelType.DIESEL,
                        registrationNumber = "DL 03 CD 4567",
                        kilometersDriven = 580.0,
                        fuelLiters = 34.0,
                        totalPrice = 3060.0,
                        calculatedMileage = 580.0 / 34.0,
                        dateMillis = now - oneDay,
                        notes = "Intercity trip"
                    )
                )
                mileageDao.insertEntry(
                    MileageEntry(
                        vehicleId = tharId,
                        vehicleName = "Thar Roxx",
                        vehicleType = VehicleType.CAR,
                        fuelType = FuelType.DIESEL,
                        registrationNumber = "KA 05 EF 8901",
                        kilometersDriven = 410.0,
                        fuelLiters = 31.0,
                        totalPrice = 2850.0,
                        calculatedMileage = 410.0 / 31.0,
                        dateMillis = now,
                        notes = "Off-road and highway drive"
                    )
                )
            }
        }
    }
}

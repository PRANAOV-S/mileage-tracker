package com.example

import com.example.data.model.FuelType
import com.example.data.model.VehicleType
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun mileageCalculation_isAccurate() {
    val kilometersDriven = 350.0
    val litersFilled = 10.0
    val calculatedMileage = kilometersDriven / litersFilled
    assertEquals(35.0, calculatedMileage, 0.001)
  }

  @Test
  fun costPerKm_isAccurate() {
    val kilometersDriven = 350.0
    val totalPrice = 1050.0
    val costPerKm = totalPrice / kilometersDriven
    assertEquals(3.0, costPerKm, 0.001)
  }

  @Test
  fun fuelAndVehicleEnums_parseCorrectly() {
    assertEquals(VehicleType.BIKE, VehicleType.fromString("bike"))
    assertEquals(VehicleType.CAR, VehicleType.fromString("Car"))
    assertEquals(FuelType.PETROL, FuelType.fromString("petrol"))
    assertEquals(FuelType.DIESEL, FuelType.fromString("diesel"))
  }
}


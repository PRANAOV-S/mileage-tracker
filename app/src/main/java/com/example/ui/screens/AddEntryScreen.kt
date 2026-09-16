package com.example.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.FuelType
import com.example.data.model.Vehicle
import com.example.data.model.VehicleType
import com.example.ui.MileageViewModel
import com.example.ui.theme.DieselColor
import com.example.ui.theme.DieselContainer
import com.example.ui.theme.PetrolColor
import com.example.ui.theme.PetrolContainer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AddEntryScreen(
    viewModel: MileageViewModel,
    onEntrySaved: () -> Unit,
    onNavigateToVehicles: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vehicles by viewModel.vehicles.collectAsStateWithLifecycle()
    val formState by viewModel.entryFormState.collectAsStateWithLifecycle()

    val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedDate = remember(formState.dateMillis) { dateFormat.format(Date(formState.dateMillis)) }
    val formattedTime = remember(formState.dateMillis) { timeFormat.format(Date(formState.dateMillis)) }

    val previewMileage = viewModel.getPreviewMileage()
    val previewCostPerKm = viewModel.getPreviewCostPerKm()

    LaunchedEffect(formState.isSuccess) {
        if (formState.isSuccess) {
            viewModel.resetEntrySuccessFlag()
            onEntrySaved()
        }
    }

    val datePickerDialog = remember(formState.dateMillis) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = formState.dateMillis
        }
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newCal = Calendar.getInstance().apply {
                    timeInMillis = formState.dateMillis
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                viewModel.updateDateMillis(newCal.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    val timePickerDialog = remember(formState.dateMillis) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = formState.dateMillis
        }
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val newCal = Calendar.getInstance().apply {
                    timeInMillis = formState.dateMillis
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                }
                viewModel.updateDateMillis(newCal.timeInMillis)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("add_entry_screen")
    ) {
        // Title
        Text(
            text = "Log Fuel & Mileage",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Enter distance, fuel filled, and cost to calculate mileage",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Vehicle Preselection Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Preselect Vehicle",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Manage Tab >",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { onNavigateToVehicles() }
                    .padding(4.dp)
                    .testTag("manage_vehicles_link")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (vehicles.isEmpty()) {
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToVehicles() },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "+ Add your first vehicle to start logging",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            // Horizontal list of vehicle selection cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                vehicles.forEach { vehicle ->
                    val isSelected = formState.selectedVehicle?.id == vehicle.id
                    VehicleSelectionChipCard(
                        vehicle = vehicle,
                        isSelected = isSelected,
                        onClick = { viewModel.selectVehicleForEntry(vehicle) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Live Calculation Output Preview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("live_mileage_preview_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CALCULATED MILEAGE OUTPUT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        if (previewMileage != null) {
                            Text(
                                text = String.format(Locale.US, "%.2f km/L", previewMileage),
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        } else {
                            Text(
                                text = "--.- km/L",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            )
                        }
                    }

                    if (formState.selectedVehicle != null) {
                        val isPetrol = formState.selectedVehicle?.fuelType == FuelType.PETROL
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isPetrol) PetrolContainer else DieselContainer
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = formState.selectedVehicle!!.fuelType.displayName.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPetrol) PetrolColor else DieselColor
                                )
                                Text(
                                    text = formState.selectedVehicle!!.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (previewMileage != null) {
                            "Formula: ${formState.kilometersDriven} km ÷ ${formState.fuelLiters} L"
                        } else {
                            "Enter km & liters to calculate mileage automatically"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (previewCostPerKm != null) {
                        Text(
                            text = String.format(Locale.US, "₹%.2f / km", previewCostPerKm),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Input 1: Kilometers Driven
        OutlinedTextField(
            value = formState.kilometersDriven,
            onValueChange = { viewModel.updateKilometersDriven(it) },
            label = { Text("Kilometers Driven (km)") },
            placeholder = { Text("e.g. 350") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = "Kilometers",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            suffix = { Text("km") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_kilometers_driven")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Input 2: Fuel Liters Filled
        OutlinedTextField(
            value = formState.fuelLiters,
            onValueChange = { viewModel.updateFuelLiters(it) },
            label = {
                Text(
                    "Fuel Volume Filled (Litres)" +
                            (formState.selectedVehicle?.fuelType?.let { " - ${it.displayName}" } ?: "")
                )
            },
            placeholder = { Text("e.g. 15.0") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocalGasStation,
                    contentDescription = "Fuel Liters",
                    tint = if (formState.selectedVehicle?.fuelType == FuelType.PETROL) PetrolColor else DieselColor
                )
            },
            suffix = { Text("Litres") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_fuel_liters")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Input 3: Total Price / Cost Paid
        OutlinedTextField(
            value = formState.totalPrice,
            onValueChange = { viewModel.updateTotalPrice(it) },
            label = { Text("Total Fuel Price / Amount Spent") },
            placeholder = { Text("e.g. 1500") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Payments,
                    contentDescription = "Price",
                    tint = Color(0xFF5E35B1)
                )
            },
            prefix = { Text("₹ ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_total_price")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Date & Time of Entry Picker
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Text(
                    text = "Date & Time of Entry",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Date Button
                    Surface(
                        onClick = { datePickerDialog.show() },
                        modifier = Modifier
                            .weight(1.15f)
                            .testTag("date_picker_button"),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Date",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Date",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Text(
                                    text = formattedDate,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Time Button
                    Surface(
                        onClick = { timePickerDialog.show() },
                        modifier = Modifier
                            .weight(0.85f)
                            .testTag("time_picker_button"),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Time",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Time",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Text(
                                    text = formattedTime,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Optional Notes
        OutlinedTextField(
            value = formState.notes,
            onValueChange = { viewModel.updateNotes(it) },
            label = { Text("Notes (Optional)") },
            placeholder = { Text("e.g. Full tank at Shell, city commute") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Notes,
                    contentDescription = "Notes",
                    tint = MaterialTheme.colorScheme.outline
                )
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_notes")
        )

        // Error message banner
        if (formState.errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = formState.errorMessage!!,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Submit Button
        Button(
            onClick = {
                viewModel.saveEntry()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("save_entry_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Calculate & Save Entry",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun VehicleSelectionChipCard(
    vehicle: Vehicle,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isCar = vehicle.type == VehicleType.CAR
    val isPetrol = vehicle.fuelType == FuelType.PETROL

    OutlinedCard(
        onClick = onClick,
        modifier = Modifier
            .testTag("select_vehicle_${vehicle.id}")
            .width(180.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCar) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.secondaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCar) Icons.Default.DirectionsCar else Icons.Default.TwoWheeler,
                        contentDescription = vehicle.type.displayName,
                        tint = if (isCar) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (isPetrol) PetrolContainer else DieselContainer
                ) {
                    Text(
                        text = vehicle.fuelType.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isPetrol) PetrolColor else DieselColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = vehicle.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = vehicle.registrationNumber,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                maxLines = 1
            )
        }
    }
}

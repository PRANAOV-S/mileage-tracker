package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Vehicle
import com.example.ui.MileageViewModel
import com.example.ui.components.EntryCard
import com.example.ui.components.MetricCard
import com.example.ui.theme.DieselColor
import com.example.ui.theme.DieselContainer
import com.example.ui.theme.PetrolColor
import com.example.ui.theme.PetrolContainer
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MileageViewModel,
    onNavigateToAddEntry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vehicles by viewModel.vehicles.collectAsStateWithLifecycle()
    val entries by viewModel.filteredEntries.collectAsStateWithLifecycle()
    val summary by viewModel.summaryMetrics.collectAsStateWithLifecycle()
    val selectedFilterId by viewModel.selectedVehicleFilterId.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("dashboard_list"),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Track your vehicle fuel economy and distance",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Summary Metrics 2x2 Grid + Full width cards
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Row 1: Total Distance & Avg Mileage
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            title = "Total Distance",
                            value = String.format(Locale.US, "%.1f km", summary.totalKilometers),
                            subtitle = "${summary.totalEntriesCount} fill-ups recorded",
                            icon = Icons.Default.Speed,
                            iconTint = Color(0xFF006B5B),
                            iconBackground = Color(0xFFE0F2F1),
                            modifier = Modifier.weight(1f),
                            testTag = "metric_total_km"
                        )

                        MetricCard(
                            title = "Avg Mileage",
                            value = String.format(Locale.US, "%.2f km/L", summary.averageMileage),
                            subtitle = "Overall economy",
                            icon = Icons.Default.DirectionsCar,
                            iconTint = MaterialTheme.colorScheme.primary,
                            iconBackground = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.weight(1f),
                            testTag = "metric_avg_mileage"
                        )
                    }

                    // Row 2: Petrol Filled & Diesel Filled (Specifically requested!)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            title = "Petrol Filled",
                            value = String.format(Locale.US, "%.2f L", summary.totalPetrolLiters),
                            subtitle = "Petrol fuel volume",
                            icon = Icons.Default.LocalGasStation,
                            iconTint = PetrolColor,
                            iconBackground = PetrolContainer,
                            modifier = Modifier.weight(1f),
                            testTag = "metric_petrol_liters"
                        )

                        MetricCard(
                            title = "Diesel Filled",
                            value = String.format(Locale.US, "%.2f L", summary.totalDieselLiters),
                            subtitle = "Diesel fuel volume",
                            icon = Icons.Default.LocalGasStation,
                            iconTint = DieselColor,
                            iconBackground = DieselContainer,
                            modifier = Modifier.weight(1f),
                            testTag = "metric_diesel_liters"
                        )
                    }

                    // Total Fuel Cost Card
                    MetricCard(
                        title = "Total Amount Spent",
                        value = String.format(Locale.US, "₹%.2f", summary.totalCost),
                        subtitle = if (summary.totalKilometers > 0) {
                            String.format(Locale.US, "Average ₹%.2f per kilometer driven", summary.totalCost / summary.totalKilometers)
                        } else "No distance logged yet",
                        icon = Icons.Default.Payments,
                        iconTint = Color(0xFF5E35B1),
                        iconBackground = Color(0xFFEDE7F6),
                        testTag = "metric_total_spent"
                    )
                }
            }

            // Vehicle Filter Bar
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Filter By Vehicle",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // "All Vehicles" chip
                        ElevatedFilterChip(
                            selected = selectedFilterId == null,
                            onClick = { viewModel.setVehicleFilter(null) },
                            label = { Text("All Vehicles (${vehicles.size})") },
                            modifier = Modifier.testTag("filter_all_vehicles"),
                            colors = FilterChipDefaults.elevatedFilterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )

                        // Per-vehicle chips
                        vehicles.forEach { vehicle ->
                            ElevatedFilterChip(
                                selected = selectedFilterId == vehicle.id,
                                onClick = {
                                    if (selectedFilterId == vehicle.id) {
                                        viewModel.setVehicleFilter(null)
                                    } else {
                                        viewModel.setVehicleFilter(vehicle.id)
                                    }
                                },
                                label = { Text(vehicle.name) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (vehicle.type == com.example.data.model.VehicleType.CAR) Icons.Default.DirectionsCar else Icons.Default.TwoWheeler,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                modifier = Modifier.testTag("filter_vehicle_${vehicle.id}"),
                                colors = FilterChipDefaults.elevatedFilterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                }
            }

            // Entries List Header
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Fuel & Mileage Entries (${entries.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (entries.isNotEmpty()) {
                            Text(
                                text = "Swipe or long-press to delete",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }

            // Entries List or Empty State
            if (entries.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalGasStation,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No entries found",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap the + button below to log your first fuel fill-up and calculate mileage.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(entries, key = { it.id }) { entry ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        EntryCard(
                            entry = entry,
                            onDelete = { viewModel.deleteEntry(entry) }
                        )
                    }
                }
            }
        }

        // Floating Action Button
        ExtendedFloatingActionButton(
            onClick = onNavigateToAddEntry,
            icon = { Icon(Icons.Default.Add, contentDescription = "Add Entry") },
            text = { Text("Log Fuel") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("fab_add_entry")
        )
    }
}

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.repository.MileageRepository
import com.example.ui.MileageViewModel
import com.example.ui.MileageViewModelFactory
import com.example.ui.screens.AddEntryScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.VehiclesScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
    val repository = MileageRepository(database.vehicleDao(), database.mileageDao())
    val viewModelFactory = MileageViewModelFactory(repository)

    setContent {
      MyApplicationTheme {
        MileageTrackerApp(viewModelFactory = viewModelFactory)
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MileageTrackerApp(viewModelFactory: MileageViewModelFactory) {
  val viewModel: MileageViewModel = viewModel(factory = viewModelFactory)
  var selectedTab by rememberSaveable { mutableIntStateOf(0) }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Speed,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = when (selectedTab) {
                0 -> "Mileage Tracker"
                1 -> "Log Fuel & Mileage"
                else -> "My Vehicles"
              },
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.titleLarge
            )
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      NavigationBar(
        modifier = Modifier.testTag("bottom_navigation_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
      ) {
        // Tab 0: Dashboard
        NavigationBarItem(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          icon = {
            Icon(
              imageVector = if (selectedTab == 0) Icons.Filled.Speed else Icons.Outlined.Speed,
              contentDescription = "Dashboard"
            )
          },
          label = { Text("Dashboard") },
          modifier = Modifier.testTag("nav_tab_dashboard"),
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
          )
        )

        // Tab 1: Add Entry
        NavigationBarItem(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          icon = {
            Icon(
              imageVector = if (selectedTab == 1) Icons.Filled.LocalGasStation else Icons.Outlined.LocalGasStation,
              contentDescription = "Add Entry"
            )
          },
          label = { Text("Log Fuel") },
          modifier = Modifier.testTag("nav_tab_add_entry"),
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
          )
        )

        // Tab 2: Vehicles
        NavigationBarItem(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          icon = {
            Icon(
              imageVector = if (selectedTab == 2) Icons.Filled.DirectionsCar else Icons.Outlined.DirectionsCar,
              contentDescription = "Vehicles"
            )
          },
          label = { Text("Vehicles") },
          modifier = Modifier.testTag("nav_tab_vehicles"),
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
          )
        )
      }
    }
  ) { innerPadding ->
    Crossfade(
      targetState = selectedTab,
      modifier = Modifier.padding(innerPadding),
      label = "screen_transition"
    ) { tab ->
      when (tab) {
        0 -> DashboardScreen(
          viewModel = viewModel,
          onNavigateToAddEntry = { selectedTab = 1 }
        )
        1 -> AddEntryScreen(
          viewModel = viewModel,
          onEntrySaved = { selectedTab = 0 },
          onNavigateToVehicles = { selectedTab = 2 }
        )
        2 -> VehiclesScreen(
          viewModel = viewModel
        )
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme { Greeting("Android") }
}


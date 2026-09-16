package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.FuelType
import com.example.data.model.MileageEntry
import com.example.data.model.Vehicle
import com.example.data.model.VehicleType
import com.example.data.repository.MileageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class AddEntryFormState(
    val selectedVehicle: Vehicle? = null,
    val kilometersDriven: String = "",
    val fuelLiters: String = "",
    val totalPrice: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val notes: String = "",
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

data class AddVehicleFormState(
    val name: String = "",
    val type: VehicleType = VehicleType.CAR,
    val fuelType: FuelType = FuelType.PETROL,
    val registrationNumber: String = "",
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

data class SummaryMetrics(
    val totalKilometers: Double = 0.0,
    val totalPetrolLiters: Double = 0.0,
    val totalDieselLiters: Double = 0.0,
    val totalCost: Double = 0.0,
    val averageMileage: Double = 0.0,
    val totalEntriesCount: Int = 0
)

class MileageViewModel(
    private val repository: MileageRepository
) : ViewModel() {

    val vehicles: StateFlow<List<Vehicle>> = repository.allVehicles
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allEntries: StateFlow<List<MileageEntry>> = repository.allEntries
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedVehicleFilterId = MutableStateFlow<Long?>(null)
    val selectedVehicleFilterId: StateFlow<Long?> = _selectedVehicleFilterId

    val filteredEntries: StateFlow<List<MileageEntry>> = combine(
        allEntries,
        _selectedVehicleFilterId
    ) { entries, filterId ->
        if (filterId == null) {
            entries
        } else {
            entries.filter { it.vehicleId == filterId }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val summaryMetrics: StateFlow<SummaryMetrics> = combine(
        allEntries,
        _selectedVehicleFilterId
    ) { entries, filterId ->
        val relevantEntries = if (filterId == null) entries else entries.filter { it.vehicleId == filterId }
        val totalKm = relevantEntries.sumOf { it.kilometersDriven }
        val totalPetrol = relevantEntries.filter { it.fuelType == FuelType.PETROL }.sumOf { it.fuelLiters }
        val totalDiesel = relevantEntries.filter { it.fuelType == FuelType.DIESEL }.sumOf { it.fuelLiters }
        val totalCost = relevantEntries.sumOf { it.totalPrice }
        val totalLiters = totalPetrol + totalDiesel
        val avgMileage = if (totalLiters > 0) totalKm / totalLiters else 0.0

        SummaryMetrics(
            totalKilometers = totalKm,
            totalPetrolLiters = totalPetrol,
            totalDieselLiters = totalDiesel,
            totalCost = totalCost,
            averageMileage = avgMileage,
            totalEntriesCount = relevantEntries.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SummaryMetrics()
    )

    // Form state for adding entry
    private val _entryFormState = MutableStateFlow(AddEntryFormState())
    val entryFormState: StateFlow<AddEntryFormState> = _entryFormState

    // Form state for adding vehicle
    private val _vehicleFormState = MutableStateFlow(AddVehicleFormState())
    val vehicleFormState: StateFlow<AddVehicleFormState> = _vehicleFormState

    init {
        // Auto-select first vehicle for entry form once vehicles load
        viewModelScope.launch {
            vehicles.collect { list ->
                if (_entryFormState.value.selectedVehicle == null && list.isNotEmpty()) {
                    _entryFormState.value = _entryFormState.value.copy(selectedVehicle = list.first())
                }
            }
        }
    }

    fun setVehicleFilter(vehicleId: Long?) {
        _selectedVehicleFilterId.value = vehicleId
    }

    fun selectVehicleForEntry(vehicle: Vehicle) {
        _entryFormState.value = _entryFormState.value.copy(
            selectedVehicle = vehicle,
            errorMessage = null
        )
    }

    fun updateKilometersDriven(value: String) {
        _entryFormState.value = _entryFormState.value.copy(
            kilometersDriven = value,
            errorMessage = null
        )
    }

    fun updateFuelLiters(value: String) {
        _entryFormState.value = _entryFormState.value.copy(
            fuelLiters = value,
            errorMessage = null
        )
    }

    fun updateTotalPrice(value: String) {
        _entryFormState.value = _entryFormState.value.copy(
            totalPrice = value,
            errorMessage = null
        )
    }

    fun updateDateMillis(value: Long) {
        _entryFormState.value = _entryFormState.value.copy(
            dateMillis = value
        )
    }

    fun updateNotes(value: String) {
        _entryFormState.value = _entryFormState.value.copy(
            notes = value
        )
    }

    fun getPreviewMileage(): Double? {
        val km = _entryFormState.value.kilometersDriven.toDoubleOrNull() ?: return null
        val liters = _entryFormState.value.fuelLiters.toDoubleOrNull() ?: return null
        if (km <= 0 || liters <= 0) return null
        return km / liters
    }

    fun getPreviewCostPerKm(): Double? {
        val km = _entryFormState.value.kilometersDriven.toDoubleOrNull() ?: return null
        val price = _entryFormState.value.totalPrice.toDoubleOrNull() ?: return null
        if (km <= 0 || price <= 0) return null
        return price / km
    }

    fun saveEntry(): Boolean {
        val state = _entryFormState.value
        val vehicle = state.selectedVehicle
        if (vehicle == null) {
            _entryFormState.value = state.copy(errorMessage = "Please select a vehicle first.")
            return false
        }

        val km = state.kilometersDriven.toDoubleOrNull()
        if (km == null || km <= 0) {
            _entryFormState.value = state.copy(errorMessage = "Please enter valid kilometers driven (> 0).")
            return false
        }

        val liters = state.fuelLiters.toDoubleOrNull()
        if (liters == null || liters <= 0) {
            _entryFormState.value = state.copy(errorMessage = "Please enter valid liters of fuel (> 0).")
            return false
        }

        val price = state.totalPrice.toDoubleOrNull()
        if (price == null || price < 0) {
            _entryFormState.value = state.copy(errorMessage = "Please enter valid total price/cost.")
            return false
        }

        val calculatedMileage = km / liters

        val newEntry = MileageEntry(
            vehicleId = vehicle.id,
            vehicleName = vehicle.name,
            vehicleType = vehicle.type,
            fuelType = vehicle.fuelType,
            registrationNumber = vehicle.registrationNumber,
            kilometersDriven = km,
            fuelLiters = liters,
            totalPrice = price,
            calculatedMileage = calculatedMileage,
            dateMillis = state.dateMillis,
            notes = state.notes.trim()
        )

        viewModelScope.launch {
            repository.insertEntry(newEntry)
            // Reset input fields but keep current vehicle selected
            _entryFormState.value = AddEntryFormState(
                selectedVehicle = vehicle,
                dateMillis = System.currentTimeMillis(),
                isSuccess = true
            )
        }
        return true
    }

    fun resetEntrySuccessFlag() {
        _entryFormState.value = _entryFormState.value.copy(isSuccess = false)
    }

    fun deleteEntry(entry: MileageEntry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }

    // Vehicle management methods
    fun updateVehicleName(name: String) {
        _vehicleFormState.value = _vehicleFormState.value.copy(name = name, errorMessage = null)
    }

    fun updateVehicleType(type: VehicleType) {
        _vehicleFormState.value = _vehicleFormState.value.copy(type = type)
    }

    fun updateVehicleFuelType(fuelType: FuelType) {
        _vehicleFormState.value = _vehicleFormState.value.copy(fuelType = fuelType)
    }

    fun updateVehicleRegistration(reg: String) {
        _vehicleFormState.value = _vehicleFormState.value.copy(registrationNumber = reg, errorMessage = null)
    }

    fun saveVehicle(): Boolean {
        val state = _vehicleFormState.value
        val name = state.name.trim()
        if (name.isBlank()) {
            _vehicleFormState.value = state.copy(errorMessage = "Vehicle name cannot be empty.")
            return false
        }

        val reg = state.registrationNumber.trim()
        if (reg.isBlank()) {
            _vehicleFormState.value = state.copy(errorMessage = "Registration/vehicle number is required.")
            return false
        }

        val newVehicle = Vehicle(
            name = name,
            type = state.type,
            fuelType = state.fuelType,
            registrationNumber = reg
        )

        viewModelScope.launch {
            val id = repository.insertVehicle(newVehicle)
            _vehicleFormState.value = AddVehicleFormState(isSuccess = true)
            // Auto-select this vehicle if none selected
            if (_entryFormState.value.selectedVehicle == null) {
                _entryFormState.value = _entryFormState.value.copy(
                    selectedVehicle = newVehicle.copy(id = id)
                )
            }
        }
        return true
    }

    fun resetVehicleSuccessFlag() {
        _vehicleFormState.value = _vehicleFormState.value.copy(isSuccess = false)
    }

    fun deleteVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            repository.deleteVehicle(vehicle)
            if (_entryFormState.value.selectedVehicle?.id == vehicle.id) {
                val remaining = vehicles.value.filter { it.id != vehicle.id }
                _entryFormState.value = _entryFormState.value.copy(
                    selectedVehicle = remaining.firstOrNull()
                )
            }
            if (_selectedVehicleFilterId.value == vehicle.id) {
                _selectedVehicleFilterId.value = null
            }
        }
    }
}

class MileageViewModelFactory(
    private val repository: MileageRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MileageViewModel::class.java)) {
            return MileageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

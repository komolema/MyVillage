package ui.screens.animal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import models.domain.Animal
import models.domain.Resident
import org.koin.compose.koinInject
import theme.PurpleButtonColor
import ui.components.navigation.ScreenWithAppBar
import viewmodel.AnimalViewModel
import java.time.LocalDate
import java.util.*

@Composable
fun AnimalScreen(navController: NavController) {
    val viewModel: AnimalViewModel = koinInject()
    val state by viewModel.state.collectAsState()

    // Load animals when the screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.processIntent(AnimalViewModel.Intent.LoadAnimals(0))
    }

    ScreenWithAppBar("Animal Management", { navController.navigate("dashboard") }, PurpleButtonColor) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PurpleButtonColor)
                }
            } else if (state.animals.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No animals found")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.animals) { animalExpanded ->
                        val animal = animalExpanded.animal
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Species: ${animal.species}", style = MaterialTheme.typography.headlineMedium)
                                    Row {
                                        IconButton(onClick = {
                                            viewModel.processIntent(AnimalViewModel.Intent.OpenEditDialog(animal))
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                                        }
                                        IconButton(onClick = {
                                            viewModel.processIntent(AnimalViewModel.Intent.ShowDeleteConfirmation(animal.id))
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                                        }
                                    }
                                }
                                Text("Breed: ${animal.breed}")
                                Text("Gender: ${animal.gender}")
                                Text("Tag Number/**/: ${animal.tagNumber}")
                                Text("Health Status: ${animal.healthStatus}")
                                Text("Vaccination Status: ${if (animal.vaccinationStatus) "Vaccinated" else "Not Vaccinated"}")
                                animal.vaccinationDate?.let {
                                    Text("Vaccination Date: $it")
                                }

                                animalExpanded.ownership.fold(
                                    { Text("No ownership information") },
                                    { ownership ->
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Owner ID: ${ownership.residentId}")
                                        // Find resident name if available
                                        val resident = state.residents.find { it.id == ownership.residentId }
                                        if (resident != null) {
                                            Text("Owner: ${resident.firstName} ${resident.lastName}")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // FAB for adding new animal
            FloatingActionButton(
                onClick = { viewModel.processIntent(AnimalViewModel.Intent.OpenCreateDialog()) },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                containerColor = PurpleButtonColor
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Animal")
            }
        }

        // Animal Dialog
        if (state.isDialogOpen) {
            AnimalDialog(
                animal = state.currentAnimal,
                isEditMode = state.isEditMode,
                residents = state.residents,
                selectedResidentId = state.selectedResidentId,
                onResidentSelected = { viewModel.processIntent(AnimalViewModel.Intent.SelectResident(it)) },
                onSave = { animal ->
                    if (state.isEditMode) {
                        viewModel.processIntent(AnimalViewModel.Intent.SaveAnimalChanges(animal))
                    } else {
                        viewModel.processIntent(AnimalViewModel.Intent.CreateAnimal(animal))
                    }
                },
                onCancel = { viewModel.processIntent(AnimalViewModel.Intent.CloseDialog()) }
            )
        }

        // Delete Confirmation Dialog
        if (state.showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { viewModel.processIntent(AnimalViewModel.Intent.HideDeleteConfirmation()) },
                title = { Text("Confirm Deletion") },
                text = { Text("Are you sure you want to delete this animal?") },
                confirmButton = {
                    Button(
                        onClick = { viewModel.processIntent(AnimalViewModel.Intent.ConfirmDeleteAnimal()) },
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleButtonColor)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { viewModel.processIntent(AnimalViewModel.Intent.HideDeleteConfirmation()) }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun AnimalDialog(
    animal: Animal,
    isEditMode: Boolean,
    residents: List<Resident>,
    selectedResidentId: UUID?,
    onResidentSelected: (UUID) -> Unit,
    onSave: (Animal) -> Unit,
    onCancel: () -> Unit
) {
    var species by remember { mutableStateOf(animal.species) }
    var breed by remember { mutableStateOf(animal.breed) }
    var gender by remember { mutableStateOf(animal.gender) }
    var tagNumber by remember { mutableStateOf(animal.tagNumber) }
    var healthStatus by remember { mutableStateOf(animal.healthStatus) }
    var vaccinationStatus by remember { mutableStateOf(animal.vaccinationStatus) }
    var dobYear by remember { mutableStateOf(animal.dob.year.toString()) }
    var dobMonth by remember { mutableStateOf(animal.dob.monthValue.toString()) }
    var dobDay by remember { mutableStateOf(animal.dob.dayOfMonth.toString()) }

    Dialog(onDismissRequest = onCancel) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (isEditMode) "Edit Animal" else "Add New Animal",
                    style = MaterialTheme.typography.headlineMedium
                )

                OutlinedTextField(
                    value = species,
                    onValueChange = { species = it },
                    label = { Text("Species") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = breed,
                    onValueChange = { breed = it },
                    label = { Text("Breed") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = gender,
                    onValueChange = { gender = it },
                    label = { Text("Gender") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Date of Birth fields
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = dobYear,
                        onValueChange = { dobYear = it },
                        label = { Text("Year") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = dobMonth,
                        onValueChange = { dobMonth = it },
                        label = { Text("Month") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = dobDay,
                        onValueChange = { dobDay = it },
                        label = { Text("Day") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = tagNumber,
                    onValueChange = { tagNumber = it },
                    label = { Text("Tag Number") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = healthStatus,
                    onValueChange = { healthStatus = it },
                    label = { Text("Health Status") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = vaccinationStatus,
                        onCheckedChange = { vaccinationStatus = it }
                    )
                    Text("Vaccinated")
                }

                // Resident selection dropdown
                Text("Select Owner", style = MaterialTheme.typography.titleMedium)
                if (residents.isEmpty()) {
                    Text("No residents available")
                } else {
                    var expanded by remember { mutableStateOf(false) }
                    val selectedResident = residents.find { it.id == selectedResidentId }
                    val displayText = selectedResident?.let { "${it.firstName} ${it.lastName}" } ?: "Select a resident"

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(displayText)
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            residents.forEach { resident ->
                                DropdownMenuItem(
                                    text = { Text("${resident.firstName} ${resident.lastName}") },
                                    onClick = {
                                        onResidentSelected(resident.id)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onCancel) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            try {
                                val year = dobYear.toInt()
                                val month = dobMonth.toInt()
                                val day = dobDay.toInt()
                                val dob = LocalDate.of(year, month, day)

                                val updatedAnimal = animal.copy(
                                    species = species,
                                    breed = breed,
                                    gender = gender,
                                    dob = dob,
                                    tagNumber = tagNumber,
                                    healthStatus = healthStatus,
                                    vaccinationStatus = vaccinationStatus
                                )
                                onSave(updatedAnimal)
                            } catch (e: Exception) {
                                // Handle date parsing errors
                                // In a real app, you'd show an error message
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleButtonColor)
                    ) {
                        Text(if (isEditMode) "Update" else "Create")
                    }
                }
            }
        }
    }
}

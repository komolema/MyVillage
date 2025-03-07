package ui.screens.resident.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.seanproctor.datatable.TableColumnWidth
import models.Residence
import models.Address
import ui.components.table.GenericTable
import ui.components.table.TableCellType
import ui.components.table.TableColumn
import ui.components.table.TableConfig
import ui.screens.resident.WindowMode
import viewmodel.ResidentWindowViewModel
import viewmodel.ResidentWindowViewModel.Intent
import java.util.*
import ui.screens.resident.tabs.TabCompletionState

@Composable
fun ResidenceTab(
    residentId: UUID?,
    viewModel: ResidentWindowViewModel,
    mode: WindowMode,
    onTabStateChange: (TabCompletionState) -> Unit = {}
) {
    var showAddForm by remember { mutableStateOf(false) }
    var selectedResidence by remember { mutableStateOf<Residence?>(null) }
    var selectedAddress by remember { mutableStateOf<Address?>(null) }

    // Load residence when residentId changes
    LaunchedEffect(residentId) {
        if (residentId != null) {
            viewModel.processIntent(Intent.LoadResidence(residentId))
        }
    }

    // Collect state updates
    val state by viewModel.state.collectAsState()
    val residence = state.residence
    val address = state.address

    // Update tab state based on data
    LaunchedEffect(residence) {
        onTabStateChange(
            if (residence == Residence.default) TabCompletionState.TODO
            else TabCompletionState.DONE
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Residence Information",
                style = MaterialTheme.typography.headlineSmall
            )
            Button(
                onClick = { showAddForm = true },
                enabled = mode == WindowMode.UPDATE && residence == Residence.default
            ) {
                Text("Add Residence")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Residence and Address display
        if (residence != Residence.default && address != Address.default) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Current Address",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = address.formatFriendly())
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Occupation Date: ${residence.occupationDate}")
                    
                    if (mode == WindowMode.UPDATE) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    selectedResidence = residence
                                    selectedAddress = address
                                    showAddForm = true
                                }
                            ) {
                                Text("Edit")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    viewModel.processIntent(
                                        Intent.DeleteResidence(residence.id, address.id)
                                    )
                                }
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddForm) {
        ResidenceFormDialog(
            residence = selectedResidence,
            address = selectedAddress,
            onDismiss = {
                showAddForm = false
                selectedResidence = null
                selectedAddress = null
            },
            onSave = { newResidence, newAddress ->
                if (selectedResidence != null) {
                    viewModel.processIntent(
                        Intent.UpdateResidence(newResidence, newAddress)
                    )
                } else {
                    viewModel.processIntent(
                        Intent.CreateResidence(newResidence, newAddress)
                    )
                }
                showAddForm = false
                selectedResidence = null
                selectedAddress = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidenceFormDialog(
    residence: Residence?,
    address: Address?,
    onDismiss: () -> Unit,
    onSave: (Residence, Address) -> Unit
) {
    var line by remember { mutableStateOf(address?.line ?: "") }
    var houseNumber by remember { mutableStateOf(address?.houseNumber ?: "") }
    var suburb by remember { mutableStateOf(address?.suburb ?: "") }
    var town by remember { mutableStateOf(address?.town ?: "") }
    var postalCode by remember { mutableStateOf(address?.postalCode ?: "") }
    var occupationDate by remember { mutableStateOf(residence?.occupationDate ?: java.time.LocalDate.now()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (residence == null) "Add Residence" else "Edit Residence") },
        text = {
            Column {
                OutlinedTextField(
                    value = line,
                    onValueChange = { line = it },
                    label = { Text("Street") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = houseNumber,
                    onValueChange = { houseNumber = it },
                    label = { Text("House Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = suburb,
                    onValueChange = { suburb = it },
                    label = { Text("Suburb") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = town,
                    onValueChange = { town = it },
                    label = { Text("Town") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = postalCode,
                    onValueChange = { postalCode = it },
                    label = { Text("Postal Code") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newAddress = (address ?: Address.default).copy(
                        line = line,
                        houseNumber = houseNumber,
                        suburb = suburb,
                        town = town,
                        postalCode = postalCode
                    )
                    val newResidence = (residence ?: Residence.default).copy(
                        occupationDate = occupationDate
                    )
                    onSave(newResidence, newAddress)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
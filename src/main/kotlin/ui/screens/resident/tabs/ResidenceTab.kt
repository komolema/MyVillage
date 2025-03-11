package ui.screens.resident.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import models.Residence
import models.Address
import ui.components.DatePicker
import ui.screens.resident.WindowMode
import viewmodel.ResidentWindowViewModel
import viewmodel.ResidentWindowViewModel.Intent
import java.util.*
import ui.screens.resident.tabs.TabCompletionState
import java.time.LocalDate
import localization.LocaleManager
import localization.StringResourcesManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidenceTab(
    residentId: UUID?,
    viewModel: ResidentWindowViewModel,
    mode: WindowMode,
    onTabStateChange: (TabCompletionState) -> Unit = {}
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var currentResidence by remember { mutableStateOf<Residence?>(null) }
    var currentAddress by remember { mutableStateOf<Address?>(null) }
    val strings = remember { mutableStateOf(StringResourcesManager.getCurrentStringResources()) }

    // Update strings when locale changes
    LaunchedEffect(LocaleManager.getCurrentLocale()) {
        strings.value = StringResourcesManager.getCurrentStringResources()
    }

    LaunchedEffect(residentId) {
        if (residentId != null) {
            viewModel.processIntent(Intent.LoadResidence(residentId))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.state.collect { state ->
            currentResidence = state.residence
            currentAddress = state.address
            onTabStateChange(when {
                currentResidence != null && currentAddress != null -> TabCompletionState.DONE
                else -> TabCompletionState.TODO
            })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (currentResidence != null && currentAddress != null) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = strings.value.residenceInformation,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Address fields
                    OutlinedTextField(
                        value = currentAddress!!.line,
                        onValueChange = {},
                        label = { Text(strings.value.street) },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )

                    OutlinedTextField(
                        value = currentAddress!!.houseNumber,
                        onValueChange = {},
                        label = { Text(strings.value.houseNumber) },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )

                    OutlinedTextField(
                        value = currentAddress!!.suburb,
                        onValueChange = {},
                        label = { Text(strings.value.suburb) },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )

                    OutlinedTextField(
                        value = currentAddress!!.town,
                        onValueChange = {},
                        label = { Text(strings.value.town) },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )

                    OutlinedTextField(
                        value = currentAddress!!.postalCode,
                        onValueChange = {},
                        label = { Text(strings.value.postalCode) },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )

                    // Optional fields
                    if (currentAddress!!.geoCoordinates != null) {
                        OutlinedTextField(
                            value = currentAddress!!.geoCoordinates!!,
                            onValueChange = {},
                            label = { Text(strings.value.geoCoordinates) },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        )
                    }

                    if (currentAddress!!.landmark != null) {
                        OutlinedTextField(
                            value = currentAddress!!.landmark!!,
                            onValueChange = {},
                            label = { Text(strings.value.landmark) },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        )
                    }

                    // Occupation Date
                    OutlinedTextField(
                        value = currentResidence!!.occupationDate.toString(),
                        onValueChange = {},
                        label = { Text(strings.value.occupationDate) },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )

                    // Action buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row {
                            Button(
                                onClick = { showEditDialog = true },
                                enabled = mode == WindowMode.UPDATE
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = strings.value.edit)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(strings.value.edit)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Proof of Address button
                            var showProofOfAddressDialog by remember { mutableStateOf(false) }
                            Button(
                                onClick = { showProofOfAddressDialog = true }
                            ) {
                                Icon(Icons.Default.Info, contentDescription = "Proof of Address")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Proof of Address")
                            }

                            if (showProofOfAddressDialog) {
                                val resident = viewModel.state.value.resident
                                val address = currentAddress
                                val residence = currentResidence
                                if (resident != null && address != null && residence != null) {
                                    ui.screens.resident.ProofOfAddressDialog(
                                        resident = resident,
                                        address = address,
                                        residence = residence,
                                        onDismiss = { showProofOfAddressDialog = false }
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.processIntent(Intent.DeleteResidence(currentResidence!!.id, currentAddress!!.id))
                            },
                            enabled = mode == WindowMode.UPDATE,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = strings.value.delete)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.value.delete)
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = strings.value.noResidenceInfo,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )

                if (mode == WindowMode.UPDATE) {
                    Button(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Residence")
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        ResidenceFormDialog(
            residence = currentResidence,
            address = currentAddress,
            onDismiss = { showEditDialog = false },
            onSave = { residence, address ->
                if (currentResidence == null) {
                    val newResidence = residence.copy(residentId = residentId ?: return@ResidenceFormDialog)
                    viewModel.processIntent(Intent.CreateResidence(newResidence, address))
                } else {
                    viewModel.processIntent(Intent.UpdateResidence(residence, address))
                }
                showEditDialog = false
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
    val strings = remember { mutableStateOf(StringResourcesManager.getCurrentStringResources()) }

    // Update strings when locale changes
    LaunchedEffect(LocaleManager.getCurrentLocale()) {
        strings.value = StringResourcesManager.getCurrentStringResources()
    }
    var line by remember { mutableStateOf(address?.line ?: "") }
    var houseNumber by remember { mutableStateOf(address?.houseNumber ?: "") }
    var suburb by remember { mutableStateOf(address?.suburb ?: "") }
    var town by remember { mutableStateOf(address?.town ?: "") }
    var postalCode by remember { mutableStateOf(address?.postalCode ?: "") }
    var geoCoordinates by remember { mutableStateOf(address?.geoCoordinates ?: "") }
    var landmark by remember { mutableStateOf(address?.landmark ?: "") }
    var occupationDate by remember { mutableStateOf(residence?.occupationDate ?: LocalDate.now()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.value.editResidence) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = line,
                    onValueChange = { line = it },
                    label = { Text(strings.value.street) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = houseNumber,
                    onValueChange = { houseNumber = it },
                    label = { Text(strings.value.houseNumber) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = suburb,
                    onValueChange = { suburb = it },
                    label = { Text(strings.value.suburb) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = town,
                    onValueChange = { town = it },
                    label = { Text(strings.value.town) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = postalCode,
                    onValueChange = { postalCode = it },
                    label = { Text(strings.value.postalCode) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = geoCoordinates,
                    onValueChange = { geoCoordinates = it },
                    label = { Text("${strings.value.geoCoordinates} (${strings.value.optional})") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = landmark,
                    onValueChange = { landmark = it },
                    label = { Text("${strings.value.landmark} (${strings.value.optional})") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )

                DatePicker(
                    date = occupationDate,
                    onDateSelected = { occupationDate = it },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedAddress = address?.copy(
                        line = line,
                        houseNumber = houseNumber,
                        suburb = suburb,
                        town = town,
                        postalCode = postalCode,
                        geoCoordinates = geoCoordinates.takeIf { it.isNotEmpty() },
                        landmark = landmark.takeIf { it.isNotEmpty() }
                    ) ?: Address(
                        id = UUID.randomUUID(),
                        line = line,
                        houseNumber = houseNumber,
                        suburb = suburb,
                        town = town,
                        postalCode = postalCode,
                        geoCoordinates = geoCoordinates.takeIf { it.isNotEmpty() },
                        landmark = landmark.takeIf { it.isNotEmpty() }
                    )

                    val updatedResidence = residence?.copy(
                        occupationDate = occupationDate
                    ) ?: Residence(
                        id = UUID.randomUUID(),
                        residentId = UUID.randomUUID(), // This should be set properly
                        addressId = updatedAddress.id,
                        occupationDate = occupationDate
                    )

                    onSave(updatedResidence, updatedAddress)
                }
            ) {
                Text(strings.value.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.value.cancel)
            }
        }
    )
}

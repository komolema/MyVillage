import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusOrder
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import epicarchitect.calendar.compose.basis.config.rememberBasisEpicCalendarConfig
import epicarchitect.calendar.compose.datepicker.EpicDatePicker
import epicarchitect.calendar.compose.datepicker.config.rememberEpicDatePickerConfig
import epicarchitect.calendar.compose.datepicker.state.EpicDatePickerState
import epicarchitect.calendar.compose.datepicker.state.rememberEpicDatePickerState
import epicarchitect.calendar.compose.pager.config.rememberEpicCalendarPagerConfig
import kotlinx.datetime.toJavaLocalDate
import ui.screens.resident.WindowMode
import viewmodel.ResidentWindowViewModel
import java.util.*
import models.Resident

@Composable
fun ResidentTab(residentId: UUID?, viewModel: ResidentWindowViewModel, mode: WindowMode) {
    var residentState by remember { mutableStateOf(Resident.default) }
    var showDatePicker by remember { mutableStateOf(false) }
    val viewModelState by viewModel.state.collectAsStateWithLifecycle()

    // Load resident data once if in edit/view mode
    LaunchedEffect(residentId) {
        if (mode != WindowMode.NEW && residentId != null) {
            viewModel.processIntent(ResidentWindowViewModel.Intent.LoadResident(residentId))
        }
    }

    // Sync UI state with ViewModel state
    LaunchedEffect(viewModelState.resident) {
        if (mode != WindowMode.NEW) {
            residentState = viewModelState.resident
        }
    }

    // Create FocusRequesters for each TextField
    val focusRequesters = List(7) { FocusRequester() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text("First Name:", modifier = Modifier.weight(1f))
            TextField(
                value = residentState.firstName,
                enabled = mode != WindowMode.VIEW,
                onValueChange = { residentState = residentState.copy(firstName = it) },
                modifier = Modifier.weight(2f).focusOrder(focusRequesters[0]) { next = focusRequesters[1] },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusRequesters[1].requestFocus() })
            )
        }

        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text("Last Name:", modifier = Modifier.weight(1f))
            TextField(
                value = residentState.lastName,
                enabled = mode != WindowMode.VIEW,
                onValueChange = { residentState = residentState.copy(lastName = it) },
                modifier = Modifier.weight(2f).focusOrder(focusRequesters[1]) { next = focusRequesters[2] },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusRequesters[2].requestFocus() })
            )
        }

        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text("Date of Birth:", modifier = Modifier.weight(1f))
            TextField(
                value = residentState.dob.toString(),
                enabled = mode != WindowMode.VIEW,
                onValueChange = {},
                modifier = Modifier.weight(2f).focusOrder(focusRequesters[2]) { next = focusRequesters[3] }
                    .clickable { showDatePicker = true },
                readOnly = true
            )
        }

        if (showDatePicker) {
            Dialog(onDismissRequest = { showDatePicker = false }) {
                EpicDatePicker(
                    state = rememberEpicDatePickerState(
                        selectionMode = EpicDatePickerState.SelectionMode.Single(),
                        config = rememberEpicDatePickerConfig(
                            pagerConfig = rememberEpicCalendarPagerConfig(
                                basisConfig = rememberBasisEpicCalendarConfig(
                                    displayDaysOfAdjacentMonths = false
                                )
                            ),
                            selectionContentColor = MaterialTheme.colors.onPrimary,
                            selectionContainerColor = MaterialTheme.colors.primary,
                        )
                    )
                ) { date ->
                    residentState = residentState.copy(dob = date.toJavaLocalDate())
                    showDatePicker = false
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text("Gender:", modifier = Modifier.weight(1f))
            var expanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.weight(2f)) {
                TextField(
                    value = residentState.gender,
                    enabled = mode != WindowMode.VIEW,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth().clickable { expanded = true },
                    readOnly = true
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(onClick = {
                        residentState = residentState.copy(gender = "Male")
                        expanded = false
                    }) {
                        Text("Male")
                    }
                    DropdownMenuItem(onClick = {
                        residentState = residentState.copy(gender = "Female")
                        expanded = false
                    }) {
                        Text("Female")
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text("ID Number:", modifier = Modifier.weight(1f))
            TextField(
                value = residentState.idNumber,
                enabled = mode != WindowMode.VIEW,
                onValueChange = { residentState = residentState.copy(idNumber = it) },
                modifier = Modifier.weight(2f).focusOrder(focusRequesters[3]) { next = focusRequesters[4] },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusRequesters[4].requestFocus() })
            )
        }

        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text("Phone Number:", modifier = Modifier.weight(1f))
            TextField(
                value = residentState.phoneNumber ?: "",
                enabled = mode != WindowMode.VIEW,
                onValueChange = { residentState = residentState.copy(phoneNumber = it) },
                modifier = Modifier.weight(2f).focusOrder(focusRequesters[4]) { next = focusRequesters[5] },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusRequesters[5].requestFocus() })
            )
        }

        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text("Email:", modifier = Modifier.weight(1f))
            TextField(
                value = residentState.email ?: "",
                enabled = mode != WindowMode.VIEW,
                onValueChange = { residentState = residentState.copy(email = it) },
                modifier = Modifier.weight(2f).focusOrder(focusRequesters[5]) { next = focusRequesters[6] },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusRequesters[6].requestFocus() })
            )
        }

    }
}
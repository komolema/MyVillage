package ui.screens.resident.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusOrder
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ui.screens.resident.WindowMode
import viewmodel.ResidentWindowViewModel
import java.util.*
import javax.swing.JPanel
import models.domain.Resident
import org.jdatepicker.impl.JDatePanelImpl
import org.jdatepicker.impl.JDatePickerImpl
import org.jdatepicker.impl.UtilDateModel
import java.text.SimpleDateFormat
import java.time.ZoneId
import javax.swing.JFormattedTextField
import localization.LocaleManager
import localization.StringResourcesManager
import localization.StringResources

enum class Gender {
    MALE,
    FEMALE;

    // Default display name for tests
    val displayName: String
        get() = when (this) {
            MALE -> "Male"
            FEMALE -> "Female"
        }

    fun getDisplayName(strings: StringResources): String {
        return when (this) {
            MALE -> strings.genderMaleDisplay
            FEMALE -> strings.genderFemaleDisplay
        }
    }

    companion object {
        fun fromString(value: String?, strings: StringResources): Gender? = values().find { it.getDisplayName(strings) == value }
    }
}

@Composable
fun ResidentTab(
    residentId: UUID?,
    viewModel: ResidentWindowViewModel,
    mode: WindowMode,
    onTabStateChange: (TabCompletionState) -> Unit = {}
) {
    var residentState by remember { mutableStateOf(Resident.default) }
    val viewModelState by viewModel.state.collectAsStateWithLifecycle()
    val strings = remember { mutableStateOf(StringResourcesManager.getCurrentStringResources()) }

    // Update strings when locale changes
    LaunchedEffect(LocaleManager.getCurrentLocale()) {
        strings.value = StringResourcesManager.getCurrentStringResources()
    }

    // Error states
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var idNumberError by remember { mutableStateOf<String?>(null) }

    // Validation functions
    fun validateEmail(email: String?): Boolean {
        return if (email.isNullOrBlank()) true
        else email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)\$")).also { isValid ->
            emailError = if (isValid) null else strings.value.invalidEmailFormat
        }
    }

    fun validatePhone(phone: String?): Boolean {
        return if (phone.isNullOrBlank()) true
        else phone.matches(Regex("^\\+?[0-9]{10,15}$")).also { isValid ->
            phoneError = if (isValid) null else strings.value.invalidPhoneFormat
        }
    }

    fun validateIdNumber(id: String): Boolean {
        return id.matches(Regex("^[0-9]{13}$")).also { isValid ->
            idNumberError = if (isValid) null else strings.value.idNumberMustBe13Digits
        }
    }

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

    // Function to check form completion
    fun checkFormCompletion(): TabCompletionState {
        val requiredFields = listOf(
            residentState.firstName,
            residentState.lastName,
            residentState.idNumber
        )

        return when {
            requiredFields.all { it.isNotBlank() } -> TabCompletionState.DONE
            requiredFields.any { it.isNotBlank() } -> TabCompletionState.IN_PROGRESS
            else -> TabCompletionState.TODO
        }
    }

    // Monitor form changes and update tab state
    LaunchedEffect(residentState) {
        onTabStateChange(checkFormCompletion())

        // Only update if not in VIEW mode and there are actual changes
        if (mode != WindowMode.VIEW && residentState != viewModelState.resident) {
            viewModel.processIntent(
                ResidentWindowViewModel.Intent.UpdateResidentState(residentState)
            )
        }
    }

    // Create FocusRequesters for each TextField
    val focusRequesters = List(7) { FocusRequester() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text(strings.value.firstNameLabel, modifier = Modifier.weight(1f))
            TextField(
                value = residentState.firstName,
                enabled = mode != WindowMode.VIEW,
                onValueChange = { residentState = residentState.copy(firstName = it.trim()) },
                modifier = Modifier.weight(2f).focusOrder(focusRequesters[0]) { next = focusRequesters[1] },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusRequesters[1].requestFocus() }),
                placeholder = { Text(strings.value.enterFirstName) },
                singleLine = true
            )
        }

        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text(strings.value.lastNameLabel, modifier = Modifier.weight(1f))
            TextField(
                value = residentState.lastName,
                enabled = mode != WindowMode.VIEW,
                onValueChange = { residentState = residentState.copy(lastName = it.trim()) },
                modifier = Modifier.weight(2f).focusOrder(focusRequesters[1]) { next = focusRequesters[2] },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusRequesters[2].requestFocus() }),
                placeholder = { Text(strings.value.enterLastName) },
                singleLine = true
            )
        }

        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text(strings.value.dateOfBirthLabel, modifier = Modifier.weight(1f))
            Box(modifier = Modifier.weight(2f)) {
                TextField(
                    value = SimpleDateFormat("dd MMMM yyyy").format(Date.from(residentState.dob.atStartOfDay(ZoneId.systemDefault()).toInstant())),
                    enabled = false,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth().focusOrder(focusRequesters[2]) { next = focusRequesters[3] },
                    readOnly = true,
                    placeholder = { Text(strings.value.selectDateOfBirth) }
                )
                if (mode != WindowMode.VIEW) {
                    SwingPanel(
                        factory = {
                            val model = UtilDateModel().apply {
                                value = Date.from(residentState.dob.atStartOfDay(ZoneId.systemDefault()).toInstant())
                            }
                            val properties = Properties().apply {
                                put("text.today", strings.value.today)
                                put("text.month", strings.value.month)
                                put("text.year", strings.value.year)
                                put("text.select", strings.value.selectDate)
                            }
                            val datePanel = JDatePanelImpl(model, properties)
                            val datePicker = JDatePickerImpl(datePanel, DateLabelFormatter())
                            datePicker.addActionListener {
                                val selectedDate = datePicker.model.value as? Date
                                if (selectedDate != null) {
                                    residentState = residentState.copy(dob = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                                }
                            }
                            JPanel().apply { 
                                layout = java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0)
                                add(datePicker)
                                preferredSize = java.awt.Dimension(200, 30)
                                isEnabled = mode != WindowMode.VIEW
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .width(200.dp)
                            .height(30.dp)
                    )
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text(strings.value.genderLabel, modifier = Modifier.weight(1f))
            var expanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.weight(2f)) {
                OutlinedTextField(
                    value = residentState.gender,
                    enabled = mode != WindowMode.VIEW,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth()
                        .clickable(enabled = mode != WindowMode.VIEW) { expanded = true }
                        .focusOrder(focusRequesters[3]) { next = focusRequesters[4] },
                    readOnly = true,
                    placeholder = { Text(strings.value.selectGenderPlaceholder) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledTextColor = LocalContentColor.current.copy(alpha = ContentAlpha.high),
                        disabledBorderColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                        disabledPlaceholderColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                    ),
                    trailingIcon = {
                        if (mode != WindowMode.VIEW) {
                            Icon(
                                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (expanded) strings.value.closeGenderSelection else strings.value.openGenderSelection,
                                modifier = Modifier.clickable { expanded = !expanded }
                            )
                        }
                    }
                )
                if (mode != WindowMode.VIEW) {
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        Gender.values().forEach { gender ->
                            DropdownMenuItem(
                                onClick = {
                                    residentState = residentState.copy(gender = gender.getDisplayName(strings.value))
                                    expanded = false
                                }
                            ) {
                                Text(
                                    text = gender.getDisplayName(strings.value),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text(strings.value.idNumberLabel, modifier = Modifier.weight(1f))
            Column(modifier = Modifier.weight(2f)) {
                TextField(
                    value = residentState.idNumber,
                    enabled = mode != WindowMode.VIEW,
                    onValueChange = { 
                        residentState = residentState.copy(idNumber = it)
                        validateIdNumber(it)
                    },
                    modifier = Modifier.fillMaxWidth().focusOrder(focusRequesters[3]) { next = focusRequesters[4] },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusRequesters[4].requestFocus() }),
                    isError = idNumberError != null,
                    placeholder = { Text(strings.value.enterIdNumber) }
                )
                if (idNumberError != null) {
                    Text(
                        text = idNumberError!!,
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text(strings.value.phoneNumberLabel, modifier = Modifier.weight(1f))
            Column(modifier = Modifier.weight(2f)) {
                TextField(
                    value = residentState.phoneNumber ?: "",
                    enabled = mode != WindowMode.VIEW,
                    onValueChange = { 
                        residentState = residentState.copy(phoneNumber = it)
                        validatePhone(it)
                    },
                    modifier = Modifier.fillMaxWidth().focusOrder(focusRequesters[4]) { next = focusRequesters[5] },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusRequesters[5].requestFocus() }),
                    isError = phoneError != null,
                    placeholder = { Text(strings.value.enterPhoneNumber) }
                )
                if (phoneError != null) {
                    Text(
                        text = phoneError!!,
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text(strings.value.emailLabel, modifier = Modifier.weight(1f))
            Column(modifier = Modifier.weight(2f)) {
                TextField(
                    value = residentState.email ?: "",
                    enabled = mode != WindowMode.VIEW,
                    onValueChange = { 
                        residentState = residentState.copy(email = it)
                        validateEmail(it)
                    },
                    modifier = Modifier.fillMaxWidth().focusOrder(focusRequesters[5]) { next = focusRequesters[6] },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusRequesters[6].requestFocus() }),
                    isError = emailError != null,
                    placeholder = { Text(strings.value.enterEmail) }
                )
                if (emailError != null) {
                    Text(
                        text = emailError!!,
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

class DateLabelFormatter : JFormattedTextField.AbstractFormatter() {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    override fun stringToValue(text: String?): Any {
        return dateFormat.parse(text)
    }

    override fun valueToString(value: Any?): String {
        return if (value != null) {
//            dateFormat.format(value)
            ""
        } else {
            ""
        }
    }
}

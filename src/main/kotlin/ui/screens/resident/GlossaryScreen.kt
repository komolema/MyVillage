package ui.screens.resident

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import ui.components.ScrollableContainer
import utils.GlossaryPdfUtils
import utils.ProofOfAddressUtils
import viewmodel.ResidentWindowViewModel
import java.awt.Desktop
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.UUID
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import models.domain.Dependant
import models.domain.Employment
import models.domain.Qualification

/**
 * Glossary dialog that displays all information about a resident in a dialog.
 * This includes personal details, dependents, qualifications, and employment history.
 */
@Composable
fun GlossaryDialog(
    viewModel: ResidentWindowViewModel,
    residentId: UUID,
    onDismiss: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Load resident data when the dialog is first displayed
    LaunchedEffect(residentId) {
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadResident(residentId))
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadDependants(residentId))
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadQualifications(residentId))
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadEmployment(residentId))
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadResidence(residentId))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false  // Allow custom width
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)  // Use 85% of screen width instead of full width
                .heightIn(max = 900.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Dialog Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.primary)
                        .padding(vertical = 20.dp, horizontal = 24.dp)  // Increased padding
                ) {
                    Text(
                        text = "Resident Details",
                        style = MaterialTheme.typography.h5,  // Increased from h6 to h5
                        fontWeight = FontWeight.Bold,  // Added bold for emphasis
                        color = MaterialTheme.colors.onPrimary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(28.dp)  // Increased from 24.dp to 28.dp
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colors.onPrimary
                        )
                    }
                }

                // Dialog Content with Print Section
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp)  // Increased horizontal padding
                ) {
                    // Main scrollable content
                    GlossaryContent(
                        state = state,
                        modifier = Modifier
                            .weight(0.75f)  // Increased from 0.7f to 0.75f to give more space to content
                            .verticalScroll(rememberScrollState())
                            .padding(end = 16.dp)  // Add padding to the right of content
                    )

                    // Vertical divider to separate content from print section
                    Divider(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                            .padding(vertical = 16.dp),  // Increased vertical padding
                        color = Color.LightGray
                    )

                    // Print section
                    Column(
                        modifier = Modifier
                            .weight(0.25f)  // Decreased from 0.3f to 0.25f
                            .padding(start = 24.dp)  // Increased left padding
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Text(
                            text = "Print Options",
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.primary,  // Changed to primary color
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        // Print Glossary Button
                        var isGeneratingGlossaryPdf by remember { mutableStateOf(false) }
                        var glossaryPdfError by remember { mutableStateOf<String?>(null) }

                        OutlinedButton(
                            onClick = {
                                isGeneratingGlossaryPdf = true
                                glossaryPdfError = null

                                try {
                                    val resident = state.resident
                                    val address = state.address
                                    val dependants = state.dependants
                                    val qualifications = state.qualifications
                                    val employmentHistory = state.employmentHistory

                                    if (resident != null) {
                                        val pdfFile = GlossaryPdfUtils.generateGlossaryPdf(
                                            resident = resident,
                                            address = address,
                                            dependants = dependants,
                                            qualifications = qualifications,
                                            employmentHistory = employmentHistory
                                        )

                                        // Open the PDF file with the default PDF viewer
                                        if (Desktop.isDesktopSupported()) {
                                            Desktop.getDesktop().open(pdfFile)
                                        }
                                    }
                                } catch (e: Exception) {
                                    glossaryPdfError = "Error generating PDF: ${e.message}"
                                } finally {
                                    isGeneratingGlossaryPdf = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)  // Fixed height for consistency
                                .padding(vertical = 8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colors.primary),  // Added border color
                            enabled = !isGeneratingGlossaryPdf
                        ) {
                            Text(
                                "Print Glossary",
                                style = MaterialTheme.typography.button,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Show error message if there was an error generating the PDF
                        if (glossaryPdfError != null) {
                            Text(
                                text = glossaryPdfError!!,
                                color = MaterialTheme.colors.error,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }


                        // Print Dependents List Button
                        var isGeneratingDependentsPdf by remember { mutableStateOf(false) }
                        var dependentsPdfError by remember { mutableStateOf<String?>(null) }

                        OutlinedButton(
                            onClick = {
                                isGeneratingDependentsPdf = true
                                dependentsPdfError = null

                                try {
                                    val resident = state.resident
                                    val dependants = state.dependants

                                    if (resident != null) {
                                        val pdfFile = GlossaryPdfUtils.generateDependentsPdf(
                                            resident = resident,
                                            dependants = dependants
                                        )

                                        // Open the PDF file with the default PDF viewer
                                        if (Desktop.isDesktopSupported()) {
                                            Desktop.getDesktop().open(pdfFile)
                                        }
                                    }
                                } catch (e: Exception) {
                                    dependentsPdfError = "Error generating PDF: ${e.message}"
                                } finally {
                                    isGeneratingDependentsPdf = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)  // Fixed height for consistency
                                .padding(vertical = 8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colors.primary),  // Added border color
                            enabled = !isGeneratingDependentsPdf
                        ) {
                            Text(
                                "Print Dependents List",
                                style = MaterialTheme.typography.button,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Show error message if there was an error generating the PDF
                        if (dependentsPdfError != null) {
                            Text(
                                text = dependentsPdfError!!,
                                color = MaterialTheme.colors.error,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }

                        // Print Qualifications Button
                        var isGeneratingQualificationsPdf by remember { mutableStateOf(false) }
                        var qualificationsPdfError by remember { mutableStateOf<String?>(null) }

                        OutlinedButton(
                            onClick = {
                                isGeneratingQualificationsPdf = true
                                qualificationsPdfError = null

                                try {
                                    val resident = state.resident
                                    val qualifications = state.qualifications

                                    if (resident != null) {
                                        val pdfFile = GlossaryPdfUtils.generateQualificationsPdf(
                                            resident = resident,
                                            qualifications = qualifications
                                        )

                                        // Open the PDF file with the default PDF viewer
                                        if (Desktop.isDesktopSupported()) {
                                            Desktop.getDesktop().open(pdfFile)
                                        }
                                    }
                                } catch (e: Exception) {
                                    qualificationsPdfError = "Error generating PDF: ${e.message}"
                                } finally {
                                    isGeneratingQualificationsPdf = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)  // Fixed height for consistency
                                .padding(vertical = 8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colors.primary),  // Added border color
                            enabled = !isGeneratingQualificationsPdf
                        ) {
                            Text(
                                "Print Qualifications",
                                style = MaterialTheme.typography.button,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Show error message if there was an error generating the PDF
                        if (qualificationsPdfError != null) {
                            Text(
                                text = qualificationsPdfError!!,
                                color = MaterialTheme.colors.error,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }

                        // Print Employment History Button
                        var isGeneratingEmploymentPdf by remember { mutableStateOf(false) }
                        var employmentPdfError by remember { mutableStateOf<String?>(null) }

                        OutlinedButton(
                            onClick = {
                                isGeneratingEmploymentPdf = true
                                employmentPdfError = null

                                try {
                                    val resident = state.resident
                                    val employmentHistory = state.employmentHistory

                                    if (resident != null) {
                                        val pdfFile = GlossaryPdfUtils.generateEmploymentHistoryPdf(
                                            resident = resident,
                                            employmentHistory = employmentHistory
                                        )

                                        // Open the PDF file with the default PDF viewer
                                        if (Desktop.isDesktopSupported()) {
                                            Desktop.getDesktop().open(pdfFile)
                                        }
                                    }
                                } catch (e: Exception) {
                                    employmentPdfError = "Error generating PDF: ${e.message}"
                                } finally {
                                    isGeneratingEmploymentPdf = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)  // Fixed height for consistency
                                .padding(vertical = 8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colors.primary),  // Added border color
                            enabled = !isGeneratingEmploymentPdf
                        ) {
                            Text(
                                "Print Employment History",
                                style = MaterialTheme.typography.button,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Show error message if there was an error generating the PDF
                        if (employmentPdfError != null) {
                            Text(
                                text = employmentPdfError!!,
                                color = MaterialTheme.colors.error,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }

                // Dialog Footer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 24.dp),  // Increased padding
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .height(48.dp)  // Set fixed height for button
                            .widthIn(min = 120.dp),  // Set minimum width
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Text(
                            "Close",
                            style = MaterialTheme.typography.button,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Glossary screen that displays all information about a resident in one place.
 * This includes personal details, dependents, qualifications, and employment history.
 */
@Composable
fun GlossaryScreen(
    navController: NavController,
    viewModel: ResidentWindowViewModel,
    residentId: String
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Load resident data when the screen is first displayed
    LaunchedEffect(residentId) {
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadResident(UUID.fromString(residentId)))
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadDependants(UUID.fromString(residentId)))
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadQualifications(UUID.fromString(residentId)))
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadEmployment(UUID.fromString(residentId)))
    }

    var showPrintOptions by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resident Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Print button
                    IconButton(onClick = { showPrintOptions = true }) {
                        Icon(
                            imageVector = Icons.Default.Info, // Using Info icon for print options
                            contentDescription = "Print Options",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        ScrollableContainer(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            GlossaryContent(state = state)
        }

        // Show print options dialog when the print button is clicked
        if (showPrintOptions) {
            PrintOptionsDialog(
                state = state,
                onDismiss = { showPrintOptions = false }
            )
        }
    }
}

/**
 * Dialog for displaying print options.
 *
 * @param state The current state of the resident window
 * @param onDismiss Callback to dismiss the dialog
 */
@Composable
private fun PrintOptionsDialog(
    state: ResidentWindowState,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Print Options",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Print Glossary Button
                var isGeneratingGlossaryPdf by remember { mutableStateOf(false) }
                var glossaryPdfError by remember { mutableStateOf<String?>(null) }

                Button(
                    onClick = {
                        isGeneratingGlossaryPdf = true
                        glossaryPdfError = null

                        try {
                            val resident = state.resident
                            val address = state.address
                            val dependants = state.dependants
                            val qualifications = state.qualifications
                            val employmentHistory = state.employmentHistory

                            if (resident != null) {
                                val pdfFile = GlossaryPdfUtils.generateGlossaryPdf(
                                    resident = resident,
                                    address = address,
                                    dependants = dependants,
                                    qualifications = qualifications,
                                    employmentHistory = employmentHistory
                                )

                                // Open the PDF file with the default PDF viewer
                                if (Desktop.isDesktopSupported()) {
                                    Desktop.getDesktop().open(pdfFile)
                                }
                            }
                        } catch (e: Exception) {
                            glossaryPdfError = "Error generating PDF: ${e.message}"
                        } finally {
                            isGeneratingGlossaryPdf = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    enabled = !isGeneratingGlossaryPdf
                ) {
                    Text("Print Full Glossary")
                }

                // Show error message if there was an error generating the PDF
                if (glossaryPdfError != null) {
                    Text(
                        text = glossaryPdfError!!,
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                // Print Proof of Address Button
                var isGeneratingProofOfAddressPdf by remember { mutableStateOf(false) }
                var proofOfAddressPdfError by remember { mutableStateOf<String?>(null) }

                Button(
                    onClick = {
                        isGeneratingProofOfAddressPdf = true
                        proofOfAddressPdfError = null

                        try {
                            val resident = state.resident
                            val address = state.address
                            val residence = state.residence

                            if (resident != null && address != null && residence != null) {
                                val (pdfFile, _) = ProofOfAddressUtils.generateProofOfAddress(
                                    resident = resident,
                                    address = address,
                                    residence = residence
                                )

                                // Open the PDF file with the default PDF viewer
                                if (Desktop.isDesktopSupported()) {
                                    Desktop.getDesktop().open(pdfFile)
                                }
                            }
                        } catch (e: Exception) {
                            proofOfAddressPdfError = "Error generating PDF: ${e.message}"
                        } finally {
                            isGeneratingProofOfAddressPdf = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    enabled = !isGeneratingProofOfAddressPdf
                ) {
                    Text("Print Proof of Address")
                }

                // Show error message if there was an error generating the PDF
                if (proofOfAddressPdfError != null) {
                    Text(
                        text = proofOfAddressPdfError!!,
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                // Print Dependents List Button
                var isGeneratingDependentsPdf by remember { mutableStateOf(false) }
                var dependentsPdfError by remember { mutableStateOf<String?>(null) }

                Button(
                    onClick = {
                        isGeneratingDependentsPdf = true
                        dependentsPdfError = null

                        try {
                            val resident = state.resident
                            val dependants = state.dependants

                            if (resident != null) {
                                val pdfFile = GlossaryPdfUtils.generateDependentsPdf(
                                    resident = resident,
                                    dependants = dependants
                                )

                                // Open the PDF file with the default PDF viewer
                                if (Desktop.isDesktopSupported()) {
                                    Desktop.getDesktop().open(pdfFile)
                                }
                            }
                        } catch (e: Exception) {
                            dependentsPdfError = "Error generating PDF: ${e.message}"
                        } finally {
                            isGeneratingDependentsPdf = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    enabled = !isGeneratingDependentsPdf
                ) {
                    Text("Print Dependents List")
                }

                // Show error message if there was an error generating the PDF
                if (dependentsPdfError != null) {
                    Text(
                        text = dependentsPdfError!!,
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                // Print Qualifications Button
                var isGeneratingQualificationsPdf by remember { mutableStateOf(false) }
                var qualificationsPdfError by remember { mutableStateOf<String?>(null) }

                Button(
                    onClick = {
                        isGeneratingQualificationsPdf = true
                        qualificationsPdfError = null

                        try {
                            val resident = state.resident
                            val qualifications = state.qualifications

                            if (resident != null) {
                                val pdfFile = GlossaryPdfUtils.generateQualificationsPdf(
                                    resident = resident,
                                    qualifications = qualifications
                                )

                                // Open the PDF file with the default PDF viewer
                                if (Desktop.isDesktopSupported()) {
                                    Desktop.getDesktop().open(pdfFile)
                                }
                            }
                        } catch (e: Exception) {
                            qualificationsPdfError = "Error generating PDF: ${e.message}"
                        } finally {
                            isGeneratingQualificationsPdf = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    enabled = !isGeneratingQualificationsPdf
                ) {
                    Text("Print Qualifications")
                }

                // Show error message if there was an error generating the PDF
                if (qualificationsPdfError != null) {
                    Text(
                        text = qualificationsPdfError!!,
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                // Print Employment History Button
                var isGeneratingEmploymentPdf by remember { mutableStateOf(false) }
                var employmentPdfError by remember { mutableStateOf<String?>(null) }

                Button(
                    onClick = {
                        isGeneratingEmploymentPdf = true
                        employmentPdfError = null

                        try {
                            val resident = state.resident
                            val employmentHistory = state.employmentHistory

                            if (resident != null) {
                                val pdfFile = GlossaryPdfUtils.generateEmploymentHistoryPdf(
                                    resident = resident,
                                    employmentHistory = employmentHistory
                                )

                                // Open the PDF file with the default PDF viewer
                                if (Desktop.isDesktopSupported()) {
                                    Desktop.getDesktop().open(pdfFile)
                                }
                            }
                        } catch (e: Exception) {
                            employmentPdfError = "Error generating PDF: ${e.message}"
                        } finally {
                            isGeneratingEmploymentPdf = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    enabled = !isGeneratingEmploymentPdf
                ) {
                    Text("Print Employment History")
                }

                // Show error message if there was an error generating the PDF
                if (employmentPdfError != null) {
                    Text(
                        text = employmentPdfError!!,
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                // Close button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 16.dp)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

/**
 * Common content for the glossary screen and dialog.
 * This includes personal details, dependents, qualifications, and employment history.
 */
@Composable
fun GlossaryContent(
    state: ResidentWindowState,
    modifier: Modifier = Modifier
) {
    val resident = state.resident
    val address = state.address
    val dependants = state.dependants
    val qualifications = state.qualifications
    val employmentHistory = state.employmentHistory

    Column(modifier = modifier) {
        // Personal Details Section
        SectionCard("Personal Details") {
            DetailItem("Name", "${resident.firstName} ${resident.lastName}")
            DetailItem("ID Number", resident.idNumber)
            DetailItem("Gender", resident.gender)
            DetailItem("Date of Birth", resident.dob.format(DateTimeFormatter.ISO_DATE))
            val age = Period.between(resident.dob, LocalDate.now()).years
            DetailItem("Age", age.toString())
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Address Section
        SectionCard("Address") {
            if (address != null) {
                DetailItem("Line", address.line)
                DetailItem("House Number", address.houseNumber)
                DetailItem("Suburb", address.suburb)
                DetailItem("Town", address.town)
                DetailItem("Postal Code", address.postalCode)
                address.landmark?.let { landmark: String ->
                    if (landmark.isNotEmpty()) {
                        DetailItem("Landmark", landmark)
                    }
                }
                address.geoCoordinates?.let { coordinates: String ->
                    if (coordinates.isNotEmpty()) {
                        DetailItem("Geo Coordinates", coordinates)
                    }
                }
            } else {
                Text("No address information available")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dependents Section
        SectionCard("Dependents (${dependants.size})") {
            if (dependants.isEmpty()) {
                Text("No dependents")
            } else {
                dependants.forEach { dependant: Dependant ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            DetailItem("Name", "${dependant.name} ${dependant.surname}")
                            DetailItem("ID Number", dependant.idNumber)
                            DetailItem("Gender", dependant.gender)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Qualifications Section
        SectionCard("Qualifications (${qualifications.size})") {
            if (qualifications.isEmpty()) {
                Text("No qualifications")
            } else {
                qualifications.forEach { qualification: Qualification ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            DetailItem("Institution", qualification.institution)
                            DetailItem("Qualification", qualification.name)
                            DetailItem("NQF Level", qualification.nqfLevel.toString())
                            DetailItem("Start Date", qualification.startDate.format(DateTimeFormatter.ISO_DATE))
                            qualification.endDate?.let { endDate ->
                                DetailItem("End Date", endDate.format(DateTimeFormatter.ISO_DATE))
                            } ?: DetailItem("End Date", "Present")
                            DetailItem("City", qualification.city)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Employment History Section
        SectionCard("Employment History (${employmentHistory.size})") {
            if (employmentHistory.isEmpty()) {
                Text("No employment history")
            } else {
                employmentHistory.forEach { employment: Employment ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            DetailItem("Employer", employment.employer)
                            DetailItem("Role", employment.role)
                            DetailItem("Start Date", employment.startDate.format(DateTimeFormatter.ISO_DATE))
                            employment.endDate?.let { endDate ->
                                DetailItem("End Date", endDate.format(DateTimeFormatter.ISO_DATE))
                            } ?: DetailItem("End Date", "Present")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),  // Increased from 8.dp to 12.dp
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(8.dp)  // Added rounded corners
    ) {
        Column(modifier = Modifier.padding(20.dp)) {  // Increased from 16.dp to 20.dp
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary,  // Changed to primary color
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.h6,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

@Composable
private fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),  // Increased from 4.dp to 8.dp
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically  // Added vertical alignment
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),  // Slightly dimmed for contrast
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)  // Added end padding
        )
        Text(
            text = value,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSurface,  // Full opacity for better readability
            modifier = Modifier.weight(2f)
        )
    }
}

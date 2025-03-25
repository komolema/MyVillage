package ui.screens.resident

import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ui.screens.resident.tabs.TabCompletionState
import viewmodel.ResidentWindowViewModel
import java.time.LocalDate
import java.util.*

/**
 * JavaFX view for the resident detail screen.
 * This implementation includes tabs for different aspects of resident information.
 */
class ResidentDetailView(
    private val residentId: UUID?,
    private val mode: WindowMode,
    private val onClose: () -> Unit
) : BorderPane(), KoinComponent {

    private val viewModel: ResidentWindowViewModel by inject()
    private val tabPane = TabPane()
    private val statusLabel = Label()

    // Tab states
    private val tabStates = mutableMapOf<String, TabCompletionState>()

    init {
        // Set up the layout
        setupLayout()

        // Load resident data if in edit/view mode
        if (mode != WindowMode.NEW && residentId != null) {
            viewModel.processIntent(ResidentWindowViewModel.Intent.LoadResident(residentId))
        }
    }

    private fun setupLayout() {
        // Top bar with title
        val topBar = HBox(20.0)
        topBar.padding = Insets(20.0)
        topBar.style = "-fx-background-color: #f0f0f0;"

        val titleText = when (mode) {
            WindowMode.NEW -> "Add New Resident"
            WindowMode.UPDATE -> "Edit Resident"
            WindowMode.VIEW -> "View Resident"
        }

        val titleLabel = Label(titleText)
        titleLabel.font = Font.font("System", FontWeight.BOLD, 20.0)

        topBar.children.add(titleLabel)
        top = topBar

        // Set up tabs
        setupTabs()

        // Bottom bar with buttons
        val bottomBar = HBox(10.0)
        bottomBar.padding = Insets(10.0)
        bottomBar.style = "-fx-background-color: #f0f0f0;"

        val cancelButton = Button("Cancel")
        cancelButton.setOnAction {
            onClose()
        }

        val saveButton = Button("Save")
        saveButton.isDisable = mode == WindowMode.VIEW
        saveButton.setOnAction {
            saveResident()
        }

        bottomBar.children.addAll(cancelButton, saveButton)
        bottom = bottomBar

        // Center content
        center = tabPane
    }

    private fun setupTabs() {
        // Resident tab
        val residentTab = Tab("Resident")
        residentTab.isClosable = false
        residentTab.content = createResidentTabContent()

        // Qualifications tab
        val qualificationsTab = Tab("Qualifications")
        qualificationsTab.isClosable = false
        qualificationsTab.content = createPlaceholderContent("Qualifications")

        // Dependents tab
        val dependentsTab = Tab("Dependents")
        dependentsTab.isClosable = false
        dependentsTab.content = createPlaceholderContent("Dependents")

        // Residence tab
        val residenceTab = Tab("Residence")
        residenceTab.isClosable = false
        residenceTab.content = createResidenceTabContent()

        // Employment tab
        val employmentTab = Tab("Employment")
        employmentTab.isClosable = false
        employmentTab.content = createPlaceholderContent("Employment")

        // Add tabs to tab pane
        tabPane.tabs.addAll(residentTab, qualificationsTab, dependentsTab, residenceTab, employmentTab)
    }

    private fun createResidentTabContent(): Pane {
        val content = GridPane()
        content.padding = Insets(20.0)
        content.hgap = 10.0
        content.vgap = 10.0

        // First Name
        content.add(Label("First Name:"), 0, 0)
        val firstNameField = TextField()
        firstNameField.isEditable = mode != WindowMode.VIEW
        content.add(firstNameField, 1, 0)

        // Last Name
        content.add(Label("Last Name:"), 0, 1)
        val lastNameField = TextField()
        lastNameField.isEditable = mode != WindowMode.VIEW
        content.add(lastNameField, 1, 1)

        // Date of Birth
        content.add(Label("Date of Birth:"), 0, 2)
        val dobPicker = DatePicker()
        dobPicker.isEditable = mode != WindowMode.VIEW
        content.add(dobPicker, 1, 2)

        // Gender
        content.add(Label("Gender:"), 0, 3)
        val genderComboBox = ComboBox<String>()
        genderComboBox.items.addAll("Male", "Female", "Other")
        genderComboBox.isEditable = false
        genderComboBox.isDisable = mode == WindowMode.VIEW
        content.add(genderComboBox, 1, 3)

        // ID Number
        content.add(Label("ID Number:"), 0, 4)
        val idNumberField = TextField()
        idNumberField.isEditable = mode != WindowMode.VIEW
        content.add(idNumberField, 1, 4)

        // Phone Number
        content.add(Label("Phone Number:"), 0, 5)
        val phoneField = TextField()
        phoneField.isEditable = mode != WindowMode.VIEW
        content.add(phoneField, 1, 5)

        // Email
        content.add(Label("Email:"), 0, 6)
        val emailField = TextField()
        emailField.isEditable = mode != WindowMode.VIEW
        content.add(emailField, 1, 6)

        // Bind to view model
        viewModel.state.value.resident.let { resident ->
            firstNameField.text = resident.firstName
            lastNameField.text = resident.lastName
            // Set the date picker value
            if (resident.dob != LocalDate.of(1900, 1, 1)) { // Check if it's not the default date
                dobPicker.value = resident.dob
            }
            genderComboBox.selectionModel.select(resident.gender)
            idNumberField.text = resident.idNumber
            phoneField.text = resident.phoneNumber ?: ""
            emailField.text = resident.email ?: ""
        }

        return content
    }

    private fun createPlaceholderContent(tabName: String): Pane {
        val content = VBox(10.0)
        content.padding = Insets(20.0)

        val label = Label("$tabName tab content will be implemented in a future update.")
        content.children.add(label)

        return content
    }

    private fun createResidenceTabContent(): Pane {
        val content = VBox(15.0)
        content.padding = Insets(20.0)

        // Add a placeholder message
        val placeholderLabel = Label("Residence information will be fully implemented in a future update.")
        content.children.add(placeholderLabel)

        // Add a separator
        val separator = Separator()
        separator.padding = Insets(10.0, 0.0, 10.0, 0.0)
        content.children.add(separator)

        // Add a section for proof of address
        val proofSection = VBox(10.0)
        proofSection.style = "-fx-border-color: #dddddd; -fx-border-radius: 5; -fx-padding: 15;"

        val proofTitle = Label("Proof of Address")
        proofTitle.font = Font.font("System", FontWeight.BOLD, 14.0)

        val proofDescription = Label("Generate a proof of address document for this resident.")

        val generateButton = Button("Generate Proof of Address")
        generateButton.style = "-fx-background-color: #1976D2; -fx-text-fill: white;"
        generateButton.setOnAction {
            if (residentId != null) {
                openProofOfAddressDialog(residentId)
            } else {
                showAlert(
                    Alert.AlertType.WARNING,
                    "Cannot Generate Proof of Address",
                    "Please save the resident information first before generating a proof of address."
                )
            }
        }

        proofSection.children.addAll(proofTitle, proofDescription, generateButton)
        content.children.add(proofSection)

        return content
    }

    private fun openProofOfAddressDialog(residentId: UUID) {
        // Get the resident, address, and residence information
        val resident = viewModel.state.value.resident
        val address = viewModel.state.value.address
        val residence = viewModel.state.value.residence

        if (address == null || residence == null) {
            showAlert(
                Alert.AlertType.WARNING,
                "Missing Information",
                "Address or residence information is missing. Please add this information before generating a proof of address."
            )
            return
        }

        // Create and show the proof of address dialog
        val dialog = ProofOfAddressDialog(
            resident = resident,
            address = address,
            residence = residence,
            onDismiss = { /* Nothing to do on dismiss */ }
        )
        dialog.show()
    }

    private fun showAlert(type: Alert.AlertType, title: String, message: String) {
        val alert = Alert(type)
        alert.title = title
        alert.headerText = null
        alert.contentText = message
        alert.showAndWait()
    }

    private fun saveResident() {
        // In a real implementation, this would gather data from all tabs and save it
        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.title = "Save Resident"
        alert.headerText = null
        alert.contentText = "Saving resident data is not yet implemented in this version."
        alert.showAndWait()

        // Close the window after saving
        onClose()
    }

    fun updateTabState(tabName: String, state: TabCompletionState) {
        tabStates[tabName] = state

        // In a real implementation, this would update the visual state of the tabs
        // to indicate completion status
    }
}

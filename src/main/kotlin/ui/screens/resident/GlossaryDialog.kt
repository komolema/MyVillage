package ui.screens.resident

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.stage.Modality
import javafx.stage.Stage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import utils.GlossaryPdfUtils
import utils.ProofOfAddressUtils
import viewmodel.ResidentWindowViewModel
import java.awt.Desktop
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * JavaFX dialog that displays all information about a resident.
 * This includes personal details, dependents, qualifications, and employment history.
 */
class GlossaryDialog(
    private val residentId: UUID,
    private val onDismiss: () -> Unit
) : Stage(), KoinComponent {
    
    private val viewModel: ResidentWindowViewModel by inject()
    
    init {
        // Configure the dialog
        initModality(Modality.APPLICATION_MODAL)
        title = "Resident Details"
        width = 900.0
        height = 700.0
        
        // Create the main layout
        val mainLayout = BorderPane()
        mainLayout.padding = Insets(20.0)
        
        // Create the content area
        val contentArea = createContentArea()
        mainLayout.center = contentArea
        
        // Create the button area
        val buttonArea = createButtonArea()
        mainLayout.bottom = buttonArea
        
        // Set the scene
        scene = Scene(mainLayout)
        
        // Load resident data
        loadResidentData()
        
        // Set close handler
        setOnCloseRequest { onDismiss() }
    }
    
    private fun loadResidentData() {
        // Load resident data when the dialog is first displayed
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadResident(residentId))
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadDependants(residentId))
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadQualifications(residentId))
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadEmployment(residentId))
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadResidence(residentId))
    }
    
    private fun createContentArea(): ScrollPane {
        val contentPane = ScrollPane()
        contentPane.isFitToWidth = true
        
        val contentVBox = VBox(20.0)
        contentVBox.padding = Insets(10.0)
        
        // Personal Details Section
        contentVBox.children.add(createSectionCard("Personal Details") {
            val resident = viewModel.state.value.resident
            val grid = GridPane()
            grid.hgap = 10.0
            grid.vgap = 10.0
            
            addDetailRow(grid, 0, "Name", "${resident.firstName} ${resident.lastName}")
            addDetailRow(grid, 1, "ID Number", resident.idNumber)
            addDetailRow(grid, 2, "Gender", resident.gender)
            addDetailRow(grid, 3, "Date of Birth", resident.dob.format(DateTimeFormatter.ISO_DATE))
            val age = Period.between(resident.dob, LocalDate.now()).years
            addDetailRow(grid, 4, "Age", age.toString())
            
            return@createSectionCard grid
        })
        
        // Address Section
        contentVBox.children.add(createSectionCard("Address") {
            val address = viewModel.state.value.address
            val grid = GridPane()
            grid.hgap = 10.0
            grid.vgap = 10.0
            
            if (address != null) {
                var row = 0
                addDetailRow(grid, row++, "Line", address.line)
                addDetailRow(grid, row++, "House Number", address.houseNumber)
                addDetailRow(grid, row++, "Suburb", address.suburb)
                addDetailRow(grid, row++, "Town", address.town)
                addDetailRow(grid, row++, "Postal Code", address.postalCode)
                address.landmark?.let { landmark ->
                    if (landmark.isNotEmpty()) {
                        addDetailRow(grid, row++, "Landmark", landmark)
                    }
                }
                address.geoCoordinates?.let { coordinates ->
                    if (coordinates.isNotEmpty()) {
                        addDetailRow(grid, row++, "Geo Coordinates", coordinates)
                    }
                }
            } else {
                grid.add(Label("No address information available"), 0, 0, 2, 1)
            }
            
            return@createSectionCard grid
        })
        
        // Dependents Section
        contentVBox.children.add(createSectionCard("Dependents") {
            val dependents = viewModel.state.value.dependants
            val dependentsBox = VBox(10.0)
            
            if (dependents.isEmpty()) {
                dependentsBox.children.add(Label("No dependents"))
            } else {
                dependents.forEach { dependant ->
                    val dependantCard = VBox(5.0)
                    dependantCard.style = "-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 10;"
                    
                    val nameLabel = Label("Name: ${dependant.name} ${dependant.surname}")
                    val idLabel = Label("ID Number: ${dependant.idNumber}")
                    val genderLabel = Label("Gender: ${dependant.gender}")
                    
                    dependantCard.children.addAll(nameLabel, idLabel, genderLabel)
                    dependentsBox.children.add(dependantCard)
                }
            }
            
            return@createSectionCard dependentsBox
        })
        
        // Qualifications Section
        contentVBox.children.add(createSectionCard("Qualifications") {
            val qualifications = viewModel.state.value.qualifications
            val qualificationsBox = VBox(10.0)
            
            if (qualifications.isEmpty()) {
                qualificationsBox.children.add(Label("No qualifications"))
            } else {
                qualifications.forEach { qualification ->
                    val qualificationCard = VBox(5.0)
                    qualificationCard.style = "-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 10;"
                    
                    val institutionLabel = Label("Institution: ${qualification.institution}")
                    val nameLabel = Label("Qualification: ${qualification.name}")
                    val levelLabel = Label("NQF Level: ${qualification.nqfLevel}")
                    val startDateLabel = Label("Start Date: ${qualification.startDate.format(DateTimeFormatter.ISO_DATE)}")
                    val endDateLabel = Label("End Date: ${qualification.endDate?.format(DateTimeFormatter.ISO_DATE) ?: "Present"}")
                    val cityLabel = Label("City: ${qualification.city}")
                    
                    qualificationCard.children.addAll(institutionLabel, nameLabel, levelLabel, startDateLabel, endDateLabel, cityLabel)
                    qualificationsBox.children.add(qualificationCard)
                }
            }
            
            return@createSectionCard qualificationsBox
        })
        
        // Employment History Section
        contentVBox.children.add(createSectionCard("Employment History") {
            val employmentHistory = viewModel.state.value.employmentHistory
            val employmentBox = VBox(10.0)
            
            if (employmentHistory.isEmpty()) {
                employmentBox.children.add(Label("No employment history"))
            } else {
                employmentHistory.forEach { employment ->
                    val employmentCard = VBox(5.0)
                    employmentCard.style = "-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 10;"
                    
                    val employerLabel = Label("Employer: ${employment.employer}")
                    val roleLabel = Label("Role: ${employment.role}")
                    val startDateLabel = Label("Start Date: ${employment.startDate.format(DateTimeFormatter.ISO_DATE)}")
                    val endDateLabel = Label("End Date: ${employment.endDate?.format(DateTimeFormatter.ISO_DATE) ?: "Present"}")
                    
                    employmentCard.children.addAll(employerLabel, roleLabel, startDateLabel, endDateLabel)
                    employmentBox.children.add(employmentCard)
                }
            }
            
            return@createSectionCard employmentBox
        })
        
        contentPane.content = contentVBox
        return contentPane
    }
    
    private fun createButtonArea(): HBox {
        val buttonArea = HBox(10.0)
        buttonArea.alignment = Pos.CENTER_RIGHT
        buttonArea.padding = Insets(20.0, 0.0, 0.0, 0.0)
        
        val printButton = Button("Print Glossary")
        printButton.setOnAction {
            try {
                val resident = viewModel.state.value.resident
                val address = viewModel.state.value.address
                val dependants = viewModel.state.value.dependants
                val qualifications = viewModel.state.value.qualifications
                val employmentHistory = viewModel.state.value.employmentHistory
                
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
            } catch (e: Exception) {
                showErrorAlert("Error generating PDF", e.message ?: "Unknown error")
            }
        }
        
        val closeButton = Button("Close")
        closeButton.setOnAction {
            close()
            onDismiss()
        }
        
        buttonArea.children.addAll(printButton, closeButton)
        return buttonArea
    }
    
    private fun createSectionCard(title: String, contentCreator: () -> Pane): VBox {
        val card = VBox(10.0)
        card.style = "-fx-background-color: white; -fx-border-color: #dddddd; -fx-border-radius: 5; -fx-padding: 15;"
        
        val titleLabel = Label(title)
        titleLabel.font = Font.font("System", FontWeight.BOLD, 16.0)
        titleLabel.style = "-fx-text-fill: #1976D2;"
        
        val content = contentCreator()
        
        card.children.addAll(titleLabel, content)
        return card
    }
    
    private fun addDetailRow(grid: GridPane, row: Int, label: String, value: String) {
        val labelText = Label(label)
        labelText.font = Font.font("System", FontWeight.MEDIUM, 12.0)
        
        val valueText = Label(value)
        
        grid.add(labelText, 0, row)
        grid.add(valueText, 1, row)
        
        GridPane.setHgrow(valueText, Priority.ALWAYS)
    }
    
    private fun showErrorAlert(title: String, message: String) {
        val alert = Alert(Alert.AlertType.ERROR)
        alert.title = title
        alert.headerText = null
        alert.contentText = message
        alert.showAndWait()
    }
}
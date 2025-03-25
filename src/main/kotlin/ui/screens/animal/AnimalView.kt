package ui.screens.animal

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.util.Callback
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import models.domain.Animal
import models.domain.Resident
import models.expanded.AnimalExpanded
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ui.navigation.NavigationManager
import ui.navigation.NavigationRoute
import viewmodel.AnimalViewModel
import java.time.LocalDate
import java.util.*

/**
 * JavaFX view for the animal management screen.
 */
class AnimalView : BorderPane(), KoinComponent {
    private val viewModel: AnimalViewModel by inject()
    private val navigationManager = NavigationManager.getInstance()
    
    private val animalListView = ListView<AnimalExpanded>()
    private val loadingIndicator = ProgressIndicator()
    private val emptyLabel = Label("No animals found")
    private val contentArea = StackPane()
    
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    
    init {
        // Set up the layout
        setupLayout()
        
        // Load animals
        loadAnimals()
        
        // Observe state changes
        observeState()
    }
    
    private fun setupLayout() {
        // Top bar with title and back button
        val topBar = HBox(20.0)
        topBar.padding = Insets(20.0)
        topBar.style = "-fx-background-color: #f0f0f0;"
        
        val titleLabel = Label("Animal Management")
        titleLabel.font = Font.font("System", FontWeight.BOLD, 20.0)
        
        val backButton = Button("Back to Dashboard")
        backButton.setOnAction {
            navigationManager.navigateTo(NavigationRoute.Dashboard)
        }
        
        topBar.children.addAll(titleLabel, backButton)
        top = topBar
        
        // Main content area
        setupContentArea()
        center = contentArea
        
        // Add button at bottom
        val addButton = Button("Add Animal")
        addButton.style = "-fx-background-color: #9C27B0; -fx-text-fill: white;"
        addButton.setOnAction {
            viewModel.processIntent(AnimalViewModel.Intent.OpenCreateDialog())
        }
        
        val bottomBar = HBox(addButton)
        bottomBar.padding = Insets(20.0)
        bottomBar.alignment = Pos.CENTER_RIGHT
        bottom = bottomBar
    }
    
    private fun setupContentArea() {
        // Configure list view
        animalListView.setCellFactory { createAnimalListCell() }
        
        // Center loading indicator and empty label
        loadingIndicator.isVisible = true
        emptyLabel.isVisible = false
        
        // Add components to content area
        contentArea.children.addAll(animalListView, loadingIndicator, emptyLabel)
    }
    
    private fun createAnimalListCell(): ListCell<AnimalExpanded> {
        return object : ListCell<AnimalExpanded>() {
            override fun updateItem(item: AnimalExpanded?, empty: Boolean) {
                super.updateItem(item, empty)
                
                if (empty || item == null) {
                    text = null
                    graphic = null
                    return
                }
                
                val animal = item.animal
                
                // Create card layout
                val card = VBox(10.0)
                card.padding = Insets(15.0)
                card.style = "-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5;"
                
                // Header with species and action buttons
                val header = HBox(10.0)
                header.alignment = Pos.CENTER_LEFT
                
                val speciesLabel = Label("Species: ${animal.species}")
                speciesLabel.font = Font.font("System", FontWeight.BOLD, 16.0)
                
                val buttonsBox = HBox(5.0)
                buttonsBox.alignment = Pos.CENTER_RIGHT
                HBox.setHgrow(buttonsBox, Priority.ALWAYS)
                
                val editButton = Button("Edit")
                editButton.setOnAction {
                    viewModel.processIntent(AnimalViewModel.Intent.OpenEditDialog(animal))
                }
                
                val deleteButton = Button("Delete")
                deleteButton.setOnAction {
                    viewModel.processIntent(AnimalViewModel.Intent.ShowDeleteConfirmation(animal.id))
                }
                
                buttonsBox.children.addAll(editButton, deleteButton)
                header.children.addAll(speciesLabel, buttonsBox)
                
                // Animal details
                val detailsBox = VBox(5.0)
                detailsBox.children.addAll(
                    Label("Breed: ${animal.breed}"),
                    Label("Gender: ${animal.gender}"),
                    Label("Tag Number: ${animal.tagNumber}"),
                    Label("Health Status: ${animal.healthStatus}"),
                    Label("Vaccination Status: ${if (animal.vaccinationStatus) "Vaccinated" else "Not Vaccinated"}")
                )
                
                animal.vaccinationDate?.let {
                    detailsBox.children.add(Label("Vaccination Date: $it"))
                }
                
                // Ownership information
                val ownershipBox = VBox(5.0)
                item.ownership.fold(
                    { ownershipBox.children.add(Label("No ownership information")) },
                    { ownership ->
                        ownershipBox.children.add(Label("Owner ID: ${ownership.residentId}"))
                    }
                )
                
                card.children.addAll(header, detailsBox, ownershipBox)
                graphic = card
            }
        }
    }
    
    private fun loadAnimals() {
        viewModel.processIntent(AnimalViewModel.Intent.LoadAnimals(0))
    }
    
    private fun observeState() {
        coroutineScope.launch {
            viewModel.state.collectLatest { state ->
                Platform.runLater {
                    // Update loading state
                    loadingIndicator.isVisible = state.isLoading
                    
                    // Update list
                    if (!state.isLoading) {
                        if (state.animals.isEmpty()) {
                            animalListView.isVisible = false
                            emptyLabel.isVisible = true
                        } else {
                            animalListView.isVisible = true
                            emptyLabel.isVisible = false
                            animalListView.items = FXCollections.observableArrayList(state.animals)
                        }
                    }
                    
                    // Handle dialog state
                    if (state.isDialogOpen) {
                        showAnimalDialog(
                            state.currentAnimal,
                            state.isEditMode,
                            state.residents,
                            state.selectedResidentId
                        )
                    }
                    
                    // Handle delete confirmation
                    if (state.showDeleteConfirmation) {
                        showDeleteConfirmation()
                    }
                }
            }
        }
    }
    
    private fun showAnimalDialog(
        animal: Animal,
        isEditMode: Boolean,
        residents: List<Resident>,
        selectedResidentId: UUID?
    ) {
        val dialog = Dialog<Animal>()
        dialog.title = if (isEditMode) "Edit Animal" else "Add New Animal"
        
        // Set up dialog pane
        val dialogPane = dialog.dialogPane
        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
        
        // Create form layout
        val formLayout = GridPane()
        formLayout.hgap = 10.0
        formLayout.vgap = 10.0
        formLayout.padding = Insets(20.0)
        
        // Species field
        val speciesField = TextField(animal.species)
        formLayout.add(Label("Species:"), 0, 0)
        formLayout.add(speciesField, 1, 0)
        
        // Breed field
        val breedField = TextField(animal.breed)
        formLayout.add(Label("Breed:"), 0, 1)
        formLayout.add(breedField, 1, 1)
        
        // Gender field
        val genderField = TextField(animal.gender)
        formLayout.add(Label("Gender:"), 0, 2)
        formLayout.add(genderField, 1, 2)
        
        // Date of birth fields
        val dobYearField = TextField(animal.dob.year.toString())
        val dobMonthField = TextField(animal.dob.monthValue.toString())
        val dobDayField = TextField(animal.dob.dayOfMonth.toString())
        
        val dobBox = HBox(5.0)
        dobBox.children.addAll(
            dobYearField, Label("-"), dobMonthField, Label("-"), dobDayField
        )
        
        formLayout.add(Label("Date of Birth (YYYY-MM-DD):"), 0, 3)
        formLayout.add(dobBox, 1, 3)
        
        // Tag number field
        val tagNumberField = TextField(animal.tagNumber)
        formLayout.add(Label("Tag Number:"), 0, 4)
        formLayout.add(tagNumberField, 1, 4)
        
        // Health status field
        val healthStatusField = TextField(animal.healthStatus)
        formLayout.add(Label("Health Status:"), 0, 5)
        formLayout.add(healthStatusField, 1, 5)
        
        // Vaccination status checkbox
        val vaccinationStatusCheckbox = CheckBox("Vaccinated")
        vaccinationStatusCheckbox.isSelected = animal.vaccinationStatus
        formLayout.add(vaccinationStatusCheckbox, 1, 6)
        
        // Resident selection
        val residentComboBox = ComboBox<Resident>()
        residentComboBox.items = FXCollections.observableArrayList(residents)
        residentComboBox.setCellFactory {
            object : ListCell<Resident>() {
                override fun updateItem(item: Resident?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = if (empty || item == null) "" else "${item.firstName} ${item.lastName}"
                }
            }
        }
        residentComboBox.buttonCell = object : ListCell<Resident>() {
            override fun updateItem(item: Resident?, empty: Boolean) {
                super.updateItem(item, empty)
                text = if (empty || item == null) "Select a resident" else "${item.firstName} ${item.lastName}"
            }
        }
        
        // Set selected resident if available
        if (selectedResidentId != null) {
            val selectedResident = residents.find { it.id == selectedResidentId }
            if (selectedResident != null) {
                residentComboBox.selectionModel.select(selectedResident)
            }
        }
        
        residentComboBox.setOnAction {
            val selectedResident = residentComboBox.selectionModel.selectedItem
            if (selectedResident != null) {
                viewModel.processIntent(AnimalViewModel.Intent.SelectResident(selectedResident.id))
            }
        }
        
        formLayout.add(Label("Owner:"), 0, 7)
        formLayout.add(residentComboBox, 1, 7)
        
        dialogPane.content = formLayout
        
        // Handle dialog result
        dialog.setResultConverter { buttonType ->
            if (buttonType == ButtonType.OK) {
                try {
                    val year = dobYearField.text.toInt()
                    val month = dobMonthField.text.toInt()
                    val day = dobDayField.text.toInt()
                    val dob = LocalDate.of(year, month, day)
                    
                    return@setResultConverter animal.copy(
                        species = speciesField.text,
                        breed = breedField.text,
                        gender = genderField.text,
                        dob = dob,
                        tagNumber = tagNumberField.text,
                        healthStatus = healthStatusField.text,
                        vaccinationStatus = vaccinationStatusCheckbox.isSelected
                    )
                } catch (e: Exception) {
                    // Handle date parsing errors
                    Alert(Alert.AlertType.ERROR, "Invalid date format").showAndWait()
                    return@setResultConverter null
                }
            }
            null
        }
        
        // Show dialog and process result
        dialog.showAndWait().ifPresent { updatedAnimal ->
            if (isEditMode) {
                viewModel.processIntent(AnimalViewModel.Intent.SaveAnimalChanges(updatedAnimal))
            } else {
                viewModel.processIntent(AnimalViewModel.Intent.CreateAnimal(updatedAnimal))
            }
        }
    }
    
    private fun showDeleteConfirmation() {
        val alert = Alert(Alert.AlertType.CONFIRMATION)
        alert.title = "Confirm Deletion"
        alert.headerText = null
        alert.contentText = "Are you sure you want to delete this animal?"
        
        alert.buttonTypes.setAll(ButtonType.YES, ButtonType.NO)
        
        alert.showAndWait().ifPresent { buttonType ->
            if (buttonType == ButtonType.YES) {
                viewModel.processIntent(AnimalViewModel.Intent.ConfirmDeleteAnimal())
            } else {
                viewModel.processIntent(AnimalViewModel.Intent.HideDeleteConfirmation())
            }
        }
    }
}
package ui.screens.resident

import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.layout.*
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.util.Callback
import kotlinx.coroutines.*
import models.expanded.ResidentExpanded
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ui.navigation.NavigationManager
import ui.navigation.NavigationRoute
import ui.screens.resident.tabs.TabCompletionState
import ui.screens.resident.WindowMode
import viewmodel.ResidentViewModel
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * JavaFX view for the resident management screen.
 * This implementation includes a table for displaying residents and tabs for different aspects of resident information.
 */
class ResidentView : BorderPane(), KoinComponent {
    private val navigationManager = NavigationManager.getInstance()
    private val viewModel: ResidentViewModel by inject()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    // UI components
    private val searchField = TextField()
    private val residentsTable = TableView<ResidentExpanded>()
    private val statusLabel = Label()

    // State
    private var currentPage = 0
    private var totalPages = 0
    private var totalItems = 0

    init {
        // Set up the layout
        setupLayout()

        // Start collecting state from the ViewModel
        collectState()
    }

    private fun collectState() {
        coroutineScope.launch {
            viewModel.state.collect { state ->
                Platform.runLater {
                    updateUI(state)
                }
            }
        }

        // Initial data load
        viewModel.processIntent(ResidentViewModel.Intent.LoadResidents(0))
    }

    private fun updateUI(state: ResidentState) {
        // Update table data
        residentsTable.items = FXCollections.observableArrayList(state.residents)

        // Update pagination info
        totalItems = state.totalItems
        totalPages = (totalItems + 19) / 20 // 20 items per page, rounded up

        // Update status label
        statusLabel.text = "Showing ${state.residents.size} of $totalItems residents"

        // Show/hide loading indicator
        if (state.isLoading) {
            // Show loading indicator
        } else {
            // Hide loading indicator
        }
    }

    private fun setupLayout() {
        // Top bar with title and back button
        val topBar = HBox(20.0)
        topBar.padding = Insets(20.0)
        topBar.style = "-fx-background-color: #f0f0f0;"

        val titleLabel = Label("Resident Management")
        titleLabel.font = Font.font("System", FontWeight.BOLD, 20.0)

        val backButton = Button("Back to Dashboard")
        backButton.setOnAction {
            navigationManager.navigateTo(NavigationRoute.Dashboard)
        }

        topBar.children.addAll(titleLabel, backButton)
        top = topBar

        // Main content area
        val contentBox = VBox(16.0)
        contentBox.padding = Insets(20.0)

        // Search bar
        val searchBox = HBox(10.0)
        searchBox.alignment = Pos.CENTER_LEFT

        searchField.promptText = "Search residents..."
        searchField.prefWidth = 300.0

        val searchButton = Button("Search")
        searchButton.setOnAction {
            performSearch()
        }

        // Add search components to search box
        searchBox.children.addAll(searchField, searchButton)

        // Set up the table
        setupTable()

        // Pagination controls
        val paginationBox = HBox(10.0)
        paginationBox.alignment = Pos.CENTER

        val prevButton = Button("Previous")
        prevButton.setOnAction {
            if (currentPage > 0) {
                currentPage--
                viewModel.processIntent(ResidentViewModel.Intent.LoadResidents(currentPage))
            }
        }

        val nextButton = Button("Next")
        nextButton.setOnAction {
            if (currentPage < totalPages - 1) {
                currentPage++
                viewModel.processIntent(ResidentViewModel.Intent.LoadResidents(currentPage))
            }
        }

        paginationBox.children.addAll(prevButton, statusLabel, nextButton)

        // Add new resident button
        val addButton = Button("Add New Resident")
        addButton.style = "-fx-background-color: #1976D2; -fx-text-fill: white;"
        addButton.setOnAction {
            openResidentWindow(null, WindowMode.NEW)
        }

        // Add components to content box
        contentBox.children.addAll(searchBox, residentsTable, paginationBox, addButton)

        center = contentBox
    }

    private fun setupTable() {
        residentsTable.isEditable = true
        residentsTable.columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY

        // First Name column
        val firstNameCol = TableColumn<ResidentExpanded, String>("First Name")
        firstNameCol.setCellValueFactory { SimpleStringProperty(it.value.resident.firstName) }
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn())
        firstNameCol.setOnEditCommit { event ->
            val resident = event.rowValue.resident.copy(firstName = event.newValue)
            viewModel.processIntent(ResidentViewModel.Intent.SaveResidentChanges(resident))
        }

        // Last Name column
        val lastNameCol = TableColumn<ResidentExpanded, String>("Last Name")
        lastNameCol.setCellValueFactory { SimpleStringProperty(it.value.resident.lastName) }
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn())
        lastNameCol.setOnEditCommit { event ->
            val resident = event.rowValue.resident.copy(lastName = event.newValue)
            viewModel.processIntent(ResidentViewModel.Intent.SaveResidentChanges(resident))
        }

        // Date of Birth column
        val dobCol = TableColumn<ResidentExpanded, String>("Date of Birth")
        dobCol.setCellValueFactory { 
            SimpleStringProperty(it.value.resident.dob.format(DateTimeFormatter.ISO_DATE))
        }

        // Age column
        val ageCol = TableColumn<ResidentExpanded, Int>("Age")
        ageCol.setCellValueFactory { 
            val age = Period.between(it.value.resident.dob, LocalDate.now()).years
            SimpleObjectProperty(age)
        }

        // Gender column
        val genderCol = TableColumn<ResidentExpanded, String>("Gender")
        genderCol.setCellValueFactory { SimpleStringProperty(it.value.resident.gender) }

        // Residence column
        val residenceCol = TableColumn<ResidentExpanded, String>("Residence")
        residenceCol.setCellValueFactory { 
            SimpleStringProperty(
                it.value.address.fold(
                    { "No address" },
                    { it.formatFriendly() }
                )
            )
        }

        // Action columns
        val glossaryCol = TableColumn<ResidentExpanded, Void>("Glossary")
        glossaryCol.setCellFactory {
            object : TableCell<ResidentExpanded, Void>() {
                private val glossaryButton = Button("Info").apply {
                    style = "-fx-background-color: #2196F3; -fx-text-fill: white;"
                    setOnAction {
                        val resident = tableView.items[index]
                        openGlossaryDialog(resident.resident.id)
                    }
                }

                override fun updateItem(item: Void?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty) {
                        graphic = null
                    } else {
                        graphic = glossaryButton
                    }
                }
            }
        }

        val editCol = TableColumn<ResidentExpanded, Void>("Edit")
        editCol.setCellFactory {
            object : TableCell<ResidentExpanded, Void>() {
                private val editButton = Button("Edit").apply {
                    setOnAction {
                        val resident = tableView.items[index]
                        openResidentWindow(resident.resident.id, WindowMode.UPDATE)
                    }
                }

                override fun updateItem(item: Void?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty) {
                        graphic = null
                    } else {
                        graphic = editButton
                    }
                }
            }
        }

        val deleteCol = TableColumn<ResidentExpanded, Void>("Delete")
        deleteCol.setCellFactory {
            object : TableCell<ResidentExpanded, Void>() {
                private val deleteButton = Button("Delete").apply {
                    style = "-fx-background-color: #f44336; -fx-text-fill: white;"
                    setOnAction {
                        val resident = tableView.items[index]
                        confirmDelete(resident)
                    }
                }

                override fun updateItem(item: Void?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty) {
                        graphic = null
                    } else {
                        graphic = deleteButton
                    }
                }
            }
        }

        // Add columns to table
        residentsTable.columns.addAll(
            firstNameCol, lastNameCol, dobCol, ageCol, genderCol, residenceCol, glossaryCol, editCol, deleteCol
        )

        // Set table height
        residentsTable.prefHeight = 500.0
    }

    private fun performSearch() {
        val searchText = searchField.text
        if (searchText.isBlank()) {
            viewModel.processIntent(ResidentViewModel.Intent.LoadResidents(0))
        } else {
            viewModel.processIntent(ResidentViewModel.Intent.Search(searchText, 0))
        }
        currentPage = 0
    }

    private fun openResidentWindow(residentId: UUID?, mode: WindowMode) {
        // Create a new stage for the resident detail view
        val stage = javafx.stage.Stage()
        stage.title = when (mode) {
            WindowMode.NEW -> "Add New Resident"
            WindowMode.UPDATE -> "Edit Resident"
            WindowMode.VIEW -> "View Resident"
        }

        // Create the resident detail view
        val detailView = ResidentDetailView(
            residentId = residentId,
            mode = mode,
            onClose = { stage.close() }
        )

        // Set the scene and show the stage
        val scene = javafx.scene.Scene(detailView, 800.0, 600.0)
        stage.scene = scene
        stage.show()
    }

    private fun confirmDelete(resident: ResidentExpanded) {
        val alert = Alert(Alert.AlertType.CONFIRMATION)
        alert.title = "Confirm Delete"
        alert.headerText = "Delete Resident"
        alert.contentText = "Are you sure you want to delete ${resident.resident.firstName} ${resident.resident.lastName}?"

        val result = alert.showAndWait()
        if (result.isPresent && result.get() == ButtonType.OK) {
            viewModel.processIntent(ResidentViewModel.Intent.DeleteResident(resident.resident.id))
        }
    }

    private fun openGlossaryDialog(residentId: UUID) {
        // Create and show the glossary dialog
        val dialog = GlossaryDialog(
            residentId = residentId,
            onDismiss = { /* Nothing to do on dismiss */ }
        )
        dialog.show()
    }
}

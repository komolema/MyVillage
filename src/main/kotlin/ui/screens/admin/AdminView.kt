package ui.screens.admin

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ui.navigation.NavigationManager
import ui.navigation.NavigationRoute
import database.dao.audit.DocumentsGeneratedDao
import localization.StringResourcesManager
import security.VillageSecurityManager
import java.time.format.DateTimeFormatter

/**
 * JavaFX view for the admin screen.
 */
class AdminView : BorderPane(), KoinComponent {
    private val navigationManager = NavigationManager.getInstance()
    private val documentsGeneratedDao: DocumentsGeneratedDao by inject()
    private val securityManager: VillageSecurityManager by inject()
    private val strings = StringResourcesManager.getCurrentStringResources()

    init {
        // Set up the layout
        setupLayout()
    }

    private fun setupLayout() {
        // Top bar with title and back button
        val topBar = HBox(20.0)
        topBar.padding = Insets(20.0)
        topBar.style = "-fx-background-color: #f0f0f0;"

        val titleLabel = Label(strings.adminScreen)
        titleLabel.font = Font.font("System", FontWeight.BOLD, 20.0)

        val backButton = Button("Back to Dashboard")
        backButton.setOnAction {
            navigationManager.navigateTo(NavigationRoute.Dashboard)
        }

        topBar.children.addAll(titleLabel, backButton)
        top = topBar

        // Main content area
        if (securityManager.hasRole("ADMIN")) {
            setupAdminContent()
        } else {
            setupUnauthorizedContent()
        }
    }

    private fun setupAdminContent() {
        val contentBox = VBox(16.0)
        contentBox.padding = Insets(20.0)
        contentBox.alignment = Pos.CENTER

        val showDocumentsButton = Button(strings.generatedDocuments)
        showDocumentsButton.prefWidth = 300.0
        showDocumentsButton.setOnAction {
            showDocumentsDialog()
        }

        val adminContentLabel = Label("Admin Content")
        adminContentLabel.font = Font.font(16.0)

        contentBox.children.addAll(showDocumentsButton, adminContentLabel)
        center = contentBox
    }

    private fun setupUnauthorizedContent() {
        val contentBox = VBox()
        contentBox.padding = Insets(20.0)
        contentBox.alignment = Pos.CENTER

        val unauthorizedLabel = Label("You do not have permission to access this screen")
        unauthorizedLabel.font = Font.font(16.0)

        contentBox.children.add(unauthorizedLabel)
        center = contentBox
    }

    private fun showDocumentsDialog() {
        val documents = transaction {
            documentsGeneratedDao.getAllDocuments()
        }

        val dialog = Dialog<Void>()
        dialog.title = strings.generatedDocumentsList
        dialog.dialogPane.buttonTypes.add(ButtonType.CLOSE)

        val contentBox = VBox(8.0)
        contentBox.padding = Insets(10.0)

        if (documents.isEmpty()) {
            contentBox.children.add(Label("No documents found"))
        } else {
            val scrollPane = ScrollPane()
            scrollPane.isFitToWidth = true
            scrollPane.prefHeight = 400.0
            scrollPane.prefWidth = 600.0

            val documentsBox = VBox(8.0)
            documentsBox.padding = Insets(5.0)

            for (document in documents) {
                val documentCard = VBox(5.0)
                documentCard.padding = Insets(10.0)
                documentCard.style = "-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5;"

                documentCard.children.addAll(
                    Label("Document Type: ${document.documentType}"),
                    Label("Reference: ${document.referenceNumber}"),
                    Label("Generated: ${document.generatedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}"),
                    Label("Related Entity: ${document.relatedEntityType} (${document.relatedEntityId})")
                )

                if (document.filePath != null) {
                    documentCard.children.add(Label("File Path: ${document.filePath}"))
                }

                documentsBox.children.add(documentCard)
            }

            scrollPane.content = documentsBox
            contentBox.children.add(scrollPane)
        }

        dialog.dialogPane.content = contentBox
        dialog.showAndWait()
    }
}

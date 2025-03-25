package ui.screens.resident

import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.stage.Modality
import javafx.stage.Stage
import kotlinx.coroutines.*
import models.domain.Address
import models.domain.Resident
import models.domain.Residence
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.Desktop
import java.io.File
import java.util.*

/**
 * JavaFX dialog for generating and displaying a proof of address PDF.
 *
 * @param resident The resident for whom to generate the proof of address
 * @param address The address to include in the proof of address
 * @param residence The residence information linking the resident to the address
 * @param onDismiss Callback to dismiss the dialog
 */
class ProofOfAddressDialog(
    private val resident: Resident,
    private val address: Address,
    private val residence: Residence,
    private val onDismiss: () -> Unit
) : Stage(), KoinComponent {

    private var isGenerating = true
    private var pdfFile: File? = null
    private var referenceNumber = ""
    private var verificationCode = ""
    private var errorMessage: String? = null

    // UI components
    private val statusLabel = Label("Generating proof of address...")
    private val progressIndicator = ProgressIndicator()
    private val contentVBox = VBox(10.0)

    init {
        // Configure the dialog
        initModality(Modality.APPLICATION_MODAL)
        title = "Proof of Address"
        width = 500.0
        height = 400.0

        // Create the main layout
        val mainLayout = BorderPane()
        mainLayout.padding = Insets(20.0)

        // Create the title
        val titleLabel = Label("Proof of Address")
        titleLabel.font = Font.font("System", FontWeight.BOLD, 18.0)

        // Create the content area
        contentVBox.alignment = Pos.CENTER
        contentVBox.padding = Insets(20.0)
        contentVBox.children.addAll(progressIndicator, statusLabel)

        // Create the button area
        val buttonArea = HBox(10.0)
        buttonArea.alignment = Pos.CENTER_RIGHT
        buttonArea.padding = Insets(10.0, 0.0, 0.0, 0.0)

        val closeButton = Button("Close")
        closeButton.setOnAction {
            close()
            onDismiss()
        }

        buttonArea.children.add(closeButton)

        // Set up the layout
        mainLayout.top = titleLabel
        mainLayout.center = contentVBox
        mainLayout.bottom = buttonArea

        // Set the scene
        scene = Scene(mainLayout)

        // Generate the PDF when the dialog is first shown
        generateProofOfAddress()

        // Set close handler
        setOnCloseRequest { onDismiss() }
    }

    private fun generateProofOfAddress() {
        // Use a coroutine to generate the PDF in the background
        val scope = CoroutineScope(Dispatchers.IO + Job())
        scope.launch {
            try {
                // Use the utility function to generate the proof of address
                val (pdf, proofOfAddress) = utils.ProofOfAddressUtils.generateProofOfAddress(
                    resident = resident,
                    address = address,
                    residence = residence
                )

                // Update the state
                pdfFile = pdf
                referenceNumber = proofOfAddress.referenceNumber
                verificationCode = proofOfAddress.verificationCode

                // Update the UI on the JavaFX thread
                withContext(Dispatchers.Main) {
                    isGenerating = false
                    updateUI()
                }
            } catch (e: Exception) {
                errorMessage = "Error generating proof of address: ${e.message}"

                // Update the UI on the JavaFX thread
                withContext(Dispatchers.Main) {
                    isGenerating = false
                    updateUI()
                }
            }
        }
    }

    private fun updateUI() {
        // Clear existing content
        contentVBox.children.clear()

        if (isGenerating) {
            // Show loading state
            contentVBox.children.addAll(progressIndicator, statusLabel)
        } else if (errorMessage != null) {
            // Show error state
            val errorIcon = Label("⚠")
            errorIcon.font = Font.font("System", FontWeight.BOLD, 24.0)
            errorIcon.style = "-fx-text-fill: #f44336;"

            val errorText = Label(errorMessage)
            errorText.style = "-fx-text-fill: #f44336;"

            contentVBox.children.addAll(errorIcon, errorText)
        } else {
            // Show success state
            val successText = Label("Proof of address has been generated successfully.")
            val referenceText = Label("Reference Number: $referenceNumber")
            val verificationText = Label("Verification Code: $verificationCode")

            // Action buttons
            val buttonBox = HBox(20.0)
            buttonBox.alignment = Pos.CENTER
            buttonBox.padding = Insets(20.0, 0.0, 0.0, 0.0)

            val viewButton = Button("View")
            viewButton.setOnAction {
                try {
                    // Open the PDF file with the default PDF viewer
                    if (pdfFile != null && Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(pdfFile)
                    }
                } catch (e: Exception) {
                    showErrorAlert("Error opening PDF", e.message ?: "Unknown error")
                }
            }

            val printButton = Button("Print")
            printButton.setOnAction {
                try {
                    // Print the PDF file
                    if (pdfFile != null && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.PRINT)) {
                        Desktop.getDesktop().print(pdfFile)
                    }
                } catch (e: Exception) {
                    showErrorAlert("Error printing PDF", e.message ?: "Unknown error")
                }
            }

            buttonBox.children.addAll(viewButton, printButton)

            contentVBox.children.addAll(
                successText,
                referenceText,
                verificationText,
                buttonBox
            )
        }
    }

    private fun showErrorAlert(title: String, message: String) {
        val alert = Alert(Alert.AlertType.ERROR)
        alert.title = title
        alert.headerText = null
        alert.contentText = message
        alert.showAndWait()
    }
}

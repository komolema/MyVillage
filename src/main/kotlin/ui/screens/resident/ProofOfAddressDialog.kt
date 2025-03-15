package ui.screens.resident

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import models.domain.Address
import models.domain.Resident
import models.domain.Residence
import org.koin.java.KoinJavaComponent.inject
import java.awt.Desktop
import java.io.File
import java.util.*

/**
 * Dialog for generating and displaying a proof of address PDF.
 *
 * @param resident The resident for whom to generate the proof of address
 * @param address The address to include in the proof of address
 * @param residence The residence information linking the resident to the address
 * @param onDismiss Callback to dismiss the dialog
 */
@Composable
fun ProofOfAddressDialog(
    resident: Resident,
    address: Address,
    residence: Residence,
    onDismiss: () -> Unit
) {
    var isGenerating by remember { mutableStateOf(true) }
    var pdfFile by remember { mutableStateOf<File?>(null) }
    var referenceNumber by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Generate the PDF when the dialog is first shown
    LaunchedEffect(Unit) {
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
            isGenerating = false
        } catch (e: Exception) {
            errorMessage = "Error generating proof of address: ${e.message}"
            isGenerating = false
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Proof of Address",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp)
                    )
                    Text("Generating proof of address...")
                } else if (errorMessage != null) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    // Display PDF information
                    Text(
                        text = "Proof of address has been generated successfully.",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Reference Number: $referenceNumber",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Verification Code: $verificationCode",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                try {
                                    // Open the PDF file with the default PDF viewer
                                    if (pdfFile != null && Desktop.isDesktopSupported()) {
                                        Desktop.getDesktop().open(pdfFile)
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Error opening PDF: ${e.message}"
                                }
                            }
                        ) {
                            Icon(Icons.Default.Info, contentDescription = "View")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("View")
                        }

                        Button(
                            onClick = {
                                try {
                                    // Print the PDF file
                                    if (pdfFile != null && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.PRINT)) {
                                        Desktop.getDesktop().print(pdfFile)
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Error printing PDF: ${e.message}"
                                }
                            }
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Print")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Print")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

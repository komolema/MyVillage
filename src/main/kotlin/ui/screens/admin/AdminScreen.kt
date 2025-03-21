package ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import database.dao.audit.DocumentsGeneratedDao
import localization.StringResourcesManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.compose.koinInject
import theme.YellowButtonColor
import ui.components.SecureRoleComponent
import ui.components.navigation.ScreenWithAppBar
import java.time.format.DateTimeFormatter

@Composable
fun AdminScreen(navController: NavController) {
    val strings = remember { mutableStateOf(StringResourcesManager.getCurrentStringResources()) }
    val documentsGeneratedDao = koinInject<DocumentsGeneratedDao>()
    var showDocumentsDialog by remember { mutableStateOf(false) }

    ScreenWithAppBar(strings.value.adminScreen, { navController.navigate("dashboard") }, YellowButtonColor) {
        SecureRoleComponent(
            role = "ADMIN",
            fallback = {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("You do not have permission to access this screen")
                }
            }
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { showDocumentsDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(strings.value.generatedDocuments)
                }

                Text("Admin Content")
            }
        }

        if (showDocumentsDialog) {
            val documents = remember {
                transaction {
                    documentsGeneratedDao.getAllDocuments()
                }
            }

            AlertDialog(
                onDismissRequest = { showDocumentsDialog = false },
                title = { Text(strings.value.generatedDocumentsList) },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    ) {
                        if (documents.isEmpty()) {
                            Text("No documents found")
                        } else {
                            documents.forEach { document ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    elevation = 2.dp
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("Document Type: ${document.documentType}")
                                        Text("Reference: ${document.referenceNumber}")
                                        Text("Generated: ${document.generatedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}")
                                        Text("Related Entity: ${document.relatedEntityType} (${document.relatedEntityId})")
                                        if (document.filePath != null) {
                                            Text("File Path: ${document.filePath}")
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showDocumentsDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

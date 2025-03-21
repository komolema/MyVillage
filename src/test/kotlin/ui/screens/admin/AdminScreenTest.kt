package ui.screens.admin

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import database.dao.audit.DocumentsGeneratedDao
import models.audit.DocumentGenerated
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import java.time.LocalDateTime
import java.util.UUID
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import androidx.navigation.NavController
import localization.StringResourcesManager
import localization.StringResources

/**
 * UI tests for the AdminScreen component.
 * Tests the document display functionality.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AdminScreenTest : KoinTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var documentsGeneratedDao: DocumentsGeneratedDao
    private lateinit var navController: NavController
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: TestScope

    @BeforeEach
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        testScope = TestScope(testDispatcher)
        Dispatchers.setMain(testDispatcher)

        // Mock dependencies
        documentsGeneratedDao = mockk(relaxed = true)
        navController = mockk(relaxed = true)

        // Set up Koin DI
        stopKoin() // Stop any existing Koin instance
        startKoin {
            modules(
                module {
                    single { documentsGeneratedDao }
                }
            )
        }
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun `test document display when documents exist`() = testScope.runTest {
        // Prepare test data
        val testDocuments = listOf(
            createTestDocument(documentType = "Type1", referenceNumber = "REF-001"),
            createTestDocument(documentType = "Type2", referenceNumber = "REF-002"),
            createTestDocument(documentType = "Type3", referenceNumber = "REF-003")
        )

        // Mock DAO behavior
        every { documentsGeneratedDao.getAllDocuments() } returns testDocuments

        // Set up the UI
        composeTestRule.setContent {
            AdminScreen(navController = navController)
        }

        // Find and click the documents button
        composeTestRule.onNodeWithText(StringResourcesManager.getCurrentStringResources().generatedDocuments)
            .performClick()

        // Wait for the UI to update
        composeTestRule.waitForIdle()

        // Verify that the documents are displayed
        composeTestRule.onNodeWithText("Document Type: Type1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reference: REF-001").assertIsDisplayed()
        composeTestRule.onNodeWithText("Document Type: Type2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reference: REF-002").assertIsDisplayed()
        composeTestRule.onNodeWithText("Document Type: Type3").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reference: REF-003").assertIsDisplayed()

        // Verify that "No documents found" is not displayed
        composeTestRule.onNodeWithText("No documents found").assertDoesNotExist()
    }

    @Test
    fun `test document display when no documents exist`() = testScope.runTest {
        // Mock DAO behavior to return empty list
        every { documentsGeneratedDao.getAllDocuments() } returns emptyList()

        // Set up the UI
        composeTestRule.setContent {
            AdminScreen(navController = navController)
        }

        // Find and click the documents button
        composeTestRule.onNodeWithText(StringResourcesManager.getCurrentStringResources().generatedDocuments)
            .performClick()

        // Wait for the UI to update
        composeTestRule.waitForIdle()

        // Verify that "No documents found" is displayed
        composeTestRule.onNodeWithText("No documents found").assertIsDisplayed()
    }

    @Test
    fun `test document display with empty document type`() = testScope.runTest {
        // Prepare test data with empty document type
        val testDocuments = listOf(
            createTestDocument(documentType = "", referenceNumber = "REF-001")
        )

        // Mock DAO behavior
        every { documentsGeneratedDao.getAllDocuments() } returns testDocuments

        // Set up the UI
        composeTestRule.setContent {
            AdminScreen(navController = navController)
        }

        // Find and click the documents button
        composeTestRule.onNodeWithText(StringResourcesManager.getCurrentStringResources().generatedDocuments)
            .performClick()

        // Wait for the UI to update
        composeTestRule.waitForIdle()

        // Verify that the document is displayed despite having an empty type
        composeTestRule.onNodeWithText("Document Type: ").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reference: REF-001").assertIsDisplayed()

        // Verify that "No documents found" is not displayed
        composeTestRule.onNodeWithText("No documents found").assertDoesNotExist()
    }

    private fun createTestDocument(
        id: UUID = UUID.randomUUID(),
        documentType: String = "Test Document",
        referenceNumber: String = "REF-${UUID.randomUUID()}",
        generatedAt: LocalDateTime = LocalDateTime.now(),
        generatedBy: UUID = UUID.randomUUID(),
        relatedEntityId: UUID = UUID.randomUUID(),
        relatedEntityType: String = "Test Entity",
        verificationCode: String? = "VC-123",
        hash: String? = "hash-value",
        filePath: String? = "/path/to/document.pdf"
    ): DocumentGenerated {
        return DocumentGenerated(
            id = id,
            documentType = documentType,
            referenceNumber = referenceNumber,
            generatedAt = generatedAt,
            generatedBy = generatedBy,
            relatedEntityId = relatedEntityId,
            relatedEntityType = relatedEntityType,
            verificationCode = verificationCode,
            hash = hash,
            filePath = filePath
        )
    }
}

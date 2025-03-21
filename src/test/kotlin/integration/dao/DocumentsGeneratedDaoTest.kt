package integration.dao

import database.TestTransactionProvider
import database.TransactionProvider
import database.dao.audit.DocumentsGeneratedDao
import database.dao.audit.DocumentsGeneratedDaoImpl
import database.schema.audit.DocumentsGenerated
import models.audit.DocumentGenerated
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*
import kotlin.io.createTempFile

/**
 * Integration tests for DocumentsGeneratedDao implementation.
 * Tests CRUD operations and document retrieval functionality.
 *
 * Uses an in-memory SQLite database that is recreated for each test.
 */
class DocumentsGeneratedDaoTest {
    private lateinit var testTransactionProvider: TestTransactionProvider
    private lateinit var documentsGeneratedDao: DocumentsGeneratedDao

    companion object {
        private lateinit var db: Database
        private val dbFile = createTempFile("test_db", ".db")

        @BeforeAll
        @JvmStatic
        fun setupClass() {
            println("[DEBUG_LOG] Setting up test class")
            println("[DEBUG_LOG] Using test database at: ${dbFile.absolutePath}")

            // Create a single database connection for all tests
            db = Database.connect(
                url = "jdbc:sqlite:${dbFile.absolutePath}",
                driver = "org.sqlite.JDBC"
            )

            // Initialize schema once for all tests
            transaction(db) {
                SchemaUtils.create(DocumentsGenerated)
            }
        }

        @AfterAll
        @JvmStatic
        fun tearDownClass() {
            println("[DEBUG_LOG] Cleaning up test class")
            dbFile.delete()
            println("[DEBUG_LOG] Test database deleted")
        }
    }

    @BeforeEach
    fun setup() {
        println("[DEBUG_LOG] Setting up test")

        // Clear all data before each test
        transaction(db) {
            SchemaUtils.drop(DocumentsGenerated)
            SchemaUtils.create(DocumentsGenerated)
        }

        // Initialize the DAO with the TestTransactionProvider
        testTransactionProvider = TestTransactionProvider(db)
        documentsGeneratedDao = DocumentsGeneratedDaoImpl(testTransactionProvider)
    }

    @Test
    fun testCreateDocument() {
        val document = createTestDocument()
        val createdDocument = documentsGeneratedDao.create(document)

        assertEquals(document.id, createdDocument.id)
        assertEquals(document.documentType, createdDocument.documentType)
        assertEquals(document.referenceNumber, createdDocument.referenceNumber)
    }

    @Test
    fun testGetDocumentById() {
        val document = createTestDocument()
        documentsGeneratedDao.create(document)

        val retrievedDocument = documentsGeneratedDao.getById(document.id)
        assertNotNull(retrievedDocument)
        assertEquals(document.id, retrievedDocument?.id)
        assertEquals(document.documentType, retrievedDocument?.documentType)
        assertEquals(document.referenceNumber, retrievedDocument?.referenceNumber)
    }

    @Test
    fun testGetByDocumentType() {
        val document1 = createTestDocument(documentType = "Type1")
        val document2 = createTestDocument(documentType = "Type2")
        val document3 = createTestDocument(documentType = "Type1")

        documentsGeneratedDao.create(document1)
        documentsGeneratedDao.create(document2)
        documentsGeneratedDao.create(document3)

        val type1Documents = documentsGeneratedDao.getByDocumentType("Type1")
        assertEquals(2, type1Documents.size)
        assertTrue(type1Documents.any { it.id == document1.id })
        assertTrue(type1Documents.any { it.id == document3.id })

        val type2Documents = documentsGeneratedDao.getByDocumentType("Type2")
        assertEquals(1, type2Documents.size)
        assertEquals(document2.id, type2Documents[0].id)
    }

    @Test
    fun testGetAllDocuments() {
        val document1 = createTestDocument(documentType = "Type1")
        val document2 = createTestDocument(documentType = "Type2")
        val document3 = createTestDocument(documentType = "Type3")

        documentsGeneratedDao.create(document1)
        documentsGeneratedDao.create(document2)
        documentsGeneratedDao.create(document3)

        val allDocuments = documentsGeneratedDao.getAllDocuments()
        assertEquals(3, allDocuments.size)
        assertTrue(allDocuments.any { it.id == document1.id })
        assertTrue(allDocuments.any { it.id == document2.id })
        assertTrue(allDocuments.any { it.id == document3.id })
    }

    @Test
    fun testUpdateDocument() {
        val document = createTestDocument()
        documentsGeneratedDao.create(document)

        val updatedDocument = document.copy(
            documentType = "Updated Type",
            referenceNumber = "Updated-Ref-123"
        )

        val result = documentsGeneratedDao.update(updatedDocument)
        assertEquals(updatedDocument.documentType, result.documentType)
        assertEquals(updatedDocument.referenceNumber, result.referenceNumber)

        val retrievedDocument = documentsGeneratedDao.getById(document.id)
        assertEquals("Updated Type", retrievedDocument?.documentType)
        assertEquals("Updated-Ref-123", retrievedDocument?.referenceNumber)
    }

    @Test
    fun testDeleteDocument() {
        val document = createTestDocument()
        documentsGeneratedDao.create(document)

        val deleteResult = documentsGeneratedDao.delete(document.id)
        assertTrue(deleteResult)

        val retrievedDocument = documentsGeneratedDao.getById(document.id)
        assertNull(retrievedDocument)
    }

    @Test
    fun testGetByReferenceNumber() {
        val document = createTestDocument(referenceNumber = "REF-123-456")
        documentsGeneratedDao.create(document)

        val retrievedDocument = documentsGeneratedDao.getByReferenceNumber("REF-123-456")
        assertNotNull(retrievedDocument)
        assertEquals(document.id, retrievedDocument?.id)
    }

    @Test
    fun testGetByRelatedEntity() {
        val entityId = UUID.randomUUID()
        val document1 = createTestDocument(relatedEntityId = entityId, relatedEntityType = "Resident")
        val document2 = createTestDocument(relatedEntityId = entityId, relatedEntityType = "Resident")
        val document3 = createTestDocument(relatedEntityType = "Residence")

        documentsGeneratedDao.create(document1)
        documentsGeneratedDao.create(document2)
        documentsGeneratedDao.create(document3)

        val residentDocuments = documentsGeneratedDao.getByRelatedEntity(entityId, "Resident")
        assertEquals(2, residentDocuments.size)
        assertTrue(residentDocuments.any { it.id == document1.id })
        assertTrue(residentDocuments.any { it.id == document2.id })
    }

    @Test
    fun testEmptyDocumentType() {
        val document = createTestDocument(documentType = "")
        documentsGeneratedDao.create(document)

        val emptyTypeDocuments = documentsGeneratedDao.getByDocumentType("")
        assertEquals(1, emptyTypeDocuments.size)
        assertEquals(document.id, emptyTypeDocuments[0].id)

        val allDocuments = documentsGeneratedDao.getAllDocuments()
        assertEquals(1, allDocuments.size)
        assertEquals(document.id, allDocuments[0].id)
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
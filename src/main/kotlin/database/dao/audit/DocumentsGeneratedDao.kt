package database.dao.audit

import database.schema.audit.DocumentsGenerated
import models.audit.DocumentGenerated
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalDateTime
import java.util.UUID

/**
 * Interface for the DocumentsGenerated Data Access Object.
 */
interface DocumentsGeneratedDao {
    fun create(document: DocumentGenerated): DocumentGenerated
    fun getById(id: UUID): DocumentGenerated?
    fun getByReferenceNumber(referenceNumber: String): DocumentGenerated?
    fun getByDocumentType(documentType: String): List<DocumentGenerated>
    fun getByRelatedEntity(relatedEntityId: UUID, relatedEntityType: String): List<DocumentGenerated>
    fun update(document: DocumentGenerated): DocumentGenerated
    fun delete(id: UUID): Boolean
}

/**
 * Implementation of the DocumentsGeneratedDao interface.
 * Provides CRUD operations for generated documents.
 */
class DocumentsGeneratedDaoImpl : DocumentsGeneratedDao {
    /**
     * Creates a new document record in the database.
     *
     * @param document The document to create
     * @return The created document
     */
    override fun create(document: DocumentGenerated): DocumentGenerated = transaction {
        DocumentsGenerated.insert {
            it[id] = document.id
            it[documentType] = document.documentType
            it[referenceNumber] = document.referenceNumber
            it[generatedAt] = document.generatedAt
            it[generatedBy] = document.generatedBy
            it[relatedEntityId] = document.relatedEntityId
            it[relatedEntityType] = document.relatedEntityType
            it[verificationCode] = document.verificationCode
            it[hash] = document.hash
            it[filePath] = document.filePath
        }
        document
    }

    /**
     * Retrieves a document by its ID.
     *
     * @param id The ID of the document to retrieve
     * @return The document, or null if not found
     */
    override fun getById(id: UUID): DocumentGenerated? = transaction {
        DocumentsGenerated.selectAll()
            .where { DocumentsGenerated.id eq id }
            .limit(1)
            .map { it.toDocumentGenerated() }
            .singleOrNull()
    }

    /**
     * Retrieves a document by its reference number.
     *
     * @param referenceNumber The reference number of the document to retrieve
     * @return The document, or null if not found
     */
    override fun getByReferenceNumber(referenceNumber: String): DocumentGenerated? = transaction {
        DocumentsGenerated.selectAll()
            .where { DocumentsGenerated.referenceNumber eq referenceNumber }
            .limit(1)
            .map { it.toDocumentGenerated() }
            .singleOrNull()
    }

    /**
     * Retrieves all documents of a specific type.
     *
     * @param documentType The type of documents to retrieve
     * @return A list of documents of the specified type
     */
    override fun getByDocumentType(documentType: String): List<DocumentGenerated> = transaction {
        DocumentsGenerated.selectAll()
            .where { DocumentsGenerated.documentType eq documentType }
            .map { it.toDocumentGenerated() }
    }

    /**
     * Retrieves all documents related to a specific entity.
     *
     * @param relatedEntityId The ID of the related entity
     * @param relatedEntityType The type of the related entity
     * @return A list of documents related to the specified entity
     */
    override fun getByRelatedEntity(relatedEntityId: UUID, relatedEntityType: String): List<DocumentGenerated> = transaction {
        DocumentsGenerated.selectAll()
            .where { 
                (DocumentsGenerated.relatedEntityId eq relatedEntityId) and 
                (DocumentsGenerated.relatedEntityType eq relatedEntityType) 
            }
            .map { it.toDocumentGenerated() }
    }

    /**
     * Updates an existing document in the database.
     *
     * @param document The document to update
     * @return The updated document
     */
    override fun update(document: DocumentGenerated): DocumentGenerated = transaction {
        DocumentsGenerated.update({ DocumentsGenerated.id eq document.id }) {
            it[documentType] = document.documentType
            it[referenceNumber] = document.referenceNumber
            it[generatedAt] = document.generatedAt
            it[generatedBy] = document.generatedBy
            it[relatedEntityId] = document.relatedEntityId
            it[relatedEntityType] = document.relatedEntityType
            it[verificationCode] = document.verificationCode
            it[hash] = document.hash
            it[filePath] = document.filePath
        }
        document
    }

    /**
     * Deletes a document from the database.
     *
     * @param id The ID of the document to delete
     * @return true if the document was deleted, false otherwise
     */
    override fun delete(id: UUID): Boolean = transaction {
        DocumentsGenerated.deleteWhere { DocumentsGenerated.id eq id } > 0
    }

    /**
     * Converts a ResultRow to a DocumentGenerated object.
     */
    private fun ResultRow.toDocumentGenerated(): DocumentGenerated {
        val entityId = this[DocumentsGenerated.id] as EntityID<UUID>
        return DocumentGenerated(
            id = entityId.value,
            documentType = this[DocumentsGenerated.documentType],
            referenceNumber = this[DocumentsGenerated.referenceNumber],
            generatedAt = this[DocumentsGenerated.generatedAt],
            generatedBy = this[DocumentsGenerated.generatedBy],
            relatedEntityId = this[DocumentsGenerated.relatedEntityId],
            relatedEntityType = this[DocumentsGenerated.relatedEntityType],
            verificationCode = this[DocumentsGenerated.verificationCode],
            hash = this[DocumentsGenerated.hash],
            filePath = this[DocumentsGenerated.filePath]
        )
    }
}
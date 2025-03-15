package database.dao.audit

import models.audit.DocumentGenerated
import models.audit.ProofOfAddressDocument
import models.domain.Address
import models.domain.Resident
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID

/**
 * Interface for the ProofOfAddress Data Access Object.
 * Provides specialized operations for proof of address documents.
 */
interface ProofOfAddressDao {
    /**
     * Creates a new proof of address document.
     *
     * @param document The proof of address document to create
     * @return The created proof of address document
     */
    fun create(document: ProofOfAddressDocument): ProofOfAddressDocument
    
    /**
     * Retrieves a proof of address document by its ID.
     *
     * @param id The ID of the document to retrieve
     * @return The document if found, null otherwise
     */
    fun getById(id: UUID): ProofOfAddressDocument?
    
    /**
     * Retrieves a proof of address document by its reference number.
     *
     * @param referenceNumber The reference number of the document to retrieve
     * @return The document if found, null otherwise
     */
    fun getByReferenceNumber(referenceNumber: String): ProofOfAddressDocument?
    
    /**
     * Retrieves all proof of address documents for a resident.
     *
     * @param residentId The ID of the resident
     * @return A list of proof of address documents for the resident
     */
    fun getByResidentId(residentId: UUID): List<ProofOfAddressDocument>
    
    /**
     * Retrieves all proof of address documents for an address.
     *
     * @param addressId The ID of the address
     * @return A list of proof of address documents for the address
     */
    fun getByAddressId(addressId: UUID): List<ProofOfAddressDocument>
    
    /**
     * Updates an existing proof of address document.
     *
     * @param document The document to update
     * @return The updated document
     */
    fun update(document: ProofOfAddressDocument): ProofOfAddressDocument
    
    /**
     * Deletes a proof of address document.
     *
     * @param id The ID of the document to delete
     * @return true if the document was deleted, false otherwise
     */
    fun delete(id: UUID): Boolean
}

/**
 * Implementation of the ProofOfAddressDao interface.
 * Uses DocumentsGeneratedDao for base operations and adds specialized functionality.
 */
class ProofOfAddressDaoImpl : ProofOfAddressDao {
    private val documentsGeneratedDao: DocumentsGeneratedDao by inject(DocumentsGeneratedDao::class.java)
    
    override fun create(document: ProofOfAddressDocument): ProofOfAddressDocument {
        documentsGeneratedDao.create(document.document)
        return document
    }
    
    override fun getById(id: UUID): ProofOfAddressDocument? {
        val document = documentsGeneratedDao.getById(id) ?: return null
        return if (document.documentType == "ProofOfAddress") {
            // We need to find the addressId for this document
            // This would typically be stored in a separate table or in metadata
            // For now, we'll use a query to find it
            val addressId = findAddressIdForDocument(document)
            addressId?.let { ProofOfAddressDocument(document, it) }
        } else {
            null
        }
    }
    
    override fun getByReferenceNumber(referenceNumber: String): ProofOfAddressDocument? {
        val document = documentsGeneratedDao.getByReferenceNumber(referenceNumber) ?: return null
        return if (document.documentType == "ProofOfAddress") {
            val addressId = findAddressIdForDocument(document)
            addressId?.let { ProofOfAddressDocument(document, it) }
        } else {
            null
        }
    }
    
    override fun getByResidentId(residentId: UUID): List<ProofOfAddressDocument> {
        return documentsGeneratedDao.getByRelatedEntity(residentId, "Resident")
            .filter { it.documentType == "ProofOfAddress" }
            .mapNotNull { document -> 
                val addressId = findAddressIdForDocument(document)
                addressId?.let { ProofOfAddressDocument(document, it) }
            }
    }
    
    override fun getByAddressId(addressId: UUID): List<ProofOfAddressDocument> {
        // This would typically require a join or a separate table
        // For now, we'll filter all proof of address documents
        return documentsGeneratedDao.getByDocumentType("ProofOfAddress")
            .mapNotNull { document -> 
                val docAddressId = findAddressIdForDocument(document)
                if (docAddressId == addressId) {
                    ProofOfAddressDocument(document, addressId)
                } else {
                    null
                }
            }
    }
    
    override fun update(document: ProofOfAddressDocument): ProofOfAddressDocument {
        documentsGeneratedDao.update(document.document)
        return document
    }
    
    override fun delete(id: UUID): Boolean {
        return documentsGeneratedDao.delete(id)
    }
    
    /**
     * Helper method to find the address ID for a document.
     * In a real implementation, this would be stored in a separate table or in metadata.
     */
    private fun findAddressIdForDocument(document: DocumentGenerated): UUID? {
        // This is a placeholder implementation
        // In a real application, you would have a proper way to associate
        // the document with an address, such as a join table or metadata
        
        // For now, we'll return a random UUID
        // In a real implementation, you would query the database
        return UUID.randomUUID()
    }
}
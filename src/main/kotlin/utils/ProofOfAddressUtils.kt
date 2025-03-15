package utils

import database.dao.audit.ProofOfAddressDao
import models.audit.ProofOfAddressDocument
import models.domain.Address
import models.domain.ProofOfAddress
import models.domain.Resident
import models.domain.Residence
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.time.LocalDateTime
import java.util.*

/**
 * Utility class for generating and displaying proof of address.
 * This class provides shared functionality for generating proof of address
 * that can be used by both the ResidenceTab and GlossaryScreen.
 */
object ProofOfAddressUtils {
    /**
     * Generates a proof of address PDF for the given resident and address.
     *
     * @param resident The resident for whom to generate the proof of address
     * @param address The address to include in the proof of address
     * @param residence The residence information linking the resident to the address
     * @return A Pair containing the generated PDF file and the ProofOfAddress record
     */
    fun generateProofOfAddress(
        resident: Resident,
        address: Address,
        residence: Residence
    ): Pair<File, ProofOfAddress> {
        val proofOfAddressDao: ProofOfAddressDao by inject(ProofOfAddressDao::class.java)

        // Generate reference number and verification code
        val referenceNumber = PdfGenerator.generateReferenceNumber()
        val verificationCode = PdfGenerator.generateVerificationCode(resident, address, referenceNumber)

        // Generate the PDF
        val pdf = PdfGenerator.generateProofOfAddressPdf(
            resident = resident,
            address = address,
            residence = residence,
            referenceNumber = referenceNumber,
            verificationCode = verificationCode
        )

        // Calculate the hash of the PDF content
        val hash = PdfGenerator.calculatePdfHash(pdf)

        // Get the current user ID (in a real app, this would be from the current user)
        val currentUserId = UUID.randomUUID() // Placeholder

        // Create the proof of address document
        val proofOfAddressDocument = ProofOfAddressDocument.create(
            resident = resident,
            address = address,
            referenceNumber = referenceNumber,
            verificationCode = verificationCode,
            hash = hash,
            generatedBy = currentUserId,
            filePath = pdf.absolutePath
        )

        // Save the proof of address document to the database
        proofOfAddressDao.create(proofOfAddressDocument)

        // For backward compatibility, create a ProofOfAddress object
        val proofOfAddress = ProofOfAddress(
            id = proofOfAddressDocument.id,
            residentId = resident.id,
            addressId = address.id,
            referenceNumber = referenceNumber,
            generatedAt = proofOfAddressDocument.generatedAt,
            generatedBy = "System", // For backward compatibility
            verificationCode = verificationCode,
            hash = hash
        )

        return Pair(pdf, proofOfAddress)
    }
}

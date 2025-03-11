package utils

import androidx.compose.runtime.Composable
import database.dao.ProofOfAddressDao
import models.Address
import models.ProofOfAddress
import models.Resident
import models.Residence
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
        
        // Create the proof of address record
        val proofOfAddress = ProofOfAddress(
            id = UUID.randomUUID(),
            residentId = resident.id,
            addressId = address.id,
            referenceNumber = referenceNumber,
            generatedAt = LocalDateTime.now(),
            generatedBy = "System", // In a real app, this would be the current user
            verificationCode = verificationCode,
            hash = hash
        )
        
        // Save the proof of address record to the database
        proofOfAddressDao.create(proofOfAddress)
        
        return Pair(pdf, proofOfAddress)
    }
}
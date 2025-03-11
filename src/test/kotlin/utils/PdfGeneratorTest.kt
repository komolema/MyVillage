package utils

import models.Address
import models.Resident
import models.Residence
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.time.LocalDate
import java.util.*

class PdfGeneratorTest {
    private lateinit var resident: Resident
    private lateinit var address: Address
    private lateinit var residence: Residence
    private var generatedFiles = mutableListOf<File>()

    @BeforeEach
    fun setUp() {
        // Create test data
        resident = Resident(
            id = UUID.randomUUID(),
            firstName = "John",
            lastName = "Doe",
            dob = LocalDate.of(1990, 1, 1),
            gender = "Male",
            idNumber = "1234567890123",
            phoneNumber = "0123456789",
            email = "john.doe@example.com"
        )

        address = Address(
            id = UUID.randomUUID(),
            line = "123 Test Street",
            houseNumber = "456",
            suburb = "Test Suburb",
            town = "Test Town",
            postalCode = "1234",
            geoCoordinates = "12.345, 67.890",
            landmark = "Near Test Park"
        )

        residence = Residence(
            id = UUID.randomUUID(),
            residentId = resident.id,
            addressId = address.id,
            occupationDate = LocalDate.of(2020, 1, 1)
        )
    }

    @AfterEach
    fun tearDown() {
        // Clean up generated files
        generatedFiles.forEach { file ->
            if (file.exists()) {
                file.delete()
            }
        }
        generatedFiles.clear()
    }

    @Test
    fun `test generateProofOfAddressPdf creates a file`() {
        // Generate a PDF
        val referenceNumber = PdfGenerator.generateReferenceNumber()
        val verificationCode = PdfGenerator.generateVerificationCode(resident, address, referenceNumber)
        val pdfFile = PdfGenerator.generateProofOfAddressPdf(
            resident = resident,
            address = address,
            residence = residence,
            referenceNumber = referenceNumber,
            verificationCode = verificationCode
        )
        generatedFiles.add(pdfFile)

        // Check that the file exists
        assertTrue(pdfFile.exists(), "PDF file should exist")
        assertTrue(pdfFile.length() > 0, "PDF file should not be empty")
    }

    @Test
    fun `test generated PDF is valid`() {
        // Generate a PDF
        val referenceNumber = PdfGenerator.generateReferenceNumber()
        val verificationCode = PdfGenerator.generateVerificationCode(resident, address, referenceNumber)
        val pdfFile = PdfGenerator.generateProofOfAddressPdf(
            resident = resident,
            address = address,
            residence = residence,
            referenceNumber = referenceNumber,
            verificationCode = verificationCode
        )
        generatedFiles.add(pdfFile)

        // Validate the PDF
        val isValid = PdfValidator.isValidPdf(pdfFile)
        assertTrue(isValid, "Generated file should be a valid PDF")
    }

    @Test
    fun `test PDF contains expected content`() {
        // Generate a PDF
        val referenceNumber = PdfGenerator.generateReferenceNumber()
        val verificationCode = PdfGenerator.generateVerificationCode(resident, address, referenceNumber)
        val pdfFile = PdfGenerator.generateProofOfAddressPdf(
            resident = resident,
            address = address,
            residence = residence,
            referenceNumber = referenceNumber,
            verificationCode = verificationCode
        )
        generatedFiles.add(pdfFile)

        // Extract text from the PDF
        val pdfText = PdfValidator.extractTextFromPdf(pdfFile)

        // Check that the PDF contains the expected content
        assertTrue(pdfText.contains("PROOF OF ADDRESS"), "PDF should contain title")
        assertTrue(pdfText.contains(referenceNumber), "PDF should contain reference number")
        assertTrue(pdfText.contains("${resident.firstName} ${resident.lastName}"), "PDF should contain resident name")
        assertTrue(pdfText.contains(resident.idNumber), "PDF should contain resident ID number")
        assertTrue(pdfText.contains("${address.houseNumber} ${address.line}"), "PDF should contain address line")
        assertTrue(pdfText.contains(address.suburb), "PDF should contain suburb")
        assertTrue(pdfText.contains(address.town), "PDF should contain town")
        assertTrue(pdfText.contains(address.postalCode), "PDF should contain postal code")
        assertTrue(pdfText.contains(verificationCode), "PDF should contain verification code")
    }

    @Test
    fun `test generateReferenceNumber creates unique references`() {
        val references = (1..100).map { PdfGenerator.generateReferenceNumber() }
        val uniqueReferences = references.toSet()
        assertEquals(references.size, uniqueReferences.size, "All reference numbers should be unique")
    }

    @Test
    fun `test generateVerificationCode is deterministic`() {
        val referenceNumber = "TEST-REF-123"
        val code1 = PdfGenerator.generateVerificationCode(resident, address, referenceNumber)
        val code2 = PdfGenerator.generateVerificationCode(resident, address, referenceNumber)
        assertEquals(code1, code2, "Verification codes should be deterministic for the same inputs")
    }

    @Test
    fun `test calculatePdfHash is deterministic`() {
        // Generate a PDF
        val referenceNumber = PdfGenerator.generateReferenceNumber()
        val verificationCode = PdfGenerator.generateVerificationCode(resident, address, referenceNumber)
        val pdfFile = PdfGenerator.generateProofOfAddressPdf(
            resident = resident,
            address = address,
            residence = residence,
            referenceNumber = referenceNumber,
            verificationCode = verificationCode
        )
        generatedFiles.add(pdfFile)

        // Calculate hash twice
        val hash1 = PdfGenerator.calculatePdfHash(pdfFile)
        val hash2 = PdfGenerator.calculatePdfHash(pdfFile)

        // Hashes should be the same
        assertEquals(hash1, hash2, "PDF hash should be deterministic")
    }
}

package utils

import models.domain.*
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType1Font
import java.io.File
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Utility class for generating PDF documents using Apache PDFBox.
 */
class PdfGenerator {
    companion object {
        /**
         * Sanitizes text for PDF output by replacing unsupported characters.
         * Specifically replaces tab characters (U+0009) with spaces as they are not supported in Helvetica font.
         *
         * @param text The text to sanitize
         * @return The sanitized text
         */
        private fun sanitizeTextForPdf(text: String): String {
            // Replace tab characters (U+0009) with spaces
            return text.replace('\u0009', ' ')
        }

        /**
         * Shows sanitized text in the PDF content stream.
         * This method sanitizes the text before showing it to prevent errors with unsupported characters.
         *
         * @param contentStream The PDF content stream
         * @param text The text to show
         */
        private fun showSanitizedText(contentStream: PDPageContentStream, text: String) {
            contentStream.showText(sanitizeTextForPdf(text))
        }
        /**
         * Generates a proof of address PDF for the given resident and address.
         *
         * @param resident The resident for whom to generate the proof of address
         * @param address The address to include in the proof of address
         * @param residence The residence information linking the resident to the address
         * @param referenceNumber The unique reference number for this proof of address
         * @param verificationCode The verification code for security purposes
         * @return The generated PDF file
         */
        fun generateProofOfAddressPdf(
            resident: Resident,
            address: Address,
            residence: Residence,
            referenceNumber: String,
            verificationCode: String
        ): File {
            // Create a temporary file for the PDF
            val tempFile = File.createTempFile("proof_of_address_${resident.id}", ".pdf")

            // Create a new PDF document
            PDDocument().use { document ->
                // Create a new page
                val page = PDPage(PDRectangle.A4)
                document.addPage(page)

                // Create a content stream for adding content to the page
                PDPageContentStream(document, page).use { contentStream ->
                    // Set font and font size
                    val titleFont = PDType1Font.HELVETICA_BOLD
                    val normalFont = PDType1Font.HELVETICA
                    val fontSize = 12f
                    val titleFontSize = 14f
                    val leading = 1.5f * fontSize

                    // Start text
                    contentStream.beginText()

                    // Set initial position (top left with margins)
                    contentStream.newLineAtOffset(50f, 750f)

                    // Title
                    contentStream.setFont(titleFont, titleFontSize)
                    showSanitizedText(contentStream, "PROOF OF ADDRESS")
                    contentStream.newLineAtOffset(0f, -leading)
                    showSanitizedText(contentStream, "================")
                    contentStream.newLineAtOffset(0f, -leading * 1.5f)

                    // Switch to normal font for the rest of the document
                    contentStream.setFont(normalFont, fontSize)

                    // Reference number and date
                    showSanitizedText(contentStream, "Reference Number: $referenceNumber")
                    contentStream.newLineAtOffset(0f, -leading)
                    showSanitizedText(contentStream, "Date: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}")
                    contentStream.newLineAtOffset(0f, -leading * 1.5f)

                    // Resident information
                    showSanitizedText(contentStream, "This is to certify that:")
                    contentStream.newLineAtOffset(0f, -leading * 1.5f)
                    showSanitizedText(contentStream, "${resident.firstName} ${resident.lastName}")
                    contentStream.newLineAtOffset(0f, -leading)
                    showSanitizedText(contentStream, "ID Number: ${resident.idNumber}")
                    contentStream.newLineAtOffset(0f, -leading * 1.5f)

                    // Address information
                    showSanitizedText(contentStream, "Resides at the following address:")
                    contentStream.newLineAtOffset(0f, -leading * 1.5f)
                    showSanitizedText(contentStream, "${address.houseNumber} ${address.line}")
                    contentStream.newLineAtOffset(0f, -leading)
                    showSanitizedText(contentStream, "${address.suburb}")
                    contentStream.newLineAtOffset(0f, -leading)
                    showSanitizedText(contentStream, "${address.town}")
                    contentStream.newLineAtOffset(0f, -leading)
                    showSanitizedText(contentStream, "${address.postalCode}")
                    contentStream.newLineAtOffset(0f, -leading * 1.5f)

                    // Occupation date
                    showSanitizedText(contentStream, "Since: ${residence.occupationDate}")
                    contentStream.newLineAtOffset(0f, -leading * 1.5f)

                    // Footer information
                    showSanitizedText(contentStream, "This proof of address was generated by MyVillage application.")
                    contentStream.newLineAtOffset(0f, -leading)
                    showSanitizedText(contentStream, "Verification Code: $verificationCode")
                    contentStream.newLineAtOffset(0f, -leading * 1.5f)
                    showSanitizedText(contentStream, "This document is electronically generated and does not require a signature.")

                    // End text
                    contentStream.endText()
                }

                // Save the document to the temporary file
                document.save(tempFile)
            }

            return tempFile
        }

        /**
         * Generates a unique reference number for a proof of address.
         *
         * @return A unique reference number
         */
        fun generateReferenceNumber(): String {
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
            val random = (1000..9999).random()
            val uniqueId = UUID.randomUUID().toString().substring(0, 8)
            return "POA-$timestamp-$random-$uniqueId"
        }

        /**
         * Generates a verification code for a proof of address.
         *
         * @param resident The resident for whom the proof of address is generated
         * @param address The address included in the proof of address
         * @param referenceNumber The reference number of the proof of address
         * @return A verification code
         */
        fun generateVerificationCode(resident: Resident, address: Address, referenceNumber: String): String {
            // Use a deterministic input string without including the current timestamp
            val input = "${resident.id}-${address.id}-$referenceNumber"
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(input.toByteArray())
            return digest.fold("") { str, it -> str + "%02x".format(it) }.substring(0, 16)
        }

        /**
         * Calculates a hash of the PDF content for integrity verification.
         *
         * @param file The PDF file
         * @return A hash of the PDF content
         */
        fun calculatePdfHash(file: File): String {
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(file.readBytes())
            return digest.fold("") { str, it -> str + "%02x".format(it) }
        }

        /**
         * Generates a PDF containing all information about a resident (full glossary).
         *
         * @param resident The resident for whom to generate the glossary
         * @param address The resident's address
         * @param dependants The resident's dependants
         * @param qualifications The resident's qualifications
         * @param employmentHistory The resident's employment history
         * @param outputFile The file to write the PDF to
         */
        fun generateGlossaryPdf(
            resident: Resident,
            address: Address?,
            dependants: List<Dependant>,
            qualifications: List<Qualification>,
            employmentHistory: List<Employment>,
            outputFile: File
        ) {
            // Create a new PDF document
            PDDocument().use { document ->
                // Create a new page
                val page = PDPage(PDRectangle.A4)
                document.addPage(page)

                // Create a content stream for adding content to the page
                PDPageContentStream(document, page).use { contentStream ->
                    // Set font and font size
                    val titleFont = PDType1Font.HELVETICA_BOLD
                    val subtitleFont = PDType1Font.HELVETICA_BOLD
                    val normalFont = PDType1Font.HELVETICA
                    val fontSize = 12f
                    val titleFontSize = 16f
                    val subtitleFontSize = 14f
                    val leading = 1.5f * fontSize

                    // Start text
                    contentStream.beginText()

                    // Set initial position (top left with margins)
                    contentStream.newLineAtOffset(50f, 750f)

                    // Title
                    contentStream.setFont(titleFont, titleFontSize)
                    showSanitizedText(contentStream, "RESIDENT INFORMATION")
                    contentStream.newLineAtOffset(0f, -leading * 1.5f)

                    // Personal Details
                    contentStream.setFont(subtitleFont, subtitleFontSize)
                    showSanitizedText(contentStream, "Personal Details")
                    contentStream.newLineAtOffset(0f, -leading * 1.2f)

                    contentStream.setFont(normalFont, fontSize)
                    showSanitizedText(contentStream, "Name: ${resident.firstName} ${resident.lastName}")
                    contentStream.newLineAtOffset(0f, -leading)
                    showSanitizedText(contentStream, "ID Number: ${resident.idNumber}")
                    contentStream.newLineAtOffset(0f, -leading)
                    showSanitizedText(contentStream, "Gender: ${resident.gender}")
                    contentStream.newLineAtOffset(0f, -leading)
                    showSanitizedText(contentStream, "Date of Birth: ${resident.dob.format(DateTimeFormatter.ISO_DATE)}")
                    contentStream.newLineAtOffset(0f, -leading * 1.5f)

                    // Address
                    contentStream.setFont(subtitleFont, subtitleFontSize)
                    showSanitizedText(contentStream, "Address")
                    contentStream.newLineAtOffset(0f, -leading * 1.2f)

                    contentStream.setFont(normalFont, fontSize)
                    if (address != null) {
                        showSanitizedText(contentStream, "Line: ${address.line}")
                        contentStream.newLineAtOffset(0f, -leading)
                        showSanitizedText(contentStream, "House Number: ${address.houseNumber}")
                        contentStream.newLineAtOffset(0f, -leading)
                        showSanitizedText(contentStream, "Suburb: ${address.suburb}")
                        contentStream.newLineAtOffset(0f, -leading)
                        showSanitizedText(contentStream, "Town: ${address.town}")
                        contentStream.newLineAtOffset(0f, -leading)
                        showSanitizedText(contentStream, "Postal Code: ${address.postalCode}")
                        contentStream.newLineAtOffset(0f, -leading)
                        address.landmark?.let { landmark ->
                            if (landmark.isNotEmpty()) {
                                showSanitizedText(contentStream, "Landmark: $landmark")
                                contentStream.newLineAtOffset(0f, -leading)
                            }
                        }
                        address.geoCoordinates?.let { coordinates ->
                            if (coordinates.isNotEmpty()) {
                                showSanitizedText(contentStream, "Geo Coordinates: $coordinates")
                                contentStream.newLineAtOffset(0f, -leading)
                            }
                        }
                    } else {
                        showSanitizedText(contentStream, "No address information available")
                        contentStream.newLineAtOffset(0f, -leading)
                    }
                    contentStream.newLineAtOffset(0f, -leading * 0.5f)

                    // Dependents
                    contentStream.setFont(subtitleFont, subtitleFontSize)
                    showSanitizedText(contentStream, "Dependents (${dependants.size})")
                    contentStream.newLineAtOffset(0f, -leading * 1.2f)

                    contentStream.setFont(normalFont, fontSize)
                    if (dependants.isEmpty()) {
                        showSanitizedText(contentStream, "No dependents")
                        contentStream.newLineAtOffset(0f, -leading)
                    } else {
                        dependants.forEach { dependant ->
                            showSanitizedText(contentStream, "Name: ${dependant.name} ${dependant.surname}")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "ID Number: ${dependant.idNumber}")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "Gender: ${dependant.gender}")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "---")
                            contentStream.newLineAtOffset(0f, -leading)
                        }
                    }
                    contentStream.newLineAtOffset(0f, -leading * 0.5f)

                    // Qualifications
                    contentStream.setFont(subtitleFont, subtitleFontSize)
                    showSanitizedText(contentStream, "Qualifications (${qualifications.size})")
                    contentStream.newLineAtOffset(0f, -leading * 1.2f)

                    contentStream.setFont(normalFont, fontSize)
                    if (qualifications.isEmpty()) {
                        showSanitizedText(contentStream, "No qualifications")
                        contentStream.newLineAtOffset(0f, -leading)
                    } else {
                        qualifications.forEach { qualification ->
                            showSanitizedText(contentStream, "Institution: ${qualification.institution}")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "Qualification: ${qualification.name}")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "NQF Level: ${qualification.nqfLevel}")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "Start Date: ${qualification.startDate.format(DateTimeFormatter.ISO_DATE)}")
                            contentStream.newLineAtOffset(0f, -leading)
                            qualification.endDate?.let { endDate ->
                                showSanitizedText(contentStream, "End Date: ${endDate.format(DateTimeFormatter.ISO_DATE)}")
                            } ?: showSanitizedText(contentStream, "End Date: Present")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "City: ${qualification.city}")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "---")
                            contentStream.newLineAtOffset(0f, -leading)
                        }
                    }
                    contentStream.newLineAtOffset(0f, -leading * 0.5f)

                    // Employment History
                    contentStream.setFont(subtitleFont, subtitleFontSize)
                    showSanitizedText(contentStream, "Employment History (${employmentHistory.size})")
                    contentStream.newLineAtOffset(0f, -leading * 1.2f)

                    contentStream.setFont(normalFont, fontSize)
                    if (employmentHistory.isEmpty()) {
                        showSanitizedText(contentStream, "No employment history")
                        contentStream.newLineAtOffset(0f, -leading)
                    } else {
                        employmentHistory.forEach { employment ->
                            showSanitizedText(contentStream, "Employer: ${employment.employer}")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "Role: ${employment.role}")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "Start Date: ${employment.startDate.format(DateTimeFormatter.ISO_DATE)}")
                            contentStream.newLineAtOffset(0f, -leading)
                            employment.endDate?.let { endDate ->
                                showSanitizedText(contentStream, "End Date: ${endDate.format(DateTimeFormatter.ISO_DATE)}")
                            } ?: showSanitizedText(contentStream, "End Date: Present")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "---")
                            contentStream.newLineAtOffset(0f, -leading)
                        }
                    }

                    // Footer
                    contentStream.newLineAtOffset(0f, -leading * 1.5f)
                    showSanitizedText(contentStream, "Generated on: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}")
                    contentStream.newLineAtOffset(0f, -leading)
                    showSanitizedText(contentStream, "This document is electronically generated and does not require a signature.")

                    // End text
                    contentStream.endText()
                }

                // Save the document to the output file
                document.save(outputFile)
            }
        }

        /**
         * Generates a PDF containing only the dependents list for a resident.
         *
         * @param resident The resident for whom to generate the dependents list
         * @param dependants The resident's dependants
         * @param outputFile The file to write the PDF to
         */
        fun generateDependentsPdf(
            resident: Resident,
            dependants: List<Dependant>,
            outputFile: File
        ) {
            // Create a new PDF document
            PDDocument().use { document ->
                // Create a new page
                val page = PDPage(PDRectangle.A4)
                document.addPage(page)

                // Create a content stream for adding content to the page
                PDPageContentStream(document, page).use { contentStream ->
                    // Set font and font size
                    val titleFont = PDType1Font.HELVETICA_BOLD
                    val subtitleFont = PDType1Font.HELVETICA_BOLD
                    val normalFont = PDType1Font.HELVETICA
                    val fontSize = 12f
                    val titleFontSize = 16f
                    val subtitleFontSize = 14f
                    val leading = 1.5f * fontSize

                    // Start text
                    contentStream.beginText()

                    // Set initial position (top left with margins)
                    contentStream.newLineAtOffset(50f, 750f)

                    // Title
                    contentStream.setFont(titleFont, titleFontSize)
                    showSanitizedText(contentStream, "DEPENDENTS LIST")
                    contentStream.newLineAtOffset(0f, -leading * 1.5f)

                    // Resident information
                    contentStream.setFont(normalFont, fontSize)
                    showSanitizedText(contentStream, "Resident: ${resident.firstName} ${resident.lastName}")
                    contentStream.newLineAtOffset(0f, -leading)
                    showSanitizedText(contentStream, "ID Number: ${resident.idNumber}")
                    contentStream.newLineAtOffset(0f, -leading * 1.5f)

                    // Dependents
                    contentStream.setFont(subtitleFont, subtitleFontSize)
                    showSanitizedText(contentStream, "Dependents (${dependants.size})")
                    contentStream.newLineAtOffset(0f, -leading * 1.2f)

                    contentStream.setFont(normalFont, fontSize)
                    if (dependants.isEmpty()) {
                        showSanitizedText(contentStream, "No dependents")
                        contentStream.newLineAtOffset(0f, -leading)
                    } else {
                        dependants.forEachIndexed { index, dependant ->
                            showSanitizedText(contentStream, "Dependent #${index + 1}")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "Name: ${dependant.name} ${dependant.surname}")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "ID Number: ${dependant.idNumber}")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "Gender: ${dependant.gender}")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "---")
                            contentStream.newLineAtOffset(0f, -leading)
                        }
                    }

                    // Footer
                    contentStream.newLineAtOffset(0f, -leading * 1.5f)
                    showSanitizedText(contentStream, "Generated on: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}")
                    contentStream.newLineAtOffset(0f, -leading)
                    showSanitizedText(contentStream, "This document is electronically generated and does not require a signature.")

                    // End text
                    contentStream.endText()
                }

                // Save the document to the output file
                document.save(outputFile)
            }
        }

        /**
         * Generates a PDF containing only the qualifications for a resident.
         *
         * @param resident The resident for whom to generate the qualifications list
         * @param qualifications The resident's qualifications
         * @param outputFile The file to write the PDF to
         */
        fun generateQualificationsPdf(
            resident: Resident,
            qualifications: List<Qualification>,
            outputFile: File
        ) {
            // Create a new PDF document
            PDDocument().use { document ->
                // Create a new page
                val page = PDPage(PDRectangle.A4)
                document.addPage(page)

                // Create a content stream for adding content to the page
                PDPageContentStream(document, page).use { contentStream ->
                    // Set font and font size
                    val titleFont = PDType1Font.HELVETICA_BOLD
                    val subtitleFont = PDType1Font.HELVETICA_BOLD
                    val normalFont = PDType1Font.HELVETICA
                    val fontSize = 12f
                    val titleFontSize = 16f
                    val subtitleFontSize = 14f
                    val leading = 1.5f * fontSize

                    // Start text
                    contentStream.beginText()

                    // Set initial position (top left with margins)
                    contentStream.newLineAtOffset(50f, 750f)

                    // Title
                    contentStream.setFont(titleFont, titleFontSize)
                    showSanitizedText(contentStream, "QUALIFICATIONS")
                    contentStream.newLineAtOffset(0f, -leading * 1.5f)

                    // Resident information
                    contentStream.setFont(normalFont, fontSize)
                    showSanitizedText(contentStream, "Resident: ${resident.firstName} ${resident.lastName}")
                    contentStream.newLineAtOffset(0f, -leading)
                    showSanitizedText(contentStream, "ID Number: ${resident.idNumber}")
                    contentStream.newLineAtOffset(0f, -leading * 1.5f)

                    // Qualifications
                    contentStream.setFont(subtitleFont, subtitleFontSize)
                    showSanitizedText(contentStream, "Qualifications (${qualifications.size})")
                    contentStream.newLineAtOffset(0f, -leading * 1.2f)

                    contentStream.setFont(normalFont, fontSize)
                    if (qualifications.isEmpty()) {
                        showSanitizedText(contentStream, "No qualifications")
                        contentStream.newLineAtOffset(0f, -leading)
                    } else {
                        qualifications.forEachIndexed { index, qualification ->
                            showSanitizedText(contentStream, "Qualification #${index + 1}")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "Institution: ${qualification.institution}")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "Qualification: ${qualification.name}")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "NQF Level: ${qualification.nqfLevel}")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "Start Date: ${qualification.startDate.format(DateTimeFormatter.ISO_DATE)}")
                            contentStream.newLineAtOffset(0f, -leading)
                            qualification.endDate?.let { endDate ->
                                showSanitizedText(contentStream, "End Date: ${endDate.format(DateTimeFormatter.ISO_DATE)}")
                            } ?: showSanitizedText(contentStream, "End Date: Present")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "City: ${qualification.city}")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "---")
                            contentStream.newLineAtOffset(0f, -leading)
                        }
                    }

                    // Footer
                    contentStream.newLineAtOffset(0f, -leading * 1.5f)
                    showSanitizedText(contentStream, "Generated on: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}")
                    contentStream.newLineAtOffset(0f, -leading)
                    showSanitizedText(contentStream, "This document is electronically generated and does not require a signature.")

                    // End text
                    contentStream.endText()
                }

                // Save the document to the output file
                document.save(outputFile)
            }
        }

        /**
         * Generates a PDF containing only the employment history for a resident.
         *
         * @param resident The resident for whom to generate the employment history
         * @param employmentHistory The resident's employment history
         * @param outputFile The file to write the PDF to
         */
        fun generateEmploymentHistoryPdf(
            resident: Resident,
            employmentHistory: List<Employment>,
            outputFile: File
        ) {
            // Create a new PDF document
            PDDocument().use { document ->
                // Create a new page
                val page = PDPage(PDRectangle.A4)
                document.addPage(page)

                // Create a content stream for adding content to the page
                PDPageContentStream(document, page).use { contentStream ->
                    // Set font and font size
                    val titleFont = PDType1Font.HELVETICA_BOLD
                    val subtitleFont = PDType1Font.HELVETICA_BOLD
                    val normalFont = PDType1Font.HELVETICA
                    val fontSize = 12f
                    val titleFontSize = 16f
                    val subtitleFontSize = 14f
                    val leading = 1.5f * fontSize

                    // Start text
                    contentStream.beginText()

                    // Set initial position (top left with margins)
                    contentStream.newLineAtOffset(50f, 750f)

                    // Title
                    contentStream.setFont(titleFont, titleFontSize)
                    showSanitizedText(contentStream, "EMPLOYMENT HISTORY")
                    contentStream.newLineAtOffset(0f, -leading * 1.5f)

                    // Resident information
                    contentStream.setFont(normalFont, fontSize)
                    showSanitizedText(contentStream, "Resident: ${resident.firstName} ${resident.lastName}")
                    contentStream.newLineAtOffset(0f, -leading)
                    showSanitizedText(contentStream, "ID Number: ${resident.idNumber}")
                    contentStream.newLineAtOffset(0f, -leading * 1.5f)

                    // Employment History
                    contentStream.setFont(subtitleFont, subtitleFontSize)
                    showSanitizedText(contentStream, "Employment History (${employmentHistory.size})")
                    contentStream.newLineAtOffset(0f, -leading * 1.2f)

                    contentStream.setFont(normalFont, fontSize)
                    if (employmentHistory.isEmpty()) {
                        showSanitizedText(contentStream, "No employment history")
                        contentStream.newLineAtOffset(0f, -leading)
                    } else {
                        employmentHistory.forEachIndexed { index, employment ->
                            showSanitizedText(contentStream, "Employment #${index + 1}")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "Employer: ${employment.employer}")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "Role: ${employment.role}")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "Start Date: ${employment.startDate.format(DateTimeFormatter.ISO_DATE)}")
                            contentStream.newLineAtOffset(0f, -leading)
                            employment.endDate?.let { endDate ->
                                showSanitizedText(contentStream, "End Date: ${endDate.format(DateTimeFormatter.ISO_DATE)}")
                            } ?: showSanitizedText(contentStream, "End Date: Present")
                            contentStream.newLineAtOffset(0f, -leading)
                            showSanitizedText(contentStream, "---")
                            contentStream.newLineAtOffset(0f, -leading)
                        }
                    }

                    // Footer
                    contentStream.newLineAtOffset(0f, -leading * 1.5f)
                    showSanitizedText(contentStream, "Generated on: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}")
                    contentStream.newLineAtOffset(0f, -leading)
                    showSanitizedText(contentStream, "This document is electronically generated and does not require a signature.")

                    // End text
                    contentStream.endText()
                }

                // Save the document to the output file
                document.save(outputFile)
            }
        }
    }
}

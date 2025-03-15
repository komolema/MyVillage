package utils

import models.domain.*
import java.io.File

/**
 * Utility class for generating PDF documents for the glossary screen.
 * This class provides methods for generating PDFs for the glossary, dependents list,
 * qualifications, and employment history.
 */
object GlossaryPdfUtils {
    /**
     * Generates a PDF containing all information about a resident (full glossary).
     *
     * @param resident The resident for whom to generate the glossary
     * @param address The resident's address
     * @param dependants The resident's dependants
     * @param qualifications The resident's qualifications
     * @param employmentHistory The resident's employment history
     * @return The generated PDF file
     */
    fun generateGlossaryPdf(
        resident: Resident,
        address: Address?,
        dependants: List<Dependant>,
        qualifications: List<Qualification>,
        employmentHistory: List<Employment>
    ): File {
        // Create a temporary file for the PDF
        val tempFile = File.createTempFile("glossary_${resident.id}", ".pdf")
        
        // Use PdfGenerator to create the PDF
        PdfGenerator.generateGlossaryPdf(
            resident = resident,
            address = address,
            dependants = dependants,
            qualifications = qualifications,
            employmentHistory = employmentHistory,
            outputFile = tempFile
        )
        
        return tempFile
    }
    
    /**
     * Generates a PDF containing only the dependents list for a resident.
     *
     * @param resident The resident for whom to generate the dependents list
     * @param dependants The resident's dependants
     * @return The generated PDF file
     */
    fun generateDependentsPdf(
        resident: Resident,
        dependants: List<Dependant>
    ): File {
        // Create a temporary file for the PDF
        val tempFile = File.createTempFile("dependents_${resident.id}", ".pdf")
        
        // Use PdfGenerator to create the PDF
        PdfGenerator.generateDependentsPdf(
            resident = resident,
            dependants = dependants,
            outputFile = tempFile
        )
        
        return tempFile
    }
    
    /**
     * Generates a PDF containing only the qualifications for a resident.
     *
     * @param resident The resident for whom to generate the qualifications list
     * @param qualifications The resident's qualifications
     * @return The generated PDF file
     */
    fun generateQualificationsPdf(
        resident: Resident,
        qualifications: List<Qualification>
    ): File {
        // Create a temporary file for the PDF
        val tempFile = File.createTempFile("qualifications_${resident.id}", ".pdf")
        
        // Use PdfGenerator to create the PDF
        PdfGenerator.generateQualificationsPdf(
            resident = resident,
            qualifications = qualifications,
            outputFile = tempFile
        )
        
        return tempFile
    }
    
    /**
     * Generates a PDF containing only the employment history for a resident.
     *
     * @param resident The resident for whom to generate the employment history
     * @param employmentHistory The resident's employment history
     * @return The generated PDF file
     */
    fun generateEmploymentHistoryPdf(
        resident: Resident,
        employmentHistory: List<Employment>
    ): File {
        // Create a temporary file for the PDF
        val tempFile = File.createTempFile("employment_${resident.id}", ".pdf")
        
        // Use PdfGenerator to create the PDF
        PdfGenerator.generateEmploymentHistoryPdf(
            resident = resident,
            employmentHistory = employmentHistory,
            outputFile = tempFile
        )
        
        return tempFile
    }
}
package utils

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File
import java.io.IOException

/**
 * Utility class for validating PDF files and extracting content.
 * Uses Apache PDFBox for robust PDF validation and text extraction.
 */
object PdfValidator {
    /**
     * Checks if a file is a valid PDF using PDFBox.
     * Attempts to load the file as a PDF document to validate it.
     *
     * @param file The file to check
     * @return true if the file is a valid PDF, false otherwise
     */
    fun isValidPdf(file: File): Boolean {
        if (!file.exists() || !file.isFile || file.length() < 5) {
            return false
        }

        return try {
            PDDocument.load(file).use { document ->
                // If we can load the document, it's a valid PDF
                true
            }
        } catch (e: IOException) {
            false
        } catch (e: Exception) {
            // Catch any other exceptions that might occur during PDF loading
            false
        }
    }

    /**
     * Extracts text content from a PDF file using PDFBox.
     *
     * @param file The PDF file to extract text from
     * @return The extracted text content
     * @throws IllegalArgumentException if the file is not a valid PDF
     * @throws IOException if there's an error reading the PDF
     */
    fun extractTextFromPdf(file: File): String {
        if (!isValidPdf(file)) {
            throw IllegalArgumentException("Not a valid PDF file: ${file.absolutePath}")
        }

        return try {
            PDDocument.load(file).use { document ->
                val stripper = PDFTextStripper()
                stripper.sortByPosition = true
                stripper.getText(document)
            }
        } catch (e: IOException) {
            throw IOException("Failed to extract text from PDF: ${e.message}", e)
        }
    }
}

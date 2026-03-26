package ie.universityofgalway.projecttrackingsystem.service.document;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

/**
 * Service for generating PDF documents from HTML strings using the OpenHTMLtoPDF library.
 * Converts rendered HTML to PDF byte arrays for invoice and document generation.
 */
@Service
public class PDFGenerator {

    /**
     * Generates a PDF from an HTML string using the OpenHTMLtoPDF library.
     * The HTML is rendered to a PDF and returned as a byte array.
     *
     * @param html the HTML content as a String
     * @return a byte array containing the generated PDF
     * @throws IllegalArgumentException if the HTML String is null or blank
     * @throws RuntimeException if PDF generation fails
     */
    public byte[] generatePdf(String html) {
        if (html == null || html.isBlank()) {
            throw new IllegalArgumentException("HTML content must not be null or blank");
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF from HTML: " + e.getMessage(), e);
        }
    }
}
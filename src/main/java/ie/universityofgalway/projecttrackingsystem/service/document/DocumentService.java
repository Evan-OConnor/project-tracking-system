package ie.universityofgalway.projecttrackingsystem.service.document;

import ie.universityofgalway.projecttrackingsystem.dto.document.InvoiceDocumentData;
import ie.universityofgalway.projecttrackingsystem.service.InvoiceService;
import ie.universityofgalway.projecttrackingsystem.dto.InvoiceDTO;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling document generation workflows.
 */
@Service
public class DocumentService {

    private final InvoiceService invoiceService;
    private final InvoiceDocumentMapper invoiceDocumentMapper;
    private final DocumentTemplateRenderer documentTemplateRenderer;
    private final PDFGenerator pdfGenerator;

    public DocumentService(InvoiceService invoiceService,
                           InvoiceDocumentMapper invoiceDocumentMapper,
                           DocumentTemplateRenderer documentTemplateRenderer,
                           PDFGenerator pdfGenerator) {
        this.invoiceService = invoiceService;
        this.invoiceDocumentMapper = invoiceDocumentMapper;
        this.documentTemplateRenderer = documentTemplateRenderer;
        this.pdfGenerator = pdfGenerator;
    }

    /**
     * Retrieves invoice data and maps it into an {@link InvoiceDocumentData}
     * object for document rendering.
     *
     * @param invoiceId the unique identifier of the invoice
     * @return a fully populated InvoiceDocumentData object
     * @throws IllegalArgumentException if no invoice exists for the given ID
     */
    public InvoiceDocumentData toInvoiceDocumentData(Long invoiceId) {
        InvoiceDTO invoiceDTO = invoiceService.getInvoiceById(invoiceId);

        if (invoiceDTO == null) {
            throw new IllegalArgumentException("No invoice found with id: " + invoiceId);
        }

        return invoiceDocumentMapper.toInvoiceDocumentData(invoiceDTO);
    }

    /**
     * Generates rendered HTML for an invoice by retrieving invoice data,
     * converting it to InvoiceDocumentData, and processing it through
     * the Thymeleaf template engine.
     *
     * @param invoiceId the unique identifier of the invoice
     * @return a rendered HTML string for the invoice
     * @throws IllegalArgumentException if no invoice exists for the given ID
     */
    public String generateInvoiceHtml(Long invoiceId) {
        InvoiceDocumentData invoiceData = toInvoiceDocumentData(invoiceId);
        return documentTemplateRenderer.render("invoice", "invoice", invoiceData);
    }

    /**
     * Generates a PDF for an invoice by rendering it as HTML and converting
     * it to PDF format using the OpenHTMLtoPDF library.
     *
     * @param invoiceId the unique identifier of the invoice
     * @return a byte array containing the generated PDF
     * @throws IllegalArgumentException if no invoice exists for the given ID
     * @throws RuntimeException if PDF generation fails
     */
    public byte[] generateInvoicePdf(Long invoiceId) {
        String html = generateInvoiceHtml(invoiceId);
        return pdfGenerator.generatePdf(html);
    }
}

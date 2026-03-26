package ie.universityofgalway.projecttrackingsystem.service.document;

import ie.universityofgalway.projecttrackingsystem.dto.document.InvoiceDocumentData;
import ie.universityofgalway.projecttrackingsystem.dto.document.ReceiptDocumentData;
import ie.universityofgalway.projecttrackingsystem.domain.core.Receipt;
import ie.universityofgalway.projecttrackingsystem.service.InvoiceService;
import ie.universityofgalway.projecttrackingsystem.service.ReceiptService;
import ie.universityofgalway.projecttrackingsystem.dto.InvoiceDTO;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling document generation workflows.
 */
@Service
public class DocumentService {

    private final InvoiceService invoiceService;
    private final InvoiceDocumentMapper invoiceDocumentMapper;
    private final ReceiptService receiptService;
    private final ReceiptDocumentMapper receiptDocumentMapper;
    private final DocumentTemplateRenderer documentTemplateRenderer;
    private final PDFGenerator pdfGenerator;

    public DocumentService(InvoiceService invoiceService,
                           InvoiceDocumentMapper invoiceDocumentMapper,
                           ReceiptService receiptService,
                           ReceiptDocumentMapper receiptDocumentMapper,
                           DocumentTemplateRenderer documentTemplateRenderer,
                           PDFGenerator pdfGenerator) {
        this.invoiceService = invoiceService;
        this.invoiceDocumentMapper = invoiceDocumentMapper;
        this.receiptService = receiptService;
        this.receiptDocumentMapper = receiptDocumentMapper;
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

        return invoiceDocumentMapper.toInvoiceDocumentData(invoiceDTO);
    }

    /**
     * Retrieves receipt and associated invoice data and maps them into a
     * {@link ReceiptDocumentData} object for document rendering.
     *
     * @param receiptId the unique identifier of the receipt
     * @return a fully populated ReceiptDocumentData object
     * @throws IllegalArgumentException if no receipt exists for the given ID or if the
     *         associated invoice cannot be found
     */
    public ReceiptDocumentData toReceiptDocumentData(Long receiptId) {
        Receipt receipt = receiptService.getById(receiptId);

        Long invoiceId = receipt.getInvoice().getId();
        InvoiceDTO invoiceDTO = invoiceService.getInvoiceById(invoiceId);

        return receiptDocumentMapper.toReceiptDocumentData(receipt, invoiceDTO);
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
        return documentTemplateRenderer.render("invoice/invoice", "invoice", invoiceData);
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

    /**
     * Generates rendered HTML for a receipt by retrieving receipt and invoice data,
     * converting it to ReceiptDocumentData, and processing it through
     * the Thymeleaf template engine.
     *
     * @param receiptId the unique identifier of the receipt
     * @return a rendered HTML string for the receipt
     * @throws IllegalArgumentException if no receipt exists for the given ID or if the
     *         associated invoice cannot be found
     */
    public String generateReceiptHtml(Long receiptId) {
        ReceiptDocumentData receiptData = toReceiptDocumentData(receiptId);
        return documentTemplateRenderer.render("receipts/receipt", "receipt", receiptData);
    }

    /**
     * Generates a PDF for a receipt by rendering it as HTML and converting
     * it to PDF format using the OpenHTMLtoPDF library.
     *
     * @param receiptId the unique identifier of the receipt
     * @return a byte array containing the generated PDF
     * @throws IllegalArgumentException if no receipt exists for the given ID or if the
     *         associated invoice cannot be found
     * @throws RuntimeException if PDF generation fails
     */
    public byte[] generateReceiptPdf(Long receiptId) {
        String html = generateReceiptHtml(receiptId);
        return pdfGenerator.generatePdf(html);
    }
}

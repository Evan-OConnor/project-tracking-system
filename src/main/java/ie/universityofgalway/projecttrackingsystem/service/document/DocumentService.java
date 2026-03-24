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

    public DocumentService(InvoiceService invoiceService, InvoiceDocumentMapper invoiceDocumentMapper) {
        this.invoiceService = invoiceService;
        this.invoiceDocumentMapper = invoiceDocumentMapper;
    }

    /**
     * Generates an invoice document for the given invoice ID.
     *
     * @param id the unique identifier of the invoice.
     */
    public void generateInvoice(Long id) {
        //TODO document generation process
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
}

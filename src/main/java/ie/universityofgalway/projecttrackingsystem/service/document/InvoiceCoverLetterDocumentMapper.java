package ie.universityofgalway.projecttrackingsystem.service.document;

import ie.universityofgalway.projecttrackingsystem.config.CompanyProperties;
import ie.universityofgalway.projecttrackingsystem.dto.InvoiceDTO;
import ie.universityofgalway.projecttrackingsystem.dto.document.InvoiceCoverLetterDocumentData;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Mapper responsible for transforming {@link InvoiceDTO} objects into
 * {@link InvoiceCoverLetterDocumentData} objects for invoice cover letter document generation.
 */
@Service
public class InvoiceCoverLetterDocumentMapper {

    private final CompanyProperties companyProperties;

    public InvoiceCoverLetterDocumentMapper(CompanyProperties companyProperties) {
        this.companyProperties = companyProperties;
    }

    /**
     * Transforms an InvoiceDTO object into an InvoiceCoverLetterDocumentData
     * object suitable for cover letter document rendering.
     *
     * @param invoiceDTO the invoice data transfer object
     * @return a fully populated InvoiceCoverLetterDocumentData instance
     * @throws IllegalArgumentException if invoiceDTO is null
     */
    public InvoiceCoverLetterDocumentData toInvoiceCoverLetterDocumentData(InvoiceDTO invoiceDTO) {

        if (invoiceDTO == null) {
            throw new IllegalArgumentException("InvoiceDTO must not be null");
        }

        // Extract company info from properties
        String companyName = companyProperties.getName();
        String companyAddress = companyProperties.getAddress();
        String companyPhone = companyProperties.getPhone();
        String companyEmail = companyProperties.getEmail();

        // Client details
        String clientName = invoiceDTO.getClientName();
        String clientAddress = invoiceDTO.getClientAddress();

        // Return cover letter document data
        return new InvoiceCoverLetterDocumentData(
                invoiceDTO.getInvoiceNumber(),
                invoiceDTO.getInvoiceDate(),
                invoiceDTO.getProjectName(),
                companyName,
                companyAddress,
                companyPhone,
                companyEmail,
                LocalDate.now(),
                clientName,
                clientAddress,
                invoiceDTO.getGrossTotal()
        );
    }
}

package ie.universityofgalway.projecttrackingsystem.service.document;

import ie.universityofgalway.projecttrackingsystem.config.CompanyProperties;
import ie.universityofgalway.projecttrackingsystem.dto.InvoiceDTO;
import ie.universityofgalway.projecttrackingsystem.dto.document.InvoiceDocumentData;
import ie.universityofgalway.projecttrackingsystem.dto.document.InvoiceLineItemData;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Mapper responsible for transforming {@link InvoiceDTO} objects into
 * {@link InvoiceDocumentData} objects for document generation.
 */
@Service
public class InvoiceDocumentMapper {

    private final CompanyProperties companyProperties;

    public InvoiceDocumentMapper(CompanyProperties companyProperties) {
        this.companyProperties = companyProperties;
    }

    /**
     * Transforms an InvoiceDTO object into an InvoiceDocumentData
     * object suitable for document rendering.
     *
     * @param invoiceDTO the invoice data transfer object
     * @return a fully populated InvoiceDocumentData instance
     * @throws IllegalArgumentException if invoiceDTO is null
     */
    public InvoiceDocumentData toInvoiceDocumentData(InvoiceDTO invoiceDTO) {

        if (invoiceDTO == null) {
            throw new IllegalArgumentException("InvoiceDTO must not be null");
        }

        // Extract company info from properties
        String companyName = companyProperties.getName();
        String companyAddress = companyProperties.getAddress();
        String companyPhone = companyProperties.getPhone();
        String companyEmail = companyProperties.getEmail();

        // Map line items from InvoiceDTO to document format
        List<InvoiceLineItemData> lineItems = invoiceDTO.getLineItems().stream()
                .map(li -> new InvoiceLineItemData(
                        li.getDescription(),
                        li.getQuantity(),
                        li.getUnitRate(),
                        li.getNetAmount()
                ))
                .toList();

        // Client details
        String clientName = invoiceDTO.getClientName();
        String clientAddress = invoiceDTO.getClientAddress();

        // VAT rate
        BigDecimal vatRatePercent = invoiceDTO.getVatRate();

        // Return document data with pre-calculated totals from InvoiceDTO
        return new InvoiceDocumentData(
                invoiceDTO.getInvoiceNumber(),
                invoiceDTO.getInvoiceDate(),
                invoiceDTO.getProjectName(),
                companyName,
                companyAddress,
                companyPhone,
                companyEmail,
                clientName,
                clientAddress,
                invoiceDTO.getSubtotal(),
                vatRatePercent,
                invoiceDTO.getVatAmount(),
                invoiceDTO.getGrossTotal(),
                lineItems
        );
    }
}

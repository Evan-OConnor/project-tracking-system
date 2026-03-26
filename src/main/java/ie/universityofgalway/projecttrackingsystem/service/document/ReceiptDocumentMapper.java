package ie.universityofgalway.projecttrackingsystem.service.document;

import ie.universityofgalway.projecttrackingsystem.config.CompanyProperties;
import ie.universityofgalway.projecttrackingsystem.domain.core.Receipt;
import ie.universityofgalway.projecttrackingsystem.dto.InvoiceDTO;
import ie.universityofgalway.projecttrackingsystem.dto.document.ReceiptDocumentData;
import org.springframework.stereotype.Service;

/**
 * Mapper responsible for transforming {@link Receipt} entities and
 * {@link InvoiceDTO} objects into {@link ReceiptDocumentData} objects for document generation.
 */
@Service
public class ReceiptDocumentMapper {

    private final CompanyProperties companyProperties;

    public ReceiptDocumentMapper(CompanyProperties companyProperties) {
        this.companyProperties = companyProperties;
    }

    /**
     * Transforms a Receipt entity and InvoiceDTO object into a ReceiptDocumentData
     * object suitable for receipt PDF rendering.
     *
     * Financial calculations are sourced from InvoiceDTO, which contains pre-calculated
     * invoice totals, VAT details, and client information. Receipt provides payment-specific
     * data such as receipt number, date, discount, and payment method.
     *
     * @param receipt the receipt entity containing payment information
     * @param invoiceDTO the invoice data transfer object containing financial and client details
     * @return a fully populated ReceiptDocumentData instance
     * @throws IllegalArgumentException if either receipt or invoiceDTO is null
     */
    public ReceiptDocumentData toReceiptDocumentData(Receipt receipt, InvoiceDTO invoiceDTO) {

        if (receipt == null) {
            throw new IllegalArgumentException("Receipt must not be null");
        }

        if (invoiceDTO == null) {
            throw new IllegalArgumentException("InvoiceDTO must not be null");
        }

        // Extract company info from properties
        String companyName = companyProperties.getName();
        String companyAddress = companyProperties.getAddress();
        String companyPhone = companyProperties.getPhone();
        String companyEmail = companyProperties.getEmail();

        // Return document data with payment info from Receipt and financial data from InvoiceDTO
        return new ReceiptDocumentData(
                receipt.getReceiptNumber(),
                receipt.getDateReceived(),
                invoiceDTO.getInvoiceNumber(),
                invoiceDTO.getInvoiceDate(),
                invoiceDTO.getProjectName(),
                companyName,
                companyAddress,
                companyPhone,
                companyEmail,
                invoiceDTO.getClientName(),
                invoiceDTO.getClientAddress(),
                invoiceDTO.getSubtotal(),
                invoiceDTO.getVatRate(),
                invoiceDTO.getVatAmount(),
                invoiceDTO.getGrossTotal(),
                receipt.getDiscount(),
                receipt.getAmountPaid(),
                receipt.getPaymentMethod()
        );
    }
}




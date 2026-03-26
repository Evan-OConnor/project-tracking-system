package ie.universityofgalway.projecttrackingsystem.dto.document;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for rendering invoice cover letter PDFs.
 * Contains all data required by the invoice cover letter template.
 */
public class InvoiceCoverLetterDocumentData {

    // Invoice Metadata
    private final String invoiceNumber;
    private final LocalDate invoiceDate;
    private final String projectTitle;

    // Company Details
    private final String companyName;
    private final String companyAddress;
    private final String companyPhone;
    private final String companyEmail;

    // Letter Details
    private final LocalDate letterDate;

    // Client Details
    private final String clientName;
    private final String clientAddress;

    // Invoice Total
    private final BigDecimal totalIncVat;

    // Constructor

    public InvoiceCoverLetterDocumentData(
            String invoiceNumber,
            LocalDate invoiceDate,
            String projectTitle,
            String companyName,
            String companyAddress,
            String companyPhone,
            String companyEmail,
            LocalDate letterDate,
            String clientName,
            String clientAddress,
            BigDecimal totalIncVat) {

        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyEmail = companyEmail;
        this.companyPhone = companyPhone;
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.projectTitle = projectTitle;
        this.letterDate = letterDate;
        this.clientName = clientName;
        this.clientAddress = clientAddress;
        this.totalIncVat = totalIncVat;
    }

    // Getters

    public String getInvoiceNumber() { return invoiceNumber; }

    public LocalDate getInvoiceDate() { return invoiceDate; }
    public String getProjectTitle() { return projectTitle; }
    public LocalDate getLetterDate() { return letterDate; }

    public String getCompanyName() { return companyName; }
    public String getCompanyAddress() { return companyAddress; }
    public String getCompanyEmail() { return companyEmail; }
    public String getCompanyPhone() { return companyPhone; }

    public String getClientName() { return clientName; }
    public String getClientAddress() { return clientAddress; }

    public BigDecimal getTotalIncVat() { return totalIncVat; }
}

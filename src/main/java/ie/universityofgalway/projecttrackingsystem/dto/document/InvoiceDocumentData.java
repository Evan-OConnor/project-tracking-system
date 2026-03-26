package ie.universityofgalway.projecttrackingsystem.dto.document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for rendering invoice PDFs.
 * Contains all data required by the invoice template.
 */
public class InvoiceDocumentData {

    // Invoice Metadata
    private final String invoiceNumber;
    private final LocalDate invoiceDate;
    private final String projectTitle;

    // Company Details
    private final String companyName;
    private final String companyAddress;
    private final String companyPhone;
    private final String companyEmail;

    // Client Details
    private final String clientName;
    private final String clientAddress;

    // Totals
    private final BigDecimal totalExVat;
    private final BigDecimal vatRate;
    private final BigDecimal vatAmount;
    private final BigDecimal totalIncVat;

    // Line Items
    private final List<InvoiceLineItemData> lineItems;

    // Constructor

    public InvoiceDocumentData(
            String invoiceNumber,
            LocalDate invoiceDate,
            String projectTitle,
            String companyName,
            String companyAddress,
            String companyPhone,
            String companyEmail,
            String clientName,
            String clientAddress,
            BigDecimal totalExVat,
            BigDecimal vatRate,
            BigDecimal vatAmount,
            BigDecimal totalIncVat,
            List<InvoiceLineItemData> lineItems) {

        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.projectTitle = projectTitle;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyPhone = companyPhone;
        this.companyEmail = companyEmail;
        this.clientName = clientName;
        this.clientAddress = clientAddress;
        this.totalExVat = totalExVat;
        this.vatRate = vatRate;
        this.vatAmount = vatAmount;
        this.totalIncVat = totalIncVat;
        this.lineItems = List.copyOf(lineItems);
    }

    // Getters

    public String getInvoiceNumber() { return invoiceNumber; }
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public String getProjectTitle() { return projectTitle; }

    public String getCompanyName() { return companyName; }
    public String getCompanyAddress() { return companyAddress; }
    public String getCompanyPhone() { return companyPhone; }
    public String getCompanyEmail() { return companyEmail; }

    public String getClientName() { return clientName; }
    public String getClientAddress() { return clientAddress; }

    public BigDecimal getTotalExVat() { return totalExVat; }
    public BigDecimal getVatRate() { return vatRate; }
    public BigDecimal getVatAmount() { return vatAmount; }
    public BigDecimal getTotalIncVat() { return totalIncVat; }

    public List<InvoiceLineItemData> getLineItems() { return lineItems; }
}
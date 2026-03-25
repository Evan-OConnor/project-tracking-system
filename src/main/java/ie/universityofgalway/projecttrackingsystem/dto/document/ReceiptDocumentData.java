package ie.universityofgalway.projecttrackingsystem.dto.document;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for rendering receipt PDFs.
 * Contains all data required by the receipt template.
 */
public class ReceiptDocumentData {

    // Receipt Metadata
    private final String receiptNumber;
    private final LocalDate receiptDate;

    // Invoice Reference
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

    // Payment Summary
    private final BigDecimal originalInvoiceTotal;
    private final BigDecimal discount;
    private final BigDecimal amountPaid;
    private final String paymentMethod;

    // Constructor

    public ReceiptDocumentData(
            String receiptNumber,
            LocalDate receiptDate,
            String invoiceNumber,
            LocalDate invoiceDate,
            String projectTitle,
            String companyName,
            String companyAddress,
            String companyPhone,
            String companyEmail,
            String clientName,
            String clientAddress,
            BigDecimal originalInvoiceTotal,
            BigDecimal discount,
            BigDecimal amountPaid,
            String paymentMethod) {

        this.receiptNumber = receiptNumber;
        this.receiptDate = receiptDate;
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.projectTitle = projectTitle;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyPhone = companyPhone;
        this.companyEmail = companyEmail;
        this.clientName = clientName;
        this.clientAddress = clientAddress;
        this.originalInvoiceTotal = originalInvoiceTotal;
        this.discount = discount;
        this.amountPaid = amountPaid;
        this.paymentMethod = paymentMethod;
    }

    // Getters

    public String getReceiptNumber() { return receiptNumber; }
    public LocalDate getReceiptDate() { return receiptDate; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public String getProjectTitle() { return projectTitle; }

    public String getCompanyName() { return companyName; }
    public String getCompanyAddress() { return companyAddress; }
    public String getCompanyPhone() { return companyPhone; }
    public String getCompanyEmail() { return companyEmail; }

    public String getClientName() { return clientName; }
    public String getClientAddress() { return clientAddress; }

    public BigDecimal getOriginalInvoiceTotal() { return originalInvoiceTotal; }
    public BigDecimal getDiscount() { return discount; }
    public BigDecimal getAmountPaid() { return amountPaid; }
    public String getPaymentMethod() { return paymentMethod; }
}
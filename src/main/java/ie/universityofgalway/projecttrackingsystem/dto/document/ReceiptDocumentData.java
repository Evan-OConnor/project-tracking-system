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

    // Invoice Financial Data
    private final BigDecimal subtotalExVat;
    private final BigDecimal vatRate;
    private final BigDecimal vatAmount;
    private final BigDecimal totalIncVat;

    // Payment Summary
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
            BigDecimal subtotalExVat,
            BigDecimal vatRate,
            BigDecimal vatAmount,
            BigDecimal totalIncVat,
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
        this.subtotalExVat = subtotalExVat;
        this.vatRate = vatRate;
        this.vatAmount = vatAmount;
        this.totalIncVat = totalIncVat;
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

    public BigDecimal getSubtotalExVat() { return subtotalExVat; }
    public BigDecimal getVatRate() { return vatRate; }
    public BigDecimal getVatAmount() { return vatAmount; }
    public BigDecimal getTotalIncVat() { return totalIncVat; }

    public BigDecimal getDiscount() { return discount; }
    public BigDecimal getAmountPaid() { return amountPaid; }
    public String getPaymentMethod() { return paymentMethod; }
}
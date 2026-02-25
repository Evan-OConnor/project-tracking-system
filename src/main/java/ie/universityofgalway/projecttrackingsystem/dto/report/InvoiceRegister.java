package ie.universityofgalway.projecttrackingsystem.dto.report;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvoiceRegister {

    private Long invoiceId;
    private LocalDate invoiceDate;

    private String projectTitle;
    private String clientName;

    private BigDecimal netTotal;
    private BigDecimal vatAmount;
    private BigDecimal grossTotal;

    private BigDecimal amountPaid;
    private LocalDate dateReceived;

    public InvoiceRegister(
            Long invoiceId,
            LocalDate invoiceDate,
            String projectTitle,
            String clientName,
            BigDecimal netTotal,
            BigDecimal vatAmount,
            BigDecimal grossTotal,
            BigDecimal amountPaid,
            LocalDate dateReceived
    ) {
        this.invoiceId = invoiceId;
        this.invoiceDate = invoiceDate;
        this.projectTitle = projectTitle;
        this.clientName = clientName;
        this.netTotal = netTotal;
        this.vatAmount = vatAmount;
        this.grossTotal = grossTotal;
        this.amountPaid = amountPaid;
        this.dateReceived = dateReceived;
    }

    public Long getInvoiceId() { return invoiceId; }
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public String getProjectTitle() { return projectTitle; }
    public String getClientName() { return clientName; }
    public BigDecimal getNetTotal() { return netTotal; }
    public BigDecimal getVatAmount() { return vatAmount; }
    public BigDecimal getGrossTotal() { return grossTotal; }
    public BigDecimal getAmountPaid() { return amountPaid; }
    public LocalDate getDateReceived() { return dateReceived; }
}

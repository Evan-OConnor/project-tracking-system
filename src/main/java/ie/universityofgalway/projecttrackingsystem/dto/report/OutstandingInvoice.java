package ie.universityofgalway.projecttrackingsystem.dto.report;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OutstandingInvoice {

    private Long invoiceId;
    private String projectTitle;
    private String clientName;

    private LocalDate invoiceDate;

    private BigDecimal netTotal;
    private BigDecimal vatAmount;
    private BigDecimal grossTotal;

    private BigDecimal discount;
    private BigDecimal amountPaid;
    private BigDecimal outstandingAmount;

    private Long daysOutstanding;
    private String status;

    public OutstandingInvoice(
            Long invoiceId,
            String projectTitle,
            String clientName,
            LocalDate invoiceDate,
            BigDecimal netTotal,
            BigDecimal vatAmount,
            BigDecimal grossTotal,
            BigDecimal discount,
            BigDecimal amountPaid,
            BigDecimal outstandingAmount,
            Long daysOutstanding,
            String status
    ) {
        this.invoiceId = invoiceId;
        this.projectTitle = projectTitle;
        this.clientName = clientName;
        this.invoiceDate = invoiceDate;
        this.netTotal = netTotal;
        this.vatAmount = vatAmount;
        this.grossTotal = grossTotal;
        this.discount = discount;
        this.amountPaid = amountPaid;
        this.outstandingAmount = outstandingAmount;
        this.daysOutstanding = daysOutstanding;
        this.status = status;
    }

    public Long getInvoiceId() { return invoiceId; }
    public String getProjectTitle() { return projectTitle; }
    public String getClientName() { return clientName; }
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public BigDecimal getNetTotal() { return netTotal; }
    public BigDecimal getVatAmount() { return vatAmount; }
    public BigDecimal getGrossTotal() { return grossTotal; }
    public BigDecimal getDiscount() { return discount; }
    public BigDecimal getAmountPaid() { return amountPaid; }
    public BigDecimal getOutstandingAmount() { return outstandingAmount; }
    public Long getDaysOutstanding() { return daysOutstanding; }
    public String getStatus() { return status; }
}

package ie.universityofgalway.projecttrackingsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReceiptForm {

    private Long id;
    private Long invoiceId;
    private LocalDate dateReceived;
    private BigDecimal discount;
    private BigDecimal amountPaid;

    // Getters & Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }

    public LocalDate getDateReceived() { return dateReceived; }
    public void setDateReceived(LocalDate dateReceived) { this.dateReceived = dateReceived; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }
}

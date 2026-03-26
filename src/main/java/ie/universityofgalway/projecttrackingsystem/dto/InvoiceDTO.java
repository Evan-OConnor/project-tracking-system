package ie.universityofgalway.projecttrackingsystem.dto;

import ie.universityofgalway.projecttrackingsystem.domain.lookup.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class InvoiceDTO {

    private final Long invoiceId;
    private final String invoiceNumber;
    private final String projectName;
    private final LocalDate invoiceDate;

    private final InvoiceStatus status;

    private final List<InvoiceLineItemDTO> lineItems;

    private final BigDecimal subtotal;
    private final BigDecimal vatAmount;
    private final BigDecimal grossTotal;

    private final BigDecimal totalPaid;
    private final BigDecimal outstanding;

    public InvoiceDTO(Long invoiceId,
                      String invoiceNumber,
                      String projectName,
                      LocalDate invoiceDate,
                      InvoiceStatus status,
                      List<InvoiceLineItemDTO> lineItems,
                      BigDecimal subtotal,
                      BigDecimal vatAmount,
                      BigDecimal grossTotal,
                      BigDecimal totalPaid,
                      BigDecimal outstanding) {

        this.invoiceId = invoiceId;
        this.invoiceNumber = invoiceNumber;
        this.projectName = projectName;
        this.invoiceDate = invoiceDate;
        this.status = status;
        this.lineItems = lineItems;
        this.subtotal = subtotal;
        this.vatAmount = vatAmount;
        this.grossTotal = grossTotal;
        this.totalPaid = totalPaid;
        this.outstanding = outstanding;
    }

    // Getters
    public Long getInvoiceId() {
        return invoiceId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getProjectName() {
        return projectName;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public List<InvoiceLineItemDTO> getLineItems() {
        return lineItems;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getVatAmount() {
        return vatAmount;
    }

    public BigDecimal getGrossTotal() {
        return grossTotal;
    }

    public BigDecimal getTotalPaid() {return totalPaid;}

    public BigDecimal getOutstanding() {return outstanding;}
}
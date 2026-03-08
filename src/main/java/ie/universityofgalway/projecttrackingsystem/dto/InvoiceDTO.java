package ie.universityofgalway.projecttrackingsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class InvoiceDTO {

    private final Long invoiceId;
    private final String invoiceNumber;
    private final String projectName;
    private final LocalDate invoiceDate;

    private final List<InvoiceLineItemDTO> lineItems;

    private final BigDecimal subtotal;     // Sum of net amounts
    private final BigDecimal vatAmount;    // Total VAT
    private final BigDecimal grossTotal;   // Subtotal + VAT

    public InvoiceDTO(Long invoiceId,
                      String invoiceNumber,
                      String projectName,
                      LocalDate invoiceDate,
                      List<InvoiceLineItemDTO> lineItems,
                      BigDecimal subtotal,
                      BigDecimal vatAmount,
                      BigDecimal grossTotal) {

        this.invoiceId = invoiceId;
        this.invoiceNumber = invoiceNumber;
        this.projectName = projectName;
        this.invoiceDate = invoiceDate;
        this.lineItems = lineItems;
        this.subtotal = subtotal;
        this.vatAmount = vatAmount;
        this.grossTotal = grossTotal;
    }

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
}
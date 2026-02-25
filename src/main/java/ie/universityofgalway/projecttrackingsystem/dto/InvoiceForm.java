package ie.universityofgalway.projecttrackingsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InvoiceForm {

    private Long id;
    private Long projectId;
    private Long vatRateId;
    private LocalDate invoiceDate;

    private List<InvoiceLineItemForm> items;
    private ReceiptForm receipt;

    // Totals
    private BigDecimal subtotal;          // Net total of line items
    private BigDecimal vatTotal;          // Total VAT
    private BigDecimal totalIncludingVat; // Gross total
    private BigDecimal outstandingAmount; // Gross - paid - discount

    // ===============================
    // Constructor
    // ===============================
    public InvoiceForm() {
        this.items = new ArrayList<>();
        this.items.add(new InvoiceLineItemForm()); // ensure at least one row
        this.receipt = new ReceiptForm();          // never null

        // Initialize totals to zero to avoid nulls
        this.subtotal = BigDecimal.ZERO;
        this.vatTotal = BigDecimal.ZERO;
        this.totalIncludingVat = BigDecimal.ZERO;
        this.outstandingAmount = BigDecimal.ZERO;
    }

    // ===============================
    // Getters & Setters
    // ===============================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Long getVatRateId() { return vatRateId; }
    public void setVatRateId(Long vatRateId) { this.vatRateId = vatRateId; }

    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }

    public List<InvoiceLineItemForm> getItems() { return items; }
    public void setItems(List<InvoiceLineItemForm> items) { this.items = items; }

    public ReceiptForm getReceipt() { return receipt; }
    public void setReceipt(ReceiptForm receipt) { this.receipt = receipt; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getVatTotal() { return vatTotal; }
    public void setVatTotal(BigDecimal vatTotal) { this.vatTotal = vatTotal; }

    public BigDecimal getTotalIncludingVat() { return totalIncludingVat; }
    public void setTotalIncludingVat(BigDecimal totalIncludingVat) { this.totalIncludingVat = totalIncludingVat; }

    public BigDecimal getOutstandingAmount() { return outstandingAmount; }
    public void setOutstandingAmount(BigDecimal outstandingAmount) { this.outstandingAmount = outstandingAmount; }
}
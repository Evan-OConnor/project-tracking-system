package ie.universityofgalway.projecttrackingsystem.domain.core;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_line_item")
public class InvoiceLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_line_item_id")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Positive
    @Column(name = "quantity", nullable = false, precision = 6, scale = 2)
    private BigDecimal quantity;

    @Positive
    @Column(name = "unit_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitRate;


    // Constructors

    public InvoiceLineItem() {}

    public InvoiceLineItem(Invoice invoice, String description, BigDecimal quantity, BigDecimal unitRate) {
        this.invoice = invoice;
        this.description = description;
        this.quantity = quantity;
        this.unitRate = unitRate;
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitRate() {
        return unitRate;
    }

    public void setUnitRate(BigDecimal unitRate) {
        this.unitRate = unitRate;
    }

    // Helper methods for Invoice calculations

    /** Net amount = quantity * unit rate */
    public BigDecimal getNetAmount() {
        if (quantity == null || unitRate == null) {
            return BigDecimal.ZERO;
        }
        return unitRate.multiply(quantity);
    }

    /** VAT amount = net amount * invoice's VAT rate (if available) */
    public BigDecimal getVatAmount() {
        if (invoice == null || invoice.getVatRate() == null || invoice.getVatRate().getRatePercent() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal rate = invoice.getVatRate().getRatePercent().divide(BigDecimal.valueOf(100));
        return getNetAmount().multiply(rate);
    }
}

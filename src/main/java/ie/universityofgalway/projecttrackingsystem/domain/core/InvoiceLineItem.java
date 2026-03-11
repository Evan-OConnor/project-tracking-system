package ie.universityofgalway.projecttrackingsystem.domain.core;

import ie.universityofgalway.projecttrackingsystem.domain.lookup.VatRate;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vat_rate_id", nullable = false)
    private VatRate vatRate;

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

    // NEW: Discount percentage
    @DecimalMin(value = "0.0")
    @Column(name = "discount_percent", precision = 5, scale = 2)
    private BigDecimal discountPercent = BigDecimal.ZERO;

    // Constructors

    protected InvoiceLineItem() {
    }

    public InvoiceLineItem(Invoice invoice,
                           VatRate vatRate,
                           String description,
                           BigDecimal quantity,
                           BigDecimal unitRate) {
        this.invoice = invoice;
        this.vatRate = vatRate;
        this.description = description;
        this.quantity = quantity;
        this.unitRate = unitRate;
        this.discountPercent = BigDecimal.ZERO;
    }

    public InvoiceLineItem(Invoice invoice,
                           VatRate vatRate,
                           String description,
                           BigDecimal quantity,
                           BigDecimal unitRate,
                           BigDecimal discountPercent) {
        this.invoice = invoice;
        this.vatRate = vatRate;
        this.description = description;
        this.quantity = quantity;
        this.unitRate = unitRate;
        this.discountPercent = discountPercent;
    }

    // Getters

    public Long getId() {
        return id;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public VatRate getVatRate() {
        return vatRate;
    }

    public String getDescription() {
        return description;
    }

    public String getDetails() {
        return details;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitRate() {
        return unitRate;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    // Setters

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public void setVatRate(VatRate vatRate) {
        this.vatRate = vatRate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public void setUnitRate(BigDecimal unitRate) {
        this.unitRate = unitRate;
    }

    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }
}
package ie.universityofgalway.projecttrackingsystem.domain.core;

import ie.universityofgalway.projecttrackingsystem.domain.lookup.VatRate;
import ie.universityofgalway.projecttrackingsystem.dto.ReceiptForm;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vat_rate_id", nullable = false)
    private VatRate vatRate;

    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceLineItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private Receipt receipt;

    // Constructors
    public Invoice() {}

    public Invoice(Project project, VatRate vatRate, LocalDate invoiceDate) {
        this.project = project;
        this.vatRate = vatRate;
        this.invoiceDate = invoiceDate;
    }

    // Getters & Setters
    public Long getId() { return id; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public VatRate getVatRate() { return vatRate; }
    public void setVatRate(VatRate vatRate) { this.vatRate = vatRate; }

    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }

    public List<InvoiceLineItem> getItems() { return items; }
    public void setItems(List<InvoiceLineItem> items) { this.items = items; }

    public Receipt getReceipt() { return receipt; }
    public void setReceipt(Receipt receipt) { this.receipt = receipt; }

    // ============================
    // Helper Methods for Totals
    // ============================

    /** Total net amount from line items */
    public BigDecimal getNetTotal() {
        return items.stream()
                .map(InvoiceLineItem::getNetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Total VAT amount from line items */
    public BigDecimal getVatTotal() {
        return items.stream()
                .map(InvoiceLineItem::getVatAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Gross total = net + VAT */
    public BigDecimal getGrossTotal() {
        return getNetTotal().add(getVatTotal());
    }

    /** Amount paid from receipt (if any) */
    public BigDecimal getAmountPaid() {
        return receipt != null ? receipt.getAmountPaid() : BigDecimal.ZERO;
    }

    /** Discount from receipt (if any) */
    public BigDecimal getDiscount() {
        return receipt != null ? receipt.getDiscount() : BigDecimal.ZERO;
    }

    /** Outstanding = gross total - amount paid - discount */
    public BigDecimal getOutstandingAmount() {
        return getGrossTotal().subtract(getAmountPaid()).subtract(getDiscount());
    }

    /** Check if invoice is overpaid */
    public boolean isOverpaid() {
        return getAmountPaid().add(getDiscount()).compareTo(getGrossTotal()) > 0;
    }

    @Override
    public String toString() {
        return "Invoice #" + id + " for project: " + (project != null ? project.getTitle() : "N/A");
    }
}
package ie.universityofgalway.projecttrackingsystem.domain.core;

import ie.universityofgalway.projecttrackingsystem.domain.lookup.InvoiceStatus;
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

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceLineItem> lineItems = new ArrayList<>();

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Receipt> receipts = new ArrayList<>();

    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @Column(name = "invoice_number", nullable = false, unique = true)
    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvoiceStatus status = InvoiceStatus.GENERATED;

    // Constructor
    protected Invoice() {
    }

    public Invoice(Project project,
                   LocalDate invoiceDate,
                   String invoiceNumber) {

        this.project = project;
        this.invoiceDate = invoiceDate;
        this.invoiceNumber = invoiceNumber;
        this.status = InvoiceStatus.GENERATED;
    }

    // Getters

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public List<InvoiceLineItem> getLineItems() {
        return lineItems;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    // Setters

    public void setProject(Project project) {
        this.project = project;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    // Calculated Totals

    @Transient
    public BigDecimal getTotalExVat() {

        if (lineItems == null || lineItems.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return lineItems.stream()
                .map(li -> li.getQuantity().multiply(li.getUnitRate()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
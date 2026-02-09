package ie.universityofgalway.projecttrackingsystem.domain.core;

import ie.universityofgalway.projecttrackingsystem.domain.lookup.VatRate;
import jakarta.persistence.*;

import java.time.LocalDate;

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

    // Constructors

    protected Invoice() {
    }

    public Invoice(Project project, VatRate vatRate, LocalDate invoiceDate) {
        this.project = project;
        this.vatRate = vatRate;
        this.invoiceDate = invoiceDate;
    }

    // Getters

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public VatRate getVatRate() {
        return vatRate;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    // Setters

    public void setProject(Project project) {
        this.project = project;
    }

    public void setVatRate(VatRate vatRate) {
        this.vatRate = vatRate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }
}

package ie.universityofgalway.projecttrackingsystem.domain.core;

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

    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @Column(name = "invoice_number", nullable = false, unique = true)
    private String invoiceNumber;

    // Constructors

    protected Invoice() {
    }

    public Invoice(Project project,
                   LocalDate invoiceDate,
                   String invoiceNumber) {

        this.project = project;
        this.invoiceDate = invoiceDate;
        this.invoiceNumber = invoiceNumber;
    }

    // Getters

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
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
}
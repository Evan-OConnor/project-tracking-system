package ie.universityofgalway.projecttrackingsystem.domain.core;

import jakarta.persistence.*;
import ie.universityofgalway.projecttrackingsystem.domain.core.Invoice;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cost_item")
public class CostItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cost_item_id")
    private Long id;

    // Every cost item belongs to a project
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // Every cost item must have an associated employee (per brief)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // Supplier required ONLY for OUTLAY
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_contact_id")
    private Contact supplierContact;

    // Invoices
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @NotNull
    @Column(name = "cost_date", nullable = false)
    private LocalDate costDate;

    @NotNull
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull
    @PositiveOrZero
    @Column(name = "cost_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal costAmount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private Type type;

    public enum Type {
        OUTLAY,
        EXPENSE
    }

    // Protected constructor for JPA
    protected CostItem() {}

    // Main constructor
    public CostItem(Project project,
                    Employee employee,
                    Contact supplierContact,
                    LocalDate costDate,
                    String description,
                    BigDecimal costAmount,
                    Type type) {

        this.project = project;
        this.employee = employee;
        this.supplierContact = supplierContact;
        this.costDate = costDate;
        this.description = description;
        this.costAmount = costAmount;
        this.type = type;

        validateBusinessRules();
    }

    // 🔥 Business Rule Enforcement
    @PrePersist
    @PreUpdate
    private void validateBusinessRules() {

        if (type == Type.OUTLAY && supplierContact == null) {
            throw new IllegalStateException(
                    "Outlays must be linked to a supplier."
            );
        }

        if (type == Type.EXPENSE && supplierContact != null) {
            throw new IllegalStateException(
                    "Expenses must not have a supplier."
            );
        }

        if (employee == null) {
            throw new IllegalStateException(
                    "All cost items must have an associated employee."
            );
        }

        if (costAmount == null || costAmount.signum() < 0) {
            throw new IllegalStateException(
                    "Cost amount must be zero or positive."
            );
        }
    }

    // --------------------
    // Getters
    // --------------------

    public Long getId() { return id; }

    public Project getProject() { return project; }

    public Employee getEmployee() { return employee; }

    public Contact getSupplierContact() { return supplierContact; }

    public LocalDate getCostDate() { return costDate; }

    public String getDescription() { return description; }

    public BigDecimal getCostAmount() { return costAmount; }

    public Type getType() { return type; }

    public String getSupplierName() {
        return supplierContact != null ? supplierContact.getName() : null;
    }

    public Invoice getInvoice() {return invoice;}

    // --------------------
    // Setters
    // --------------------

    public void setProject(Project project) { this.project = project; }

    public void setEmployee(Employee employee) { this.employee = employee; }

    public void setSupplierContact(Contact supplierContact) {
        this.supplierContact = supplierContact;
    }

    public void setCostDate(LocalDate costDate) { this.costDate = costDate; }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCostAmount(BigDecimal costAmount) {
        this.costAmount = costAmount;
    }

    public void setType(Type type) { this.type = type; }

    public void setInvoice(Invoice invoice) {this.invoice = invoice;}

    public boolean isUnbilled() {return this.invoice == null;}
}
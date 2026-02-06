package ie.universityofgalway.projecttrackingsystem.domain.core;


import jakarta.persistence.*;
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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_contact_id")
    private Contact supplierContact;

    @Column(name = "cost_date", nullable = false)
    private LocalDate costDate;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @PositiveOrZero
    @Column(name = "cost_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal costAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private Type type;

    // Type Enum

    public enum Type {
        OUTLAY,
        EXPENSE
    }

    // Constructors

    protected CostItem() {
    }

    public CostItem(Project project, Employee employee, LocalDate costDate, String description, BigDecimal costAmount, Type type) {
        this.project = project;
        this.employee = employee;
        this.costDate = costDate;
        this.description = description;
        this.costAmount = costAmount;
        this.type = type;
    }

    // Getters

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Contact getSupplierContact() {
        return supplierContact;
    }

    public LocalDate getCostDate() {
        return costDate;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getCostAmount() {
        return costAmount;
    }

    public Type getType() {
        return type;
    }

    // Setters

    public void setProject(Project project) {
        this.project = project;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setSupplierContact(Contact supplierContact) {
        this.supplierContact = supplierContact;
    }

    public void setCostDate(LocalDate costDate) {
        this.costDate = costDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCostAmount(BigDecimal costAmount) {
        this.costAmount = costAmount;
    }

    public void setType(Type type) {
        this.type = type;
    }
}


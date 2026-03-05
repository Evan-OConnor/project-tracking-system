package ie.universityofgalway.projecttrackingsystem.domain.core;

import ie.universityofgalway.projecttrackingsystem.domain.lookup.WorkDescription;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "timesheet_entry")
public class TimesheetEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timesheet_entry_id")
    private Long id;

    // ============================
    // Relationships
    // ============================

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "work_description_id", nullable = false)
    private WorkDescription workDescription;

    // 🔴 OPTION 2: Link to Invoice (nullable = unbilled)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")  // nullable by default
    private Invoice invoice;

    // ============================
    // Fields
    // ============================

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Positive
    @Column(name = "hours", nullable = false, precision = 6, scale = 2)
    private BigDecimal hours;

    // ============================
    // Constructors
    // ============================

    protected TimesheetEntry() {
    }

    public TimesheetEntry(Project project,
                          Employee employee,
                          WorkDescription workDescription,
                          LocalDate entryDate,
                          BigDecimal hours) {
        this.project = project;
        this.employee = employee;
        this.workDescription = workDescription;
        this.entryDate = entryDate;
        this.hours = hours;
    }

    // ============================
    // Getters
    // ============================

    public Long getId() { return id; }

    public Project getProject() { return project; }

    public Employee getEmployee() { return employee; }

    public WorkDescription getWorkDescription() { return workDescription; }

    public LocalDate getEntryDate() { return entryDate; }

    public BigDecimal getHours() { return hours; }

    public Invoice getInvoice() { return invoice; }

    // ============================
    // Setters
    // ============================

    public void setProject(Project project) {
        this.project = project;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setWorkDescription(WorkDescription workDescription) {
        this.workDescription = workDescription;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public void setHours(BigDecimal hours) {
        this.hours = hours;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    // ============================
    // Business Logic
    // ============================

    /** Unbilled if no invoice linked */
    public boolean isUnbilled() {
        return this.invoice == null;
    }

    /** Net charge = hours × employee hourly rate */
    public BigDecimal getNetAmount() {
        if (hours == null || employee == null || employee.getHourlyRate() == null) {
            return BigDecimal.ZERO;
        }
        return hours.multiply(employee.getHourlyRate());
    }

    @Override
    public String toString() {
        return "TimesheetEntry{" +
                "project=" + (project != null ? project.getTitle() : "N/A") +
                ", employee=" + (employee != null ? employee.getName() : "N/A") +
                ", hours=" + hours +
                ", date=" + entryDate +
                '}';
    }
}
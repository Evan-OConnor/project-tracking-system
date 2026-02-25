package ie.universityofgalway.projecttrackingsystem.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TimesheetEntryForm {

    private Long id; // For updates

    @NotNull(message = "Project is required")
    private Long projectId;

    @NotNull(message = "Employee is required")
    private Long employeeId;

    @NotNull(message = "Work description is required")
    private Long workDescriptionId;

    @NotNull(message = "Entry date is required")
    private LocalDate entryDate;

    @NotNull(message = "Hours are required")
    @PositiveOrZero(message = "Hours must be zero or positive")
    private BigDecimal hours;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getWorkDescriptionId() {
        return workDescriptionId;
    }

    public void setWorkDescriptionId(Long workDescriptionId) {
        this.workDescriptionId = workDescriptionId;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public BigDecimal getHours() {
        return hours;
    }

    public void setHours(BigDecimal hours) {
        this.hours = hours;
    }
}

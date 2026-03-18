package ie.universityofgalway.projecttrackingsystem.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TimesheetEntryForm {

    private Long id;

    @NotNull(message = "Project is required")
    private Long projectId;

    @NotNull(message = "Employee is required")
    private Long employeeId;

    @NotNull(message = "Work description is required")
    private Long workDescriptionId;

    @Size(max = 255, message = "Other description must be less than 255 characters")
    private String otherDescription;

    @NotNull(message = "Entry date is required")
    private LocalDate entryDate;

    @NotNull(message = "Hours are required")
    @PositiveOrZero(message = "Hours must be zero or positive")
    private BigDecimal hours;

    // Getters

    public Long getId() {
        return id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public Long getWorkDescriptionId() {
        return workDescriptionId;
    }

    public String getOtherDescription() {
        return otherDescription;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public BigDecimal getHours() {
        return hours;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public void setWorkDescriptionId(Long workDescriptionId) {
        this.workDescriptionId = workDescriptionId;
    }

    public void setOtherDescription(String otherDescription) {
        this.otherDescription = otherDescription;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public void setHours(BigDecimal hours) {
        this.hours = hours;
    }
}
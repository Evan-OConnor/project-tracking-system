package ie.universityofgalway.projecttrackingsystem.dto;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TimesheetEntryForm {

    private Long id;

    @NotNull(message = "Project is required")
    private Long projectId;

    private String projectName;

    @NotNull(message = "Work description is required")
    private Long workDescriptionId;

    @Size(max = 255, message = "Other description must be less than 255 characters")
    private String otherDescription;

    @NotNull(message = "Entry date is required")
    @PastOrPresent(message = "Start date cannot be in the future")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate entryDate;

    @NotNull(message = "Hours are required")
    @DecimalMin(value = "0.01", message = "Hours must be greater than 0")
    private BigDecimal hours;

    // Getters

    public Long getId() {
        return id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {return projectName;}

    public Long getWorkDescriptionId() {return workDescriptionId;}

    public String getOtherDescription() {return otherDescription;}

    public LocalDate getEntryDate() {return entryDate;}

    public BigDecimal getHours() {return hours;}

    // Setters
    public void setId(Long id) {this.id = id;}

    public void setProjectName(String projectName) {this.projectName = projectName;}

    public void setProjectId(Long projectId) {this.projectId = projectId;}

    public void setWorkDescriptionId(Long workDescriptionId) {this.workDescriptionId = workDescriptionId;}

    public void setOtherDescription(String otherDescription) {this.otherDescription = otherDescription;}

    public void setEntryDate(LocalDate entryDate) {this.entryDate = entryDate;}

    public void setHours(BigDecimal hours) {this.hours = hours;}
}
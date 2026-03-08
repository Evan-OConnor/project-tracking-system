package ie.universityofgalway.projecttrackingsystem.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class CostItemForm {

    private Long id;

    @NotNull(message = "Project is required")
    private Long projectId;

    @NotNull(message = "Employee is required")
    private Long employeeId;

    private Long supplierContactId; // Optional

    @NotNull(message = "Cost date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate costDate;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Cost amount is required")
    @PositiveOrZero(message = "Cost amount must be zero or positive")
    private BigDecimal costAmount;

    @NotBlank(message = "Type is required")
    @Pattern(regexp = "OUTLAY|EXPENSE", message = "Type must be OUTLAY or EXPENSE")
    private String type;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public Long getSupplierContactId() { return supplierContactId; }
    public void setSupplierContactId(Long supplierContactId) { this.supplierContactId = supplierContactId; }

    public LocalDate getCostDate() { return costDate; }
    public void setCostDate(LocalDate costDate) { this.costDate = costDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getCostAmount() { return costAmount; }
    public void setCostAmount(BigDecimal costAmount) { this.costAmount = costAmount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}

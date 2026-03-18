package ie.universityofgalway.projecttrackingsystem.dto;

import ie.universityofgalway.projecttrackingsystem.domain.core.CostItem;
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

    @NotNull(message = "Type is required")
    private CostItem.Type type;

    // Getters
    public Long getId() { return id; }

    public Long getProjectId() { return projectId; }

    public Long getEmployeeId() { return employeeId; }

    public Long getSupplierContactId() { return supplierContactId; }

    public LocalDate getCostDate() { return costDate; }

    public String getDescription() { return description; }

    public BigDecimal getCostAmount() { return costAmount; }

    public CostItem.Type getType() { return type; }

    // Setters

    public void setId(Long id) { this.id = id; }

    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public void setSupplierContactId(Long supplierContactId) { this.supplierContactId = supplierContactId; }

    public void setCostDate(LocalDate costDate) { this.costDate = costDate; }

    public void setDescription(String description) { this.description = description; }

    public void setCostAmount(BigDecimal costAmount) { this.costAmount = costAmount; }

    public void setType(CostItem.Type type) { this.type = type; }
}

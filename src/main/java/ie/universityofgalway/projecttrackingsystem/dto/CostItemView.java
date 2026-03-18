package ie.universityofgalway.projecttrackingsystem.dto;

import ie.universityofgalway.projecttrackingsystem.domain.core.CostItem;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CostItemView {

    private Long id;
    private String projectTitle;
    private String employeeName;
    private String supplierContactName;
    private LocalDate costDate;
    private String description;
    private BigDecimal costAmount;
    private String type;
    private boolean billed;

    public CostItemView(
            Long id,
            String projectTitle,
            String employeeName,
            String supplierName,
            LocalDate costDate,
            String description,
            BigDecimal costAmount,
            CostItem.Type type,
            boolean billed
    ) {
        this.id = id;
        this.projectTitle = projectTitle;
        this.employeeName = employeeName;
        this.supplierContactName = supplierName;
        this.costDate = costDate;
        this.description = description;
        this.costAmount = costAmount;
        this.type = type.name();
        this.billed = billed;
    }

// Getters

    public Long getId() {
        return id;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getSupplierContactName() {
        return supplierContactName;
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

    public String getType() {
        return type;
    }

    public boolean isBilled() {
        return billed;
    }
}
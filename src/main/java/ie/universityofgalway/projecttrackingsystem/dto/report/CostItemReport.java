package ie.universityofgalway.projecttrackingsystem.dto.report;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CostItemReport {

    private String projectTitle;
    private String employeeName;

    private LocalDate costDate;
    private String description;

    private BigDecimal costAmount;
    private String type; // OUTLAY or EXPENSE

    private String supplierName;

    public CostItemReport(
            String projectTitle,
            String employeeName,
            LocalDate costDate,
            String description,
            BigDecimal costAmount,
            String type,
            String supplierName
    ) {
        this.projectTitle = projectTitle;
        this.employeeName = employeeName;
        this.costDate = costDate;
        this.description = description;
        this.costAmount = costAmount;
        this.type = type;
        this.supplierName = supplierName;
    }

    public String getProjectTitle() { return projectTitle; }
    public String getEmployeeName() { return employeeName; }
    public LocalDate getCostDate() { return costDate; }
    public String getDescription() { return description; }
    public BigDecimal getCostAmount() { return costAmount; }
    public String getType() { return type; }
    public String getSupplierName() { return supplierName; }
}

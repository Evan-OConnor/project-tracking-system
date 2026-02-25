package ie.universityofgalway.projecttrackingsystem.dto;

import java.math.BigDecimal;

public class ProjectView {

    private Long id;
    private String title;
    private String description;
    private String status;
    private BigDecimal totalOutlays;
    private BigDecimal totalExpenses;
    private BigDecimal outstandingInvoices;

    public ProjectView(Long id, String title, String description, String status,
                       BigDecimal totalOutlays, BigDecimal totalExpenses, BigDecimal outstandingInvoices) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.totalOutlays = totalOutlays;
        this.totalExpenses = totalExpenses;
        this.outstandingInvoices = outstandingInvoices;
    }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public BigDecimal getTotalOutlays() { return totalOutlays; }
    public BigDecimal getTotalExpenses() { return totalExpenses; }
    public BigDecimal getOutstandingInvoices() { return outstandingInvoices; }

    // Setters if needed
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }
    public void setTotalOutlays(BigDecimal totalOutlays) { this.totalOutlays = totalOutlays; }
    public void setTotalExpenses(BigDecimal totalExpenses) { this.totalExpenses = totalExpenses; }
    public void setOutstandingInvoices(BigDecimal outstandingInvoices) { this.outstandingInvoices = outstandingInvoices; }
}

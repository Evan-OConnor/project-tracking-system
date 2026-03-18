package ie.universityofgalway.projecttrackingsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProjectView {

    private Long id;
    private String title;
    private String description;
    private String status;

    private String categoryName;
    private String clientName;
    private LocalDate startDate;

    private BigDecimal totalOutlays;
    private BigDecimal totalExpenses;

    public ProjectView(Long id, String title, String description, String status, String categoryName,
                       String clientName, LocalDate startDate, BigDecimal totalOutlays, BigDecimal totalExpenses) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.categoryName = categoryName;
        this.clientName = clientName;
        this.startDate = startDate;
        this.totalOutlays = totalOutlays;
        this.totalExpenses = totalExpenses;
    }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getCategoryName(){return categoryName;}
    public String getClientName(){return clientName;}
    public LocalDate getStartDate(){return startDate;}
    public BigDecimal getTotalOutlays() { return totalOutlays; }
    public BigDecimal getTotalExpenses() { return totalExpenses; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }
    public void setTotalOutlays(BigDecimal totalOutlays) { this.totalOutlays = totalOutlays; }
    public void setTotalExpenses(BigDecimal totalExpenses) { this.totalExpenses = totalExpenses; }
    }

package ie.universityofgalway.projecttrackingsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProjectSearchCriteria {

    private String title;
    private Long clientId;
    private Long statusId;
    private Long categoryId;

    private LocalDate startDateFrom;
    private LocalDate startDateTo;

    private BigDecimal minBudget;
    private BigDecimal maxBudget;

    private Boolean hasInvoices;
    private Boolean hasExpenses;

    // Getters

    public String getTitle() {
        return title;
    }

    public Long getClientId() {
        return clientId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public Long getCategoryId() {return categoryId;}

    public LocalDate getStartDateFrom() {
        return startDateFrom;
    }

    public LocalDate getStartDateTo() {
        return startDateTo;
    }

    public BigDecimal getMinBudget() {
        return minBudget;
    }

    public BigDecimal getMaxBudget() {
        return maxBudget;
    }

    public Boolean getHasInvoices() {
        return hasInvoices;
    }

    public Boolean getHasExpenses() {
        return hasExpenses;
    }


  // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public void setCategoryId(Long categoryId) {this.categoryId = categoryId;}

    public void setStartDateFrom(LocalDate startDateFrom) {
        this.startDateFrom = startDateFrom;
    }

    public void setStartDateTo(LocalDate startDateTo) {
        this.startDateTo = startDateTo;
    }

    public void setMinBudget(BigDecimal minBudget) {
        this.minBudget = minBudget;
    }

    public void setMaxBudget(BigDecimal maxBudget) {
        this.maxBudget = maxBudget;
    }

    public void setHasInvoices(Boolean hasInvoices) {
        this.hasInvoices = hasInvoices;
    }

    public void setHasExpenses(Boolean hasExpenses) {
        this.hasExpenses = hasExpenses;
    }

    /**
     * Returns true if no search criteria are set.
     */
    public boolean isEmpty() {
        return (title == null || title.isBlank())
                && clientId == null
                && statusId == null
                && startDateFrom == null
                && startDateTo == null
                && minBudget == null
                && maxBudget == null
                && hasInvoices == null
                && hasExpenses == null;
    }
}
package ie.universityofgalway.projecttrackingsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProjectSearchCriteria {

    private String title;
    private Long clientId;
    private Long statusId;

    private LocalDate startDateFrom;
    private LocalDate startDateTo;

    private BigDecimal minBudget;
    private BigDecimal maxBudget;

    private Boolean hasInvoices;
    private Boolean hasExpenses;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public LocalDate getStartDateFrom() {
        return startDateFrom;
    }

    public void setStartDateFrom(LocalDate startDateFrom) {
        this.startDateFrom = startDateFrom;
    }

    public LocalDate getStartDateTo() {
        return startDateTo;
    }

    public void setStartDateTo(LocalDate startDateTo) {
        this.startDateTo = startDateTo;
    }

    public BigDecimal getMinBudget() {
        return minBudget;
    }

    public void setMinBudget(BigDecimal minBudget) {
        this.minBudget = minBudget;
    }

    public BigDecimal getMaxBudget() {
        return maxBudget;
    }

    public void setMaxBudget(BigDecimal maxBudget) {
        this.maxBudget = maxBudget;
    }

    public Boolean getHasInvoices() {
        return hasInvoices;
    }

    public void setHasInvoices(Boolean hasInvoices) {
        this.hasInvoices = hasInvoices;
    }

    public Boolean getHasExpenses() {
        return hasExpenses;
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
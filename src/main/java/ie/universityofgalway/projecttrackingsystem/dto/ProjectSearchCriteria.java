package ie.universityofgalway.projecttrackingsystem.dto;

import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class ProjectSearchCriteria {

    @Size(max = 80, message = "Project title must be 80 characters or fewer")
    private String title;
    private String clientContactName;
    private Long statusId;
    private String status;
    private Long categoryId;

    @DateTimeFormat (pattern = "yyyy-MM-dd")
    private LocalDate startDateFrom;

    @DateTimeFormat (pattern = "yyyy-MM-dd")
    private LocalDate startDateTo;

    private Boolean hasInvoices;
    private Boolean hasExpenses;

    // Getters
    public String getTitle() {
        return title;
    }

    public String getClientContactName() {
        return clientContactName;
    }

    public Long getStatusId() {
        return statusId;
    }

    public String getStatus() {
        return status;
    }

    public Long getCategoryId() {return categoryId;}

    public LocalDate getStartDateFrom() {
        return startDateFrom;
    }

    public LocalDate getStartDateTo() {
        return startDateTo;
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

    public void setClientContactName(String clientContactName) {
        this.clientContactName = clientContactName;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim().toUpperCase();
    }

    public void setCategoryId(Long categoryId) {this.categoryId = categoryId;}

    public void setStartDateFrom(LocalDate startDateFrom) {
        this.startDateFrom = startDateFrom;
    }

    public void setStartDateTo(LocalDate startDateTo) {
        this.startDateTo = startDateTo;
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
                && clientContactName == null
                && statusId == null
                && (status == null || status.isBlank())
                && startDateFrom == null
                && startDateTo == null
                && hasInvoices == null
                && hasExpenses == null;
    }
}
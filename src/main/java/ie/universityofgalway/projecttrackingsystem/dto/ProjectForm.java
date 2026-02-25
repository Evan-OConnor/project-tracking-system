package ie.universityofgalway.projecttrackingsystem.dto;

import java.time.LocalDate;

public class ProjectForm {
    private Long categoryId;
    private Long statusId;
    private Long clientContactId;
    private Long solicitorContactId;
    private Long insuranceCompanyContactId;
    private String title;
    private String description;
    private LocalDate startDate;

    public ProjectForm() {
    }

    // Getters & Setters
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public Long getStatusId() { return statusId; }
    public void setStatusId(Long statusId) { this.statusId = statusId; }

    public Long getClientContactId() { return clientContactId; }
    public void setClientContactId(Long clientContactId) { this.clientContactId = clientContactId; }

    public Long getSolicitorContactId() { return solicitorContactId; }
    public void setSolicitorContactId(Long solicitorContactId) { this.solicitorContactId = solicitorContactId; }

    public Long getInsuranceCompanyContactId() { return insuranceCompanyContactId; }
    public void setInsuranceCompanyContactId(Long insuranceCompanyContactId) { this.insuranceCompanyContactId = insuranceCompanyContactId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
}

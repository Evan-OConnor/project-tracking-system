package ie.universityofgalway.projecttrackingsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public class ProjectForm {

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Status is required")
    private Long statusId;

    @NotBlank(message = "Client name is required")
    @Size(max = 255, message = "Client name must be less than 255 characters")
    private String clientContactName;

    @Size(max = 255, message = "Solicitor name must be less than 255 characters")
    private String solicitorContactName;

    @Size(max = 255, message = "Insurance company name must be less than 255 characters")
    private String insuranceCompanyContactName;

    @NotBlank(message = "Project title is required")
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @NotNull(message = "Start date is required")
    @PastOrPresent(message = "Start date cannot be in the future")
    private LocalDate startDate;

    public ProjectForm() {
    }


    // Getters
    public Long getCategoryId() {
        return categoryId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public String getClientContactName() {
        return clientContactName;
    }

    public String getSolicitorContactName() {
        return solicitorContactName;
    }

    public String getInsuranceCompanyContactName() {
        return insuranceCompanyContactName;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }



    // Setters
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public void setClientContactName(String clientContactName) {
        this.clientContactName = trim(clientContactName);
    }

    public void setSolicitorContactName(String solicitorContactName) {this.solicitorContactName = trim(solicitorContactName);}

    public void setInsuranceCompanyContactName(String insuranceCompanyContactName) {this.insuranceCompanyContactName = trim(insuranceCompanyContactName);}

    public void setTitle(String title) {
        this.title = trim(title);
    }

    public void setDescription(String description) {
        this.description = trim(description);
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    // Utility
    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
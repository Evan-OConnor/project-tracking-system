package ie.universityofgalway.projecttrackingsystem.dto;

import java.time.LocalDate;

public class ProjectView {
    private Long id;
    private String reference;
    private String title;
    private String description;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;

    public ProjectView(Long id, String reference, String title, String description,
                       String status, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.reference = reference;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() { return id; }
    public String getReference() { return reference; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
}

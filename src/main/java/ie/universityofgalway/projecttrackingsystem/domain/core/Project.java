package ie.universityofgalway.projecttrackingsystem.domain.core;

import ie.universityofgalway.projecttrackingsystem.domain.lookup.ProjectCategory;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.ProjectStatus;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ProjectCategory category;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private ProjectStatus status;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_contact_id", nullable = false)
    private Contact clientContact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitor_contact_id")
    private Contact solicitorContact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_company_contact_id")
    private Contact insuranceCompanyContact;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Constructors

    protected Project() {
    }

    public Project(ProjectCategory category, ProjectStatus status, Contact clientContact, LocalDate startDate, String title) {
        this.category = category;
        this.status = status;
        this.clientContact = clientContact;
        this.startDate = startDate;
        this.title = title;
    }

    // Getters

    public Long getId() {
        return id;
    }

    public ProjectCategory getCategory() {
        return category;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public Contact getClientContact() {
        return clientContact;
    }

    public Contact getSolicitorContact() {
        return solicitorContact;
    }

    public Contact getInsuranceCompanyContact() {
        return insuranceCompanyContact;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    // Setters

    public void setCategory(ProjectCategory category) {
        this.category = category;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public void setClientContact(Contact clientContact) {
        this.clientContact = clientContact;
    }

    public void setSolicitorContact(Contact solicitorContact) {
        this.solicitorContact = solicitorContact;
    }

    public void setInsuranceCompanyContact(Contact insuranceCompanyContact) {
        this.insuranceCompanyContact = insuranceCompanyContact;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

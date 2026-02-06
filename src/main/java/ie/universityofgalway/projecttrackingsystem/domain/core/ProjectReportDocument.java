package ie.universityofgalway.projecttrackingsystem.domain.core;

import ie.universityofgalway.projecttrackingsystem.domain.lookup.DocumentType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "project_report_document")
public class ProjectReportDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_document_id")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id", nullable = false)
    private DocumentType documentType;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_employee_id", nullable = false)
    private Employee uploadedBy;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "date_created", nullable = false)
    private LocalDateTime dateCreated;

    @Column(name = "storage_location", nullable = false, length = 500)
    private String storageLocation;

    // Constructors

    protected ProjectReportDocument() {
    }

    public ProjectReportDocument(Project project, DocumentType documentType, Employee uploadedBy, String title, LocalDateTime dateCreated, String storageLocation) {
        this.project = project;
        this.documentType = documentType;
        this.uploadedBy = uploadedBy;
        this.title = title;
        this.dateCreated = dateCreated;
        this.storageLocation = storageLocation;
    }

    // Getters

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public Employee getUploadedBy() {
        return uploadedBy;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    // Setters

    public void setProject(Project project) {
        this.project = project;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public void setUploadedBy(Employee uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }
}

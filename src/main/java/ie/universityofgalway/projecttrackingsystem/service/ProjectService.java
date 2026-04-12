package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.domain.core.*;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.ProjectStatus;
import ie.universityofgalway.projecttrackingsystem.dto.ProjectForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.*;
import ie.universityofgalway.projecttrackingsystem.repository.lookup.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProjectService {

    private static final String ACTIVE_STATUS = "ACTIVE";

    private final ProjectRepository projectRepository;
    private final ProjectCategoryRepository categoryRepository;
    private final ProjectStatusRepository statusRepository;
    private final ContactRepository contactRepository;
    private final InvoiceRepository invoiceRepository;
    private final CostItemRepository costItemRepository;
    private final TimesheetEntryRepository timesheetRepository;

    // Constructor
    public ProjectService(ProjectRepository projectRepository,
                          ProjectCategoryRepository categoryRepository,
                          ProjectStatusRepository statusRepository,
                          ContactRepository contactRepository,
                          InvoiceRepository invoiceRepository,
                          CostItemRepository costItemRepository,
                          TimesheetEntryRepository timesheetRepository) {

        this.projectRepository = projectRepository;
        this.categoryRepository = categoryRepository;
        this.statusRepository = statusRepository;
        this.contactRepository = contactRepository;
        this.invoiceRepository = invoiceRepository;
        this.costItemRepository = costItemRepository;
        this.timesheetRepository = timesheetRepository;
    }

    // Create Form
    public Project create(ProjectForm form) {

        Project project = new Project();

        project.setTitle(form.getTitle());
        project.setDescription(form.getDescription());
        project.setStartDate(form.getStartDate());

        project.setCategory(
                categoryRepository.findById(form.getCategoryId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid category"))
        );

        Contact client = contactRepository.findById(form.getClientId())
                .orElseThrow(() -> new IllegalArgumentException("Client must exist"));

        project.setClientContact(client);

        if (form.getSolicitorId() != null) {
            Contact solicitor = contactRepository.findById(form.getSolicitorId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid solicitor"));
            project.setSolicitorContact(solicitor);
        }

        if (form.getInsuranceCompanyId() != null) {
            Contact insurance = contactRepository.findById(form.getInsuranceCompanyId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid insurance company"));
            project.setInsuranceCompanyContact(insurance);
        }

        ProjectStatus activeStatus = statusRepository.findByName(ACTIVE_STATUS)
                .orElseThrow(() -> new IllegalStateException("Default status not found"));

        project.setStatus(activeStatus);

        return projectRepository.save(project);
    }

    // Update
    public Project update(Long id, ProjectForm form) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));

        updateEntity(project, form);
        return projectRepository.save(project);
    }

    // Delete
    public void delete(Long id) {

        if (invoiceRepository.existsByProjectId(id)) {
            throw new IllegalStateException("Cannot delete project because invoices exist.");
        }

        if (costItemRepository.existsByProjectId(id)) {
            throw new IllegalStateException("Cannot delete project because costs exist.");
        }

        if (timesheetRepository.existsByProjectId(id)) {
            throw new IllegalStateException("Cannot delete project because timesheets exist.");
        }

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        projectRepository.delete(project);
    }

    // Form
    public ProjectForm getFormById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        return mapToForm(project);
    }
    // Update
    public void updateEntity(Project project, ProjectForm form) {

        project.setCategory(categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category")));

        project.setStatus(statusRepository.findById(form.getStatusId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid status")));

        Contact client = contactRepository.findById(form.getClientId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid client"));
        project.setClientContact(client);

        if (form.getSolicitorId() != null) {
            Contact solicitor = contactRepository.findById(form.getSolicitorId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid solicitor"));
            project.setSolicitorContact(solicitor);
        } else {
            project.setSolicitorContact(null);
        }

        if (form.getInsuranceCompanyId() != null) {
            Contact insurance = contactRepository.findById(form.getInsuranceCompanyId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid insurance"));
            project.setInsuranceCompanyContact(insurance);
        } else {
            project.setInsuranceCompanyContact(null);
        }

        project.setTitle(form.getTitle());
        project.setDescription(form.getDescription());
        project.setStartDate(form.getStartDate());
    }

    // Project details to form
    public ProjectForm mapToForm(Project project) {
        ProjectForm form = new ProjectForm();

        form.setCategoryId(project.getCategory().getId());
        form.setStatusId(project.getStatus().getId());
        form.setClientId(project.getClientContact().getId());
        form.setClientName(project.getClientContact().getName());

        if (project.getSolicitorContact() != null) {
            form.setSolicitorId(project.getSolicitorContact().getId());
            form.setSolicitorContactName(project.getSolicitorContact().getName());
        }

        if (project.getInsuranceCompanyContact() != null) {
            form.setInsuranceCompanyId(project.getInsuranceCompanyContact().getId());
            form.setInsuranceCompanyContactName(project.getInsuranceCompanyContact().getName());
        }

        form.setTitle(project.getTitle());
        form.setDescription(project.getDescription());
        form.setStartDate(project.getStartDate());

        return form;
    }

    // Active Projects
    public long getActiveProjectCount() {
        return projectRepository.countByStatus_NameIn(
                List.of(ACTIVE_STATUS)
        );
    }

    // Total Projects Count
    public long getTotalProjectCount() {
        return projectRepository.count();
    }

    // Count by status
    public long getProjectCountByStatus(String statusName) {
        return projectRepository.countByStatus_NameIn(List.of(statusName));
    }

    // Recent Projects
    public List<Project> getRecentProjects() {
        return projectRepository.findTop6ByOrderByStartDateDescIdDesc();
    }
}
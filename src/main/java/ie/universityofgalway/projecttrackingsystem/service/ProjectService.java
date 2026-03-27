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

    private final ProjectRepository projectRepository;
    private final ProjectCategoryRepository categoryRepository;
    private final ProjectStatusRepository statusRepository;
    private final ContactRepository contactRepository;
    private final InvoiceRepository invoiceRepository;
    private final CostItemRepository costItemRepository;
    private final TimesheetEntryRepository timesheetRepository;

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

    // Create
    public Project create(ProjectForm form) {
        Project project = new Project();
        updateEntity(project, form);
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

        Contact client = contactRepository.findByNameIgnoreCase(form.getClientContactName())
                .orElseThrow(() -> new IllegalArgumentException("Client must exist"));
        project.setClientContact(client);

        if (form.getSolicitorContactName() != null && !form.getSolicitorContactName().isBlank()) {
            project.setSolicitorContact(
                    contactRepository.findByNameIgnoreCase(form.getSolicitorContactName()).orElse(null)
            );
        } else {
            project.setSolicitorContact(null);
        }

        if (form.getInsuranceCompanyContactName() != null && !form.getInsuranceCompanyContactName().isBlank()) {
            project.setInsuranceCompanyContact(
                    contactRepository.findByNameIgnoreCase(form.getInsuranceCompanyContactName()).orElse(null)
            );
        } else {
            project.setInsuranceCompanyContact(null);
        }

        project.setTitle(form.getTitle());
        project.setDescription(form.getDescription());
        project.setStartDate(form.getStartDate());
    }

    public ProjectForm mapToForm(Project project) {
        ProjectForm form = new ProjectForm();

        form.setCategoryId(project.getCategory().getId());
        form.setStatusId(project.getStatus().getId());
        form.setClientContactName(project.getClientContact().getName());

        if (project.getSolicitorContact() != null) {
            form.setSolicitorContactName(project.getSolicitorContact().getName());
        }

        if (project.getInsuranceCompanyContact() != null) {
            form.setInsuranceCompanyContactName(project.getInsuranceCompanyContact().getName());
        }

        form.setTitle(project.getTitle());
        form.setDescription(project.getDescription());
        form.setStartDate(project.getStartDate());

        return form;
    }
    public long getActiveProjectCount() {
        return projectRepository.countByStatus_NameIn(
                List.of("IN_PROGRESS")
        );
    }
}
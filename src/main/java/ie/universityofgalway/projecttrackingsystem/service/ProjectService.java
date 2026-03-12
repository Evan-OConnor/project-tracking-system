package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.domain.core.Project;
import ie.universityofgalway.projecttrackingsystem.domain.core.Contact;
import ie.universityofgalway.projecttrackingsystem.dto.ProjectForm;
import ie.universityofgalway.projecttrackingsystem.dto.ProjectSearchCriteria;
import ie.universityofgalway.projecttrackingsystem.repository.core.*;
import ie.universityofgalway.projecttrackingsystem.repository.lookup.ProjectCategoryRepository;
import ie.universityofgalway.projecttrackingsystem.repository.lookup.ProjectStatusRepository;
import ie.universityofgalway.projecttrackingsystem.specification.ProjectSpecification;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

@Service
public class ProjectService implements BaseService<Project, ProjectForm> {

    private final ProjectRepository projectRepository;
    private final ProjectCategoryRepository categoryRepository;
    private final ProjectStatusRepository statusRepository;
    private final ContactRepository contactRepository;

    private final TimesheetEntryRepository timesheetRepository;
    private final CostItemRepository costItemRepository;
    private final InvoiceRepository invoiceRepository;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectCategoryRepository categoryRepository,
                          ProjectStatusRepository statusRepository,
                          ContactRepository contactRepository,
                          TimesheetEntryRepository timesheetRepository,
                          CostItemRepository costItemRepository,
                          InvoiceRepository invoiceRepository) {

        this.projectRepository = projectRepository;
        this.categoryRepository = categoryRepository;
        this.statusRepository = statusRepository;
        this.contactRepository = contactRepository;
        this.timesheetRepository = timesheetRepository;
        this.costItemRepository = costItemRepository;
        this.invoiceRepository = invoiceRepository;
    }

    // ------------------------------------------------
    // LIST
    // ------------------------------------------------

    @Override
    public List<Project> list() {
        return projectRepository.findAll();
    }

    // ------------------------------------------------
    // ADVANCED SEARCH
    // ------------------------------------------------

    public List<Project> searchProjects(ProjectSearchCriteria criteria) {
        return projectRepository.findAll(ProjectSpecification.search(criteria));
    }

    // ------------------------------------------------
    // GET BY ID
    // ------------------------------------------------

    @Override
    public Project getById(Long id) {

        return projectRepository.findWithCostItemsById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Project not found with id: " + id));
    }

    // ------------------------------------------------
    // GET FORM BY ID
    // ------------------------------------------------

    @Override
    public ProjectForm getFormById(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Project not found with id: " + id));

        return mapToForm(project);
    }

    // ------------------------------------------------
    // CREATE
    // ------------------------------------------------

    @Override
    public Project create(ProjectForm form) {

        Project project = new Project();

        updateEntity(project, form);

        return projectRepository.save(project);
    }

    // ------------------------------------------------
    // UPDATE
    // ------------------------------------------------

    @Override
    public Project update(Long id, ProjectForm form) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Project not found with id: " + id));

        updateEntity(project, form);

        return projectRepository.save(project);
    }

    // ------------------------------------------------
    // DELETE
    // ------------------------------------------------

    @Override
    public void delete(Long id) {

        if (invoiceRepository.existsByProjectId(id)) {
            throw new IllegalStateException(
                    "Cannot delete project because invoices exist.");
        }

        if (costItemRepository.existsByProjectId(id)) {
            throw new IllegalStateException(
                    "Cannot delete project because expenses or outlays exist.");
        }

        if (timesheetRepository.existsByProjectId(id)) {
            throw new IllegalStateException(
                    "Cannot delete project because timesheet entries exist.");
        }

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Project not found with id: " + id));

        projectRepository.delete(project);
    }

    // ------------------------------------------------
    // UPDATE ENTITY FROM FORM
    // ------------------------------------------------

    @Override
    public void updateEntity(Project project, ProjectForm form) {

        project.setCategory(
                categoryRepository.findById(form.getCategoryId())
                        .orElseThrow(() ->
                                new IllegalArgumentException("Invalid project category"))
        );

        project.setStatus(
                statusRepository.findById(form.getStatusId())
                        .orElseThrow(() ->
                                new IllegalArgumentException("Invalid project status"))
        );

        Contact client = contactRepository
                .findByNameIgnoreCase(form.getClientContactName())
                .orElseThrow(() ->
                        new IllegalArgumentException("Client must exist. Please create the contact first."));

        project.setClientContact(client);

        Contact solicitor = null;

        if (form.getSolicitorContactName() != null && !form.getSolicitorContactName().isBlank()) {
            solicitor = contactRepository
                    .findByNameIgnoreCase(form.getSolicitorContactName())
                    .orElse(null);
        }

        project.setSolicitorContact(solicitor);

        Contact insurer = null;

        if (form.getInsuranceCompanyContactName() != null && !form.getInsuranceCompanyContactName().isBlank()) {
            insurer = contactRepository
                    .findByNameIgnoreCase(form.getInsuranceCompanyContactName())
                    .orElse(null);
        }

        project.setInsuranceCompanyContact(insurer);

        project.setTitle(form.getTitle());
        project.setDescription(form.getDescription());
        project.setStartDate(form.getStartDate());
    }

    // ------------------------------------------------
    // MAP ENTITY → FORM
    // ------------------------------------------------

    @Override
    public ProjectForm mapToForm(Project project) {

        ProjectForm form = new ProjectForm();

        form.setCategoryId(project.getCategory().getId());
        form.setStatusId(project.getStatus().getId());

        form.setClientContactName(project.getClientContact().getName());

        form.setSolicitorContactName(
                project.getSolicitorContact() != null
                        ? project.getSolicitorContact().getName()
                        : null
        );

        form.setInsuranceCompanyContactName(
                project.getInsuranceCompanyContact() != null
                        ? project.getInsuranceCompanyContact().getName()
                        : null
        );

        form.setTitle(project.getTitle());
        form.setDescription(project.getDescription());
        form.setStartDate(project.getStartDate());

        return form;
    }

    // LOAD LOOKUPS

    public void loadFormLookups(Model model) {

        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("statuses", statusRepository.findAll());
        model.addAttribute("contacts", contactRepository.findAll());
    }
}
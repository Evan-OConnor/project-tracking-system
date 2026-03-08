package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.domain.core.Project;
import ie.universityofgalway.projecttrackingsystem.domain.core.Contact;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.ProjectCategory;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.ProjectStatus;
import ie.universityofgalway.projecttrackingsystem.dto.ProjectForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.ProjectRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.ContactRepository;
import ie.universityofgalway.projecttrackingsystem.repository.lookup.ProjectCategoryRepository;
import ie.universityofgalway.projecttrackingsystem.repository.lookup.ProjectStatusRepository;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

@Service
public class ProjectService implements BaseService<Project, ProjectForm> {

    private final ProjectRepository projectRepository;
    private final ProjectCategoryRepository categoryRepository;
    private final ProjectStatusRepository statusRepository;
    private final ContactRepository contactRepository;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectCategoryRepository categoryRepository,
                          ProjectStatusRepository statusRepository,
                          ContactRepository contactRepository) {

        this.projectRepository = projectRepository;
        this.categoryRepository = categoryRepository;
        this.statusRepository = statusRepository;
        this.contactRepository = contactRepository;
    }

    // LIST

    @Override
    public List<Project> list() {
        return projectRepository.findAll();
    }

    // GET BY ID

    @Override
    public Project getById(Long id) {
        return projectRepository.findWithCostItemsById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    // GET FORM BY ID

    @Override
    public ProjectForm getFormById(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        return mapToForm(project);
    }

    // CREATE

    @Override
    public Project create(ProjectForm form) {

        Project project = new Project();

        updateEntity(project, form);

        return projectRepository.save(project);
    }

    // UPDATE

    @Override
    public Project update(Long id, ProjectForm form) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        updateEntity(project, form);

        return projectRepository.save(project);
    }

    // DELETE

    @Override
    public void delete(Long id) {
        projectRepository.deleteById(id);
    }

    // UPDATE ENTITY FROM FORM

    @Override
    public void updateEntity(Project project, ProjectForm form) {

        project.setCategory(categoryRepository.findById(form.getCategoryId())
                .orElseThrow());

        project.setStatus(statusRepository.findById(form.getStatusId())
                .orElseThrow());

        Contact client = contactRepository
                .findByNameIgnoreCase(form.getClientContactName())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Client not found: " + form.getClientContactName()
                        )
                );

        project.setClientContact(client);

        project.setSolicitorContact(form.getSolicitorContactId() != null
                ? contactRepository.findById(form.getSolicitorContactId()).orElse(null)
                : null);

        project.setInsuranceCompanyContact(form.getInsuranceCompanyContactId() != null
                ? contactRepository.findById(form.getInsuranceCompanyContactId()).orElse(null)
                : null);

        project.setTitle(form.getTitle());
        project.setDescription(form.getDescription());
        project.setStartDate(form.getStartDate());
    }

    // MAP ENTITY → FORM

    @Override
    public ProjectForm mapToForm(Project project) {

        ProjectForm form = new ProjectForm();

        form.setCategoryId(project.getCategory().getId());
        form.setStatusId(project.getStatus().getId());
        form.setClientContactName(project.getClientContact().getName());

        form.setSolicitorContactId(
                project.getSolicitorContact() != null
                        ? project.getSolicitorContact().getId()
                        : null
        );

        form.setInsuranceCompanyContactId(
                project.getInsuranceCompanyContact() != null
                        ? project.getInsuranceCompanyContact().getId()
                        : null
        );

        form.setTitle(project.getTitle());
        form.setDescription(project.getDescription());
        form.setStartDate(project.getStartDate());

        return form;
    }

    // LOAD LOOKUPS FOR FORMS

    public void loadFormLookups(Model model) {

        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("statuses", statusRepository.findAll());
        model.addAttribute("contacts", contactRepository.findAll());
    }
}
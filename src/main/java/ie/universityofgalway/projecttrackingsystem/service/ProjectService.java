package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.domain.core.Contact;
import ie.universityofgalway.projecttrackingsystem.domain.core.Project;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.ProjectCategory;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.ProjectStatus;
import ie.universityofgalway.projecttrackingsystem.dto.ProjectForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.ContactRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.ProjectRepository;
import ie.universityofgalway.projecttrackingsystem.repository.lookup.ProjectCategoryRepository;
import ie.universityofgalway.projecttrackingsystem.repository.lookup.ProjectStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

@Service
public class ProjectService {

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

    public List<Project> list() {
        return projectRepository.findAll();
    }

    public void loadFormLookups(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("statuses", statusRepository.findAll());
        model.addAttribute("contacts", contactRepository.findAll());
    }

    public Project save(ProjectForm form) {
        Project project;

        ProjectCategory category = categoryRepository.findById(form.getCategoryId()).orElseThrow();
        ProjectStatus status = statusRepository.findById(form.getStatusId()).orElseThrow();
        Contact client = contactRepository.findById(form.getClientContactId()).orElseThrow();

        if (form.getProjectId() != null) {
            project = projectRepository.findById(form.getProjectId()).orElseThrow();
        } else {
            project = new Project(category, status, client, form.getStartDate(), form.getTitle());
        }

        project.setCategory(category);
        project.setStatus(status);
        project.setClientContact(client);
        project.setStartDate(form.getStartDate());
        project.setTitle(form.getTitle());
        project.setDescription(form.getDescription());

        project.setSolicitorContact(
                form.getSolicitorContactId() != null
                        ? contactRepository.findById(form.getSolicitorContactId()).orElseThrow()
                        : null
        );

        project.setInsuranceCompanyContact(
                form.getInsuranceCompanyContactId() != null
                        ? contactRepository.findById(form.getInsuranceCompanyContactId()).orElseThrow()
                        : null
        );

        return projectRepository.save(project);
    }

    public Project getById(Long id) {
        return projectRepository.findById(id).orElseThrow();
    }

    public void delete(Long id) {
        projectRepository.deleteById(id);
    }

    public ProjectForm getFormById(Long id) {
        Project p = getById(id);
        ProjectForm form = new ProjectForm();

        form.setProjectId(p.getId());
        form.setCategoryId(p.getCategory().getId());
        form.setStatusId(p.getStatus().getId());
        form.setClientContactId(p.getClientContact().getId());
        form.setTitle(p.getTitle());
        form.setDescription(p.getDescription());
        form.setStartDate(p.getStartDate());

        if (p.getSolicitorContact() != null) form.setSolicitorContactId(p.getSolicitorContact().getId());
        if (p.getInsuranceCompanyContact() != null) form.setInsuranceCompanyContactId(p.getInsuranceCompanyContact().getId());

        return form;
    }
}

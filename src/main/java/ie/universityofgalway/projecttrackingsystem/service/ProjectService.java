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

    @Override
    public List<Project> list() {
        return projectRepository.findAll();
    }

    @Override
    public Project getById(Long id) {
        return projectRepository.findWithCostItemsById(id)
                .orElseThrow();
    }

    @Override
    public ProjectForm getFormById(Long id) {
        Project project = projectRepository.findById(id).orElseThrow();
        ProjectForm form = new ProjectForm();
        form.setCategoryId(project.getCategory().getId());
        form.setStatusId(project.getStatus().getId());
        form.setClientContactId(project.getClientContact().getId());
        form.setSolicitorContactId(project.getSolicitorContact() != null ? project.getSolicitorContact().getId() : null);
        form.setInsuranceCompanyContactId(project.getInsuranceCompanyContact() != null ? project.getInsuranceCompanyContact().getId() : null);
        form.setTitle(project.getTitle());
        form.setDescription(project.getDescription());
        form.setStartDate(project.getStartDate());
        return form;
    }

    @Override
    public Project create(ProjectForm form) {
        Project project = new Project();
        mapFormToProject(project, form);
        return projectRepository.save(project);
    }

    @Override
    public Project update(Long id, ProjectForm form) {
        Project project = projectRepository.findById(id).orElseThrow();
        mapFormToProject(project, form);
        return projectRepository.save(project);
    }

    @Override
    public void delete(Long id) {
        projectRepository.deleteById(id);
    }

    private void mapFormToProject(Project project, ProjectForm form) {
        project.setCategory(categoryRepository.findById(form.getCategoryId()).orElseThrow());
        project.setStatus(statusRepository.findById(form.getStatusId()).orElseThrow());
        project.setClientContact(contactRepository.findById(form.getClientContactId()).orElseThrow());
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

    public void loadFormLookups(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("statuses", statusRepository.findAll());
        model.addAttribute("contacts", contactRepository.findAll());
    }

}

package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.domain.core.Contact;
import ie.universityofgalway.projecttrackingsystem.domain.core.Project;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.ProjectCategory;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.ProjectStatus;
import ie.universityofgalway.projecttrackingsystem.dto.ProjectForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.ContactRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.ProjectRepository;
import ie.universityofgalway.projecttrackingsystem.repository.lookup.ProjectCategoryRepository;
import ie.universityofgalway.projecttrackingsystem.repository.lookup.ProjectStatusRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final ProjectCategoryRepository categoryRepository;
    private final ProjectStatusRepository statusRepository;
    private final ContactRepository contactRepository;

    public ProjectController(ProjectRepository projectRepository,
                             ProjectCategoryRepository categoryRepository,
                             ProjectStatusRepository statusRepository,
                             ContactRepository contactRepository) {
        this.projectRepository = projectRepository;
        this.categoryRepository = categoryRepository;
        this.statusRepository = statusRepository;
        this.contactRepository = contactRepository;
    }

    // helper: load dropdown lists into every form page
    private void loadFormLookups(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("statuses", statusRepository.findAll());
        model.addAttribute("contacts", contactRepository.findAll());
    }

    // GET /projects (LIST)
    @GetMapping
    public String list(Model model) {
        model.addAttribute("projects", projectRepository.findAll());
        return "projects/list";
    }

    // GET /projects/new (CREATE FORM)
    @GetMapping("/new")
    public String newForm(Model model) {
        ProjectForm form = new ProjectForm();
        form.setStartDate(LocalDate.now());   // default start date

        model.addAttribute("projectForm", form);
        model.addAttribute("mode", "create");

        loadFormLookups(model);
        return "projects/form";
    }

    //  POST /projects (CREATE)
    @PostMapping
    public String create(@ModelAttribute ProjectForm form) {

        ProjectCategory category = categoryRepository.findById(form.getCategoryId()).orElseThrow();
        ProjectStatus status = statusRepository.findById(form.getStatusId()).orElseThrow();
        Contact client = contactRepository.findById(form.getClientContactId()).orElseThrow();

        Project p = new Project(category, status, client, form.getStartDate(), form.getTitle());
        p.setDescription(form.getDescription());

        // optional contacts (can be null)
        if (form.getSolicitorContactId() != null) {
            Contact solicitor = contactRepository.findById(form.getSolicitorContactId()).orElseThrow();
            p.setSolicitorContact(solicitor);
        }

        if (form.getInsuranceCompanyContactId() != null) {
            Contact insurer = contactRepository.findById(form.getInsuranceCompanyContactId()).orElseThrow();
            p.setInsuranceCompanyContact(insurer);
        }

        Project saved = projectRepository.save(p);
        return "redirect:/projects/" + saved.getId();
    }

    // GET /projects/{id} (VIEW)
    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        Project p = projectRepository.findById(id).orElseThrow();
        model.addAttribute("project", p);
        return "projects/view";
    }

    //  GET /projects/{id}/edit (EDIT FORM)
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Project p = projectRepository.findById(id).orElseThrow();

        ProjectForm form = new ProjectForm();
        form.setProjectId(p.getId());
        form.setCategoryId(p.getCategory().getId());
        form.setStatusId(p.getStatus().getId());
        form.setClientContactId(p.getClientContact().getId());
        form.setStartDate(p.getStartDate());
        form.setTitle(p.getTitle());
        form.setDescription(p.getDescription());

        if (p.getSolicitorContact() != null) {
            form.setSolicitorContactId(p.getSolicitorContact().getId());
        }

        if (p.getInsuranceCompanyContact() != null) {
            form.setInsuranceCompanyContactId(p.getInsuranceCompanyContact().getId());
        }

        model.addAttribute("projectForm", form);
        model.addAttribute("projectId", id);
        model.addAttribute("mode", "edit");

        loadFormLookups(model);
        return "projects/form";
    }

    // POST /projects/{id} (UPDATE)
    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute ProjectForm form) {

        Project p = projectRepository.findById(id).orElseThrow();

        ProjectCategory category = categoryRepository.findById(form.getCategoryId()).orElseThrow();
        ProjectStatus status = statusRepository.findById(form.getStatusId()).orElseThrow();
        Contact client = contactRepository.findById(form.getClientContactId()).orElseThrow();

        p.setCategory(category);
        p.setStatus(status);
        p.setClientContact(client);
        p.setStartDate(form.getStartDate());
        p.setTitle(form.getTitle());
        p.setDescription(form.getDescription());

        // optional contacts
        if (form.getSolicitorContactId() != null) {
            Contact solicitor = contactRepository.findById(form.getSolicitorContactId()).orElseThrow();
            p.setSolicitorContact(solicitor);
        } else {
            p.setSolicitorContact(null);
        }

        if (form.getInsuranceCompanyContactId() != null) {
            Contact insurer = contactRepository.findById(form.getInsuranceCompanyContactId()).orElseThrow();
            p.setInsuranceCompanyContact(insurer);
        } else {
            p.setInsuranceCompanyContact(null);
        }

        projectRepository.save(p);
        return "redirect:/projects/" + id;
    }

    //  POST /projects/{id}/delete (DELETE)
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        projectRepository.deleteById(id);
        return "redirect:/projects";
    }
}

package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.domain.core.Project;
import ie.universityofgalway.projecttrackingsystem.dto.*;
import ie.universityofgalway.projecttrackingsystem.service.ProjectQueryService;
import ie.universityofgalway.projecttrackingsystem.service.ProjectService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/projects")
public class ProjectController  {

    private final ProjectService projectService;
    private final ProjectQueryService projectQueryService;

    public ProjectController(ProjectService projectService,
                             ProjectQueryService projectQueryService) {
        this.projectService = projectService;
        this.projectQueryService = projectQueryService;
    }

    // New Form
    @GetMapping("/new")
    public String newForm(Model model) {

        model.addAttribute("projectForm", new ProjectForm());
        model.addAttribute("mode", "new");

        projectQueryService.loadFormLookups(model);

        return "projects/form";
    }

    // Create
    @PostMapping
    public String create(@Valid @ModelAttribute("projectForm") ProjectForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("mode", "new");
            projectQueryService.loadFormLookups(model);
            return "projects/form";
        }

        try {
            Project saved = projectService.create(form);

            redirectAttributes.addFlashAttribute(
                    "successMessage", "Project created successfully.");

            return "redirect:/projects/" + saved.getId();

        } catch (IllegalArgumentException ex) {

            bindingResult.reject("error.project", ex.getMessage());

            model.addAttribute("mode", "new");
            projectQueryService.loadFormLookups(model);

            return "projects/form";
        }
    }

    // Edit
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {

        model.addAttribute("projectForm", projectService.getFormById(id));
        model.addAttribute("mode", "edit");
        model.addAttribute("projectId", id);

        projectQueryService.loadFormLookups(model);

        return "projects/form";
    }

    // View
    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {

        ProjectDetailsView view = projectQueryService.getProjectDetails(id);

        model.addAttribute("project", view.getProject());
        model.addAttribute("outlays", view.getOutlays());
        model.addAttribute("expenses", view.getExpenses());
        model.addAttribute("outlayTotal", view.getOutlayTotal());
        model.addAttribute("expenseTotal", view.getExpenseTotal());
        model.addAttribute("timesheets", view.getTimesheets());
        model.addAttribute("labourTotal", view.getLabourTotal());
        model.addAttribute("totalExVat", view.getTotalExVat());
        model.addAttribute("receipts", view.getReceipts());
        model.addAttribute("receiptsTotal", view.getReceiptsTotal());
        model.addAttribute("outstandingInvoices", view.getOutstandingInvoices());
        model.addAttribute("totalInvoiced", view.getTotalInvoiced());
        model.addAttribute("discountTotal", view.getDiscountTotal());
        return "projects/view";
    }

    // Update
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("projectForm") ProjectForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("mode", "edit");
            model.addAttribute("projectId", id);
            projectQueryService.loadFormLookups(model);
            return "projects/form";
        }

        try {
            projectService.update(id, form);

            redirectAttributes.addFlashAttribute(
                    "successMessage", "Project updated successfully.");

            return "redirect:/projects/" + id;

        } catch (IllegalArgumentException ex) {

            bindingResult.reject("error.project", ex.getMessage());

            model.addAttribute("mode", "edit");
            model.addAttribute("projectId", id);
            projectQueryService.loadFormLookups(model);

            return "projects/form";
        }
    }

    // List
    @GetMapping
    public String list(Model model) {

        model.addAttribute("projects", projectQueryService.list());
        model.addAttribute("criteria", new ProjectSearchCriteria());

        projectQueryService.loadFormLookups(model);

        return "projects/list";
    }

    // Search
    @GetMapping("/search")
    public String searchProjects(@Valid @ModelAttribute("criteria") ProjectSearchCriteria criteria,
                                 BindingResult bindingResult,
                                 Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("projects", projectQueryService.list());
            projectQueryService.loadFormLookups(model);
            return "projects/list";
        }

        model.addAttribute("projects",
                projectQueryService.search(criteria));

        model.addAttribute("criteria", criteria);

        projectQueryService.loadFormLookups(model);

        return "projects/list";
    }
}
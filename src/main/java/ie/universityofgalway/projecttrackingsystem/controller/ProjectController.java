package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.dto.ProjectForm;
import ie.universityofgalway.projecttrackingsystem.domain.core.Project;
import ie.universityofgalway.projecttrackingsystem.domain.core.CostItem;
import ie.universityofgalway.projecttrackingsystem.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectController extends BaseController<Project, ProjectForm> {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        super(projectService);
        this.projectService = projectService;
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        ProjectForm form = new ProjectForm();
        model.addAttribute("projectForm", form);
        model.addAttribute("mode", "new");
        projectService.loadFormLookups(model);
        return "projects/form";
    }

    @PostMapping
    public String create(@ModelAttribute ProjectForm form) {
        Project saved = projectService.create(form);
        return "redirect:/projects/" + saved.getId();
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("projectForm", projectService.getFormById(id));
        model.addAttribute("mode", "edit");
        model.addAttribute("projectId", id);
        projectService.loadFormLookups(model);
        return "projects/form";
    }

    @GetMapping("/{id}")
    @Override
    public String view(@PathVariable Long id, Model model) {

        Project project = projectService.getById(id);

        List<CostItem> costItems = project.getCostItems();

        // Split by enum type
        List<CostItem> outlays = costItems.stream()
                .filter(c -> c.getType() == CostItem.Type.OUTLAY)
                .toList();

        List<CostItem> expenses = costItems.stream()
                .filter(c -> c.getType() == CostItem.Type.EXPENSE)
                .toList();

        // Calculate subtotals
        BigDecimal outlayTotal = outlays.stream()
                .map(CostItem::getCostAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expenseTotal = expenses.stream()
                .map(CostItem::getCostAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal grandTotal = outlayTotal.add(expenseTotal);

        // Add to model
        model.addAttribute("project", project);
        model.addAttribute("outlays", outlays);
        model.addAttribute("expenses", expenses);
        model.addAttribute("outlayTotal", outlayTotal);
        model.addAttribute("expenseTotal", expenseTotal);
        model.addAttribute("grandTotal", grandTotal);

        return "projects/view";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute ProjectForm form) {
        projectService.update(id, form);
        return "redirect:/projects/" + id;
    }

    @Override
    protected String getListView() { return "projects/list"; }

    @Override
    protected String getDetailsView() { return "projects/view"; }

    @Override
    protected String getBaseUrl() { return "/projects"; }

    @Override
    protected String getListAttributeName() { return "projects"; }

    @Override
    protected String getEntityAttributeName() { return "project"; }
}
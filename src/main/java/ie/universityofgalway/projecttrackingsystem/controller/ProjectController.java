package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.domain.core.TimesheetEntry;
import ie.universityofgalway.projecttrackingsystem.dto.ProjectForm;
import ie.universityofgalway.projecttrackingsystem.domain.core.Project;
import ie.universityofgalway.projecttrackingsystem.domain.core.CostItem;
import ie.universityofgalway.projecttrackingsystem.dto.TimesheetEntryView;
import ie.universityofgalway.projecttrackingsystem.service.ProjectService;
import ie.universityofgalway.projecttrackingsystem.service.TimesheetEntryService;
import ie.universityofgalway.projecttrackingsystem.repository.core.TimesheetEntryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectController extends BaseController<Project, ProjectForm> {

    private final ProjectService projectService;
    private final TimesheetEntryService timesheetService;

    public ProjectController(ProjectService projectService,
                             TimesheetEntryService timesheetService) {
        super(projectService);
        this.projectService = projectService;
        this.timesheetService = timesheetService;
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

        List<CostItem> outlays = costItems.stream()
                .filter(c -> c.getType() == CostItem.Type.OUTLAY)
                .toList();

        List<CostItem> expenses = costItems.stream()
                .filter(c -> c.getType() == CostItem.Type.EXPENSE)
                .toList();

        BigDecimal outlayTotal = outlays.stream()
                .map(CostItem::getCostAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expenseTotal = expenses.stream()
                .map(CostItem::getCostAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //  Get timesheets
        List<TimesheetEntryView> timesheets =
                timesheetService.findByProjectId(id);

        BigDecimal labourTotal = timesheets.stream()
                .map(TimesheetEntryView::getCharge)
                .filter(c -> c != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total excluding VAT
        BigDecimal totalExVat = outlayTotal
                .add(expenseTotal)
                .add(labourTotal);

        // Add to model
        model.addAttribute("project", project);
        model.addAttribute("outlays", outlays);
        model.addAttribute("expenses", expenses);
        model.addAttribute("outlayTotal", outlayTotal);
        model.addAttribute("expenseTotal", expenseTotal);
        model.addAttribute("timesheets", timesheets);
        model.addAttribute("labourTotal", labourTotal);
        model.addAttribute("totalExVat", totalExVat);

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
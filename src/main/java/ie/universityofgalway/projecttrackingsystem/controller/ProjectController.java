package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.domain.core.Project;
import ie.universityofgalway.projecttrackingsystem.domain.core.CostItem;
import ie.universityofgalway.projecttrackingsystem.domain.core.Receipt;
import ie.universityofgalway.projecttrackingsystem.dto.ProjectForm;
import ie.universityofgalway.projecttrackingsystem.dto.ProjectSearchCriteria;
import ie.universityofgalway.projecttrackingsystem.dto.TimesheetEntryView;
import ie.universityofgalway.projecttrackingsystem.repository.core.CostItemRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.ReceiptRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.TimesheetEntryRepository;
import ie.universityofgalway.projecttrackingsystem.service.ProjectService;
import ie.universityofgalway.projecttrackingsystem.service.TimesheetEntryService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectController extends BaseController<Project, ProjectForm> {

    private final ProjectService projectService;
    private final TimesheetEntryService timesheetService;
    private final CostItemRepository costItemRepository;
    private final TimesheetEntryRepository timesheetRepository;
    private final ReceiptRepository receiptRepository;

    public ProjectController(ProjectService projectService,
                             TimesheetEntryService timesheetService,
                             CostItemRepository costItemRepository,
                             TimesheetEntryRepository timesheetRepository,
                             ReceiptRepository receiptRepository) {

        super(projectService);

        this.projectService = projectService;
        this.timesheetService = timesheetService;
        this.costItemRepository = costItemRepository;
        this.timesheetRepository = timesheetRepository;
        this.receiptRepository = receiptRepository;
    }

    // ------------------------------------------------
    // NEW FORM
    // ------------------------------------------------

    @GetMapping("/new")
    public String newForm(Model model) {

        model.addAttribute("projectForm", new ProjectForm());
        model.addAttribute("mode", "new");

        projectService.loadFormLookups(model);

        return "projects/form";
    }

    // ------------------------------------------------
    // CREATE
    // ------------------------------------------------

    @PostMapping
    public String create(@Valid @ModelAttribute("projectForm") ProjectForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("mode", "new");
            projectService.loadFormLookups(model);

            return "projects/form";
        }

        try {

            Project saved = projectService.create(form);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Project created successfully."
            );

            return "redirect:/projects/" + saved.getId();

        } catch (IllegalArgumentException ex) {

            bindingResult.reject("error.project", ex.getMessage());

            model.addAttribute("mode", "new");
            projectService.loadFormLookups(model);

            return "projects/form";
        }
    }

    // ------------------------------------------------
    // EDIT FORM
    // ------------------------------------------------

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {

        model.addAttribute("projectForm", projectService.getFormById(id));
        model.addAttribute("mode", "edit");
        model.addAttribute("projectId", id);

        projectService.loadFormLookups(model);

        return "projects/form";
    }

    // ------------------------------------------------
    // VIEW PROJECT
    // ------------------------------------------------

    @Override
    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {

        Project project = projectService.getById(id);

        List<CostItem> costItems = project.getCostItems();

        List<CostItem> outlays = costItems.stream()
                .filter(c -> c.getType() == CostItem.Type.OUTLAY)
                .toList();

        List<CostItem> expenses = costItems.stream()
                .filter(c -> c.getType() == CostItem.Type.EXPENSE)
                .toList();

        BigDecimal outlayTotal = costItemRepository.sumOutlaysByProjectId(id);
        BigDecimal expenseTotal = costItemRepository.sumExpensesByProjectId(id);

        if (outlayTotal == null) outlayTotal = BigDecimal.ZERO;
        if (expenseTotal == null) expenseTotal = BigDecimal.ZERO;

        List<TimesheetEntryView> timesheets =
                timesheetService.findByProjectId(id);

        BigDecimal labourTotal = timesheetRepository.sumChargesByProjectId(id);

        if (labourTotal == null) labourTotal = BigDecimal.ZERO;

        BigDecimal totalExVat = outlayTotal
                .add(expenseTotal)
                .add(labourTotal);

        // -------------------------------
        // RECEIPTS
        // -------------------------------

        List<Receipt> receipts = receiptRepository.findByInvoiceProjectId(id);

        BigDecimal receiptsTotal = receipts.stream()
                .map(Receipt::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // -------------------------------

        model.addAttribute("project", project);
        model.addAttribute("outlays", outlays);
        model.addAttribute("expenses", expenses);
        model.addAttribute("outlayTotal", outlayTotal);
        model.addAttribute("expenseTotal", expenseTotal);
        model.addAttribute("timesheets", timesheets);
        model.addAttribute("labourTotal", labourTotal);
        model.addAttribute("totalExVat", totalExVat);

        model.addAttribute("receipts", receipts);
        model.addAttribute("receiptsTotal", receiptsTotal);

        return "projects/view";
    }

    // ------------------------------------------------
    // UPDATE
    // ------------------------------------------------

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("projectForm") ProjectForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("mode", "edit");
            model.addAttribute("projectId", id);
            projectService.loadFormLookups(model);

            return "projects/form";
        }

        try {

            projectService.update(id, form);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Project updated successfully."
            );

            return "redirect:/projects/" + id;

        } catch (IllegalArgumentException ex) {

            bindingResult.reject("error.project", ex.getMessage());

            model.addAttribute("mode", "edit");
            model.addAttribute("projectId", id);
            projectService.loadFormLookups(model);

            return "projects/form";
        }
    }

    // ------------------------------------------------
    // DELETE
    // ------------------------------------------------

    @Override
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         RedirectAttributes redirectAttributes) {

        try {

            projectService.delete(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Project deleted successfully."
            );

        } catch (IllegalStateException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage()
            );
        }

        return "redirect:/projects";
    }

    // ------------------------------------------------
    // LIST PROJECTS
    // ------------------------------------------------

    @Override
    @GetMapping
    public String list(Model model) {

        ProjectSearchCriteria criteria = new ProjectSearchCriteria();

        List<Project> projects = projectService.list();

        model.addAttribute("projects", projects);
        model.addAttribute("criteria", criteria);

        projectService.loadFormLookups(model);

        return "projects/list";
    }

    // ------------------------------------------------
    // SEARCH PROJECTS
    // ------------------------------------------------

    @GetMapping("/search")
    public String searchProjects(@ModelAttribute ProjectSearchCriteria criteria,
                                 Model model) {

        List<Project> projects = projectService.searchProjects(criteria);

        model.addAttribute("projects", projects);
        model.addAttribute("criteria", criteria);

        projectService.loadFormLookups(model);

        return "projects/list";
    }

    @Override
    protected String getListView() {
        return "projects/list";
    }

    @Override
    protected String getDetailsView() {
        return "projects/view";
    }

    @Override
    protected String getBaseUrl() {
        return "/projects";
    }

    @Override
    protected String getListAttributeName() {
        return "projects";
    }

    @Override
    protected String getEntityAttributeName() {
        return "project";
    }
}
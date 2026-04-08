package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.dto.TimesheetEntryForm;
import ie.universityofgalway.projecttrackingsystem.dto.TimesheetEntryView;
import ie.universityofgalway.projecttrackingsystem.service.TimesheetEntryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/timesheet-entries")
public class TimesheetEntryController extends BaseController<TimesheetEntryView, TimesheetEntryForm> {

    private final TimesheetEntryService timesheetEntryService;

    // Constructor
    public TimesheetEntryController(TimesheetEntryService timesheetEntryService) {
        super(timesheetEntryService);
        this.timesheetEntryService = timesheetEntryService;
    }

    @Override
    protected String getListView() {
        return "timesheet/list";
    }

    @Override
    protected String getDetailsView() {
        return "timesheet/view";
    }

    @Override
    protected String getBaseUrl() {
        return "/timesheet-entries";
    }

    @Override
    protected String getListAttributeName() {
        return "entries";
    }

    @Override
    protected String getEntityAttributeName() {
        return "entry";
    }

    // Create New Form
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new TimesheetEntryForm());
        model.addAttribute("isEdit", false);
        model.addAllAttributes(timesheetEntryService.getFormLookups());
        return "timesheet/form";
    }

    // Handle Create
    @PostMapping
    public String create(@Valid @ModelAttribute("form") TimesheetEntryForm form,
                         BindingResult result,
                         Model model) {

        if (result.hasErrors()) {
            model.addAllAttributes(timesheetEntryService.getFormLookups());
            model.addAttribute("isEdit", false);
            return "timesheet/form";
        }

        timesheetEntryService.create(form);
        return "redirect:/timesheet-entries";
    }

    // Edit Form
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        TimesheetEntryForm form = timesheetEntryService.getFormById(id);

        model.addAttribute("form", form);
        model.addAttribute("isEdit", true);
        model.addAllAttributes(timesheetEntryService.getFormLookups());

        return "timesheet/form";
    }

    // Update
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") TimesheetEntryForm form,
                         BindingResult result,
                         Model model) {

        if (result.hasErrors()) {
            model.addAllAttributes(timesheetEntryService.getFormLookups());
            model.addAttribute("isEdit", true);
            return "timesheet/form";
        }

        try {
            timesheetEntryService.update(id, form);
        } catch (IllegalStateException ex) {
            model.addAttribute("businessError", ex.getMessage());
            model.addAllAttributes(timesheetEntryService.getFormLookups());
            model.addAttribute("isEdit", true);
            return "timesheet/form";
        }

        return "redirect:/timesheet-entries";
    }
}

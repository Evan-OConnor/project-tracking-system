package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.dto.TimesheetEntryForm;
import ie.universityofgalway.projecttrackingsystem.dto.TimesheetEntryView;
import ie.universityofgalway.projecttrackingsystem.service.TimesheetEntryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/timesheet-entries")
public class TimesheetEntryController extends BaseController<TimesheetEntryView, TimesheetEntryForm> {

    private final TimesheetEntryService timesheetEntryService;

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

    // CREATE FORM
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new TimesheetEntryForm());
        model.addAllAttributes(timesheetEntryService.getFormLookups());
        return "timesheet/form";
    }

    // HANDLE CREATE
    @PostMapping
    public String create(@ModelAttribute TimesheetEntryForm form) {
        timesheetEntryService.create(form);
        return "redirect:/timesheet-entries";
    }

    // EDIT FORM
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        TimesheetEntryForm form = timesheetEntryService.getFormById(id);
        model.addAttribute("form", form);
        model.addAllAttributes(timesheetEntryService.getFormLookups());
        return "timesheet/form"; // reuse same form template
    }

    // HANDLE UPDATE
    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute TimesheetEntryForm form) {
        timesheetEntryService.update(id, form);
        return "redirect:/timesheet-entries";
    }
}

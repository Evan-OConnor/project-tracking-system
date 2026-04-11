package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.service.ContactService;
import ie.universityofgalway.projecttrackingsystem.service.EmployeeService;
import ie.universityofgalway.projecttrackingsystem.service.InvoiceService;
import ie.universityofgalway.projecttrackingsystem.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class uiController {

    private final ProjectService projectService;
    private final InvoiceService invoiceService;
    private final ContactService contactService;
    private final EmployeeService employeeService;

    public uiController(ProjectService projectService,
                        InvoiceService invoiceService,
                        ContactService contactService,
                        EmployeeService employeeService) {
        this.projectService = projectService;
        this.invoiceService = invoiceService;
        this.contactService = contactService;
        this.employeeService = employeeService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {

        long totalProjectCount = projectService.getTotalProjectCount();
        long activeProjectCount = projectService.getActiveProjectCount();
        long completedProjectCount = projectService.getProjectCountByStatus("COMPLETED");
        long cancelledProjectCount = projectService.getProjectCountByStatus("CANCELLED");
        long outstandingInvoiceCount = invoiceService.getOutstandingInvoiceCount();
        long contactCount = contactService.getTotalContactCount();
        long employeeCount = employeeService.getTotalEmployeeCount();

        model.addAttribute("totalProjectCount", totalProjectCount);
        model.addAttribute("activeProjectCount", activeProjectCount);
        model.addAttribute("completedProjectCount", completedProjectCount);
        model.addAttribute("cancelledProjectCount", cancelledProjectCount);
        model.addAttribute("outstandingInvoiceCount", outstandingInvoiceCount);
        model.addAttribute("contactCount", contactCount);
        model.addAttribute("employeeCount", employeeCount);
        model.addAttribute("recentProjects", projectService.getRecentProjects());

        return "dashboard";
    }

    @GetMapping("/search")
    public String search() {
        return "search";
    }

    @GetMapping("/timesheets")
    public String timesheets() {
        return "redirect:/timesheet-entries";
    }

    @GetMapping("/expenses")
    public String expenses() {
        return "expenses";
    }

}
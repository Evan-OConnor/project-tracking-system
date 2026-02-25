package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.domain.core.Employee;
import ie.universityofgalway.projecttrackingsystem.dto.EmployeeForm;
import ie.universityofgalway.projecttrackingsystem.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employees")
public class EmployeeController extends BaseController<Employee, EmployeeForm> {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        super(employeeService);
        this.employeeService = employeeService;
    }

    // CREATE FORM
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("employeeForm", new EmployeeForm());
        model.addAttribute("mode", "create");
        return "employees/form";
    }

    // CREATE
    @PostMapping
    public String create(@ModelAttribute EmployeeForm form) {
        Employee saved = employeeService.create(form);
        return "redirect:/employees/" + saved.getId();
    }

    // EDIT FORM
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("employeeForm", employeeService.getFormById(id));
        model.addAttribute("mode", "edit");
        return "employees/form";
    }

    // UPDATE
    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute EmployeeForm form) {
        employeeService.update(id, form);
        return "redirect:/employees/" + id;
    }

    // VIEW DETAILS
    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        Employee employee = employeeService.getById(id); // employee from DB
        model.addAttribute("employee", employee);
        return getDetailsView();
    }


    @Override
    protected String getListView() {
        return "employees/list";
    }

    @Override
    protected String getDetailsView() {
        return "employees/view";
    }

    @Override
    protected String getBaseUrl() {
        return "/employees";
    }

    @Override
    protected String getListAttributeName() {
        return "employees";
    }

    @Override
    protected String getEntityAttributeName() {
        return "employee";
    }
}

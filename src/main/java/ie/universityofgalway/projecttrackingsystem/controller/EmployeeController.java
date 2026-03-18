package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.dto.EmployeeView;
import ie.universityofgalway.projecttrackingsystem.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;


    // Constructor
    public EmployeeController(EmployeeService employeeService) {

        this.employeeService = employeeService;
    }

    // List / Search Employees
    @GetMapping
    public String list(
            @RequestParam(value = "q", required = false) String q,
            Model model) {

        String query = (q == null) ? "" : q.trim();

        List<EmployeeView> employees;

        // if no search query provided, list all employees
        if (query.isEmpty()) {
            employees = employeeService.listSummaries();
        } else {
            employees = employeeService.searchSummaries(query);
        }

        model.addAttribute("employees", employees);
        model.addAttribute("q", query);

        return "employees/list";
    }
}
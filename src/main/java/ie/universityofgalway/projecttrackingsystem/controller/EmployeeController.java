package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q, Model model) {
        String query = (q == null) ? "" : q.trim();
        model.addAttribute("employees", employeeService.searchSummaries(query));
        model.addAttribute("q", query);
        return "employees/list";
    }
}
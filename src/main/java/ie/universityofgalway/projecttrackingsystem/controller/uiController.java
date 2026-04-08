package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class uiController {

    private final ProjectService projectService;

    public uiController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {

        long activeProjectCount = projectService.getActiveProjectCount();

        model.addAttribute("activeProjectCount", activeProjectCount);

        return "dashboard";
    }

    @GetMapping("/search")
    public String search() {
        return "search";
    }

    @GetMapping("/timesheets")
    public String timesheets() {
        return "timesheets";
    }

    @GetMapping("/expenses")
    public String expenses() {
        return "expenses";
    }

}
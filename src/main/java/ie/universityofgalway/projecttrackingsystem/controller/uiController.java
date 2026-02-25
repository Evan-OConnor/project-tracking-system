package ie.universityofgalway.projecttrackingsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class uiController {

    @GetMapping({"/", "/dashboard"})
    public String dashboard() {
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
package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.dto.ProjectForm;
import ie.universityofgalway.projecttrackingsystem.dto.ProjectView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    // TEMP: dummy in-memory list (replace with service/repository when partner finishes entities)
    private static final AtomicLong ID_SEQ = new AtomicLong(3);
    private static final List<ProjectView> DUMMY = new ArrayList<>(List.of(
            new ProjectView(1L, "PRJ-2026-001", "Initial Site Survey", "Placeholder project", "OPEN", LocalDate.now(), null),
            new ProjectView(2L, "PRJ-2026-002", "Insurance Assessment", "Placeholder project", "IN_PROGRESS", LocalDate.now().minusDays(2), null)
    ));

    @GetMapping
    public String list(Model model) {
        model.addAttribute("projects", DUMMY);
        return "projects/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        ProjectForm form = new ProjectForm();
        form.setStatus("OPEN");
        form.setStartDate(LocalDate.now());
        model.addAttribute("projectForm", form);
        model.addAttribute("mode", "create");
        return "projects/form";
    }

    @PostMapping
    public String create(@ModelAttribute ProjectForm projectForm) {
        long id = ID_SEQ.getAndIncrement();
        DUMMY.add(new ProjectView(
                id,
                projectForm.getReference(),
                projectForm.getTitle(),
                projectForm.getDescription(),
                projectForm.getStatus(),
                projectForm.getStartDate(),
                projectForm.getEndDate()
        ));
        return "redirect:/projects";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        ProjectView p = DUMMY.stream().filter(x -> x.getId().equals(id)).findFirst()
                .orElse(new ProjectView(id, "UNKNOWN", "Not found", "", "OPEN", null, null));
        model.addAttribute("project", p);
        return "projects/view";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        ProjectView p = DUMMY.stream().filter(x -> x.getId().equals(id)).findFirst()
                .orElseThrow();

        ProjectForm form = new ProjectForm();
        form.setReference(p.getReference());
        form.setTitle(p.getTitle());
        form.setDescription(p.getDescription());
        form.setStatus(p.getStatus());
        form.setStartDate(p.getStartDate());
        form.setEndDate(p.getEndDate());

        model.addAttribute("projectForm", form);
        model.addAttribute("projectId", id);
        model.addAttribute("mode", "edit");
        return "projects/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute ProjectForm projectForm) {
        for (int i = 0; i < DUMMY.size(); i++) {
            if (DUMMY.get(i).getId().equals(id)) {
                DUMMY.set(i, new ProjectView(
                        id,
                        projectForm.getReference(),
                        projectForm.getTitle(),
                        projectForm.getDescription(),
                        projectForm.getStatus(),
                        projectForm.getStartDate(),
                        projectForm.getEndDate()
                ));
                break;
            }
        }
        return "redirect:/projects/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        DUMMY.removeIf(p -> p.getId().equals(id));
        return "redirect:/projects";
    }
}

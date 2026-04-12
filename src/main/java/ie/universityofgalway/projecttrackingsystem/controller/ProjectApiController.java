package ie.universityofgalway.projecttrackingsystem.controller;
import ie.universityofgalway.projecttrackingsystem.dto.ClientDto;
import ie.universityofgalway.projecttrackingsystem.dto.ProjectSearchDTO;
import ie.universityofgalway.projecttrackingsystem.repository.core.ContactRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.ProjectRepository;
import ie.universityofgalway.projecttrackingsystem.service.ProjectQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectApiController {

    private final ProjectQueryService projectQueryService;

    public ProjectApiController(ProjectQueryService projectQueryService) {
        this.projectQueryService = projectQueryService;
    }

    // Project Search for Form
    @GetMapping("/search")
    public List<ProjectSearchDTO> search(@RequestParam String query) {
        return projectQueryService.searchProjectsForAutocomplete(query);
    }
}
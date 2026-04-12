package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.domain.core.Project;
import ie.universityofgalway.projecttrackingsystem.dto.CostItemForm;
import ie.universityofgalway.projecttrackingsystem.domain.core.CostItem;
import ie.universityofgalway.projecttrackingsystem.repository.core.ProjectRepository;
import ie.universityofgalway.projecttrackingsystem.service.CostItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cost-items")
public class CostItemController extends BaseController<CostItem, CostItemForm> {

    private final CostItemService costItemService;
    private final ProjectRepository projectRepository;

    // Constructor
    public CostItemController(CostItemService costItemService, ProjectRepository projectRepository) {
        super(costItemService);
        this.costItemService = costItemService;
        this.projectRepository = projectRepository;
    }

    // Base Controller
    @Override
    protected String getListView() {
        return "costitems/list";
    }

    @Override
    protected String getDetailsView() {
        return "costitems/details";
    }

    @Override
    protected String getBaseUrl() {
        return "/cost-items";
    }

    @Override
    protected String getListAttributeName() {
        return "costItems";
    }

    @Override
    protected String getEntityAttributeName() {
        return "costItem";
    }

    // List
    @Override
    @GetMapping
    public String list(Model model) {
        model.addAttribute(getListAttributeName(), costItemService.listViews());
        return getListView();
    }

    // View
    @Override
    @GetMapping("/{id:\\d+}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute(getEntityAttributeName(), costItemService.getViewById(id));
        return getDetailsView();
    }

    // Create Form
    @GetMapping("/new")
    public String createForm(Model model) {

        model.addAttribute("costItemForm", new CostItemForm());
        model.addAllAttributes(costItemService.getDropdowns());

        return "costitems/form";
    }

    // Create or Update Save
    @PostMapping
    public String saveCostItem(@Valid @ModelAttribute("costItemForm") CostItemForm form,
                               BindingResult result,
                               Model model) {

        if (result.hasErrors()) {
            model.addAllAttributes(costItemService.getDropdowns());
            return "costitems/form";
        }

        try {
            // If ID is null, create new cost item
            // Otherwise, update existing one
            if (form.getId() == null) {
                costItemService.create(form);
            } else {
                costItemService.update(form.getId(), form);
            }

        } catch (IllegalStateException ex) {

            // Handle business rule violation (e.g. invalid project state)
            model.addAttribute("businessError", ex.getMessage());
            model.addAllAttributes(costItemService.getDropdowns());

            return "costitems/form";
        }

        return "redirect:/cost-items";
    }

    // Edit Form
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {

        model.addAttribute("costItemForm", costItemService.getFormById(id));
        model.addAllAttributes(costItemService.getDropdowns());

        return "costitems/form";
    }

    // Project Search
    @GetMapping("/projects/search")
    @ResponseBody
    public List<Map<String, Object>> searchProjects(@RequestParam String q) {
        return projectRepository
                .findByTitleContainingIgnoreCase(q)
                .stream()
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getId());
                    map.put("name", p.getTitle());
                    return map;
                })
                .toList();
    }
}
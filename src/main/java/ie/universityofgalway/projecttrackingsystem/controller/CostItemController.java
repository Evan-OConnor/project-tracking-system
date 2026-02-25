package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.dto.CostItemForm;
import ie.universityofgalway.projecttrackingsystem.domain.core.CostItem;
import ie.universityofgalway.projecttrackingsystem.service.CostItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/cost-items")
public class CostItemController extends BaseController<CostItem, CostItemForm> {

    private final CostItemService costItemService;

    public CostItemController(CostItemService costItemService) {
        super(costItemService);
        this.costItemService = costItemService;
    }

    // CREATE FORM
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("costItemForm", new CostItemForm());
        model.addAllAttributes(costItemService.getDropdowns());
        return "costitems/form";
    }

    // CREATE or UPDATE
    @PostMapping
    public String save(@Valid @ModelAttribute("costItemForm") CostItemForm form,
                       BindingResult result,
                       Model model) {

        if (result.hasErrors()) {
            model.addAllAttributes(costItemService.getDropdowns());
            return "costitems/form";
        }

        try {

            if (form.getId() == null) {
                costItemService.create(form);
            } else {
                costItemService.update(form.getId(), form);
            }

        } catch (IllegalStateException ex) {

            // Business rule violation
            model.addAttribute("businessError", ex.getMessage());
            model.addAllAttributes(costItemService.getDropdowns());
            return "costitems/form";
        }

        return "redirect:/cost-items";
    }

    // EDIT FORM
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("costItemForm", costItemService.getFormById(id));
        model.addAllAttributes(costItemService.getDropdowns());
        return "costitems/form";
    }

    // DELETE
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Model model) {

        try {
            costItemService.delete(id);
        } catch (IllegalStateException ex) {
            model.addAttribute("deleteError", ex.getMessage());
            return getListView();
        }

        return "redirect:/cost-items";
    }

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
}
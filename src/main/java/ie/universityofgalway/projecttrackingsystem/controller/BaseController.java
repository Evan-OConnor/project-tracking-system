package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.service.BaseService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

public abstract class BaseController<T, F> {

    protected final BaseService<T, F> service;

    protected BaseController(BaseService<T, F> service) {
        this.service = service;
    }

    // LIST
    @GetMapping
    public String list(Model model) {
        model.addAttribute(getListAttributeName(), service.list());
        return getListView();
    }

    // VIEW
    @GetMapping("/{id:\\d+}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute(getEntityAttributeName(), service.getById(id));
        return getDetailsView();
    }

    // DELETE
    @PostMapping("/{id:\\d+}/delete")
    public String delete(@PathVariable Long id,
                         org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        try {

            service.delete(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Record deleted successfully."
            );

        } catch (IllegalStateException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage()
            );

        }

        return "redirect:" + getBaseUrl();
    }

    protected abstract String getListView();
    protected abstract String getDetailsView();
    protected abstract String getBaseUrl();
    protected abstract String getListAttributeName();
    protected abstract String getEntityAttributeName();
}

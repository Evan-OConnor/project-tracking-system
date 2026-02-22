package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.domain.security.SystemUser;
import ie.universityofgalway.projecttrackingsystem.dto.CreateUserForm;
import ie.universityofgalway.projecttrackingsystem.service.security.SystemUserAdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
public class AdminUserController {

    private final SystemUserAdminService systemUserAdminService;

    public AdminUserController(SystemUserAdminService systemUserAdminService) {
        this.systemUserAdminService = systemUserAdminService;
    }

    @GetMapping("/admin/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("createUserForm", new CreateUserForm());
        return "admin/users/new"; // create a Thymeleaf template at this path
    }

    @GetMapping("/admin/users")
    public String usersRoot() {
        // Temporary: redirect to the create form. Later this should show a list page.
        return "redirect:/admin/users/new";
    }

    @PostMapping("/admin/users")
    public String createUser(@Valid @ModelAttribute("createUserForm") CreateUserForm form,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/users/new";
        }

        try {
            SystemUser created = systemUserAdminService.createEmployeeAndSystemUser(form);
            redirectAttributes.addFlashAttribute("createdUsername", created.getUsername());
            return "redirect:/admin/users/new";
        } catch (IllegalArgumentException | IllegalStateException e) {
            bindingResult.reject("createUserError", e.getMessage());
            return "admin/users/new";
        } catch (Exception e) {
            bindingResult.reject("createUserError", "Unexpected error: " + e.getMessage());
            return "admin/users/new";
        }
    }

}

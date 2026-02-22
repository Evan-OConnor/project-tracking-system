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
        model.addAttribute("roles", java.util.List.of("STAFF", "ADMIN"));
        return "admin/users/new"; // create a Thymeleaf template at this path
    }

    @GetMapping("/admin/users")
    public String usersList(Model model) {
        model.addAttribute("users", systemUserAdminService.listAllUsers());
        return "admin/users/list";
    }

    @PostMapping("/admin/users")
    public String createUser(@Valid @ModelAttribute("createUserForm") CreateUserForm form,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (form.getPassword() != null && form.getConfirmPassword() != null && !form.getPassword().equals(form.getConfirmPassword())) {
            // only add the field error if one isn't already present
            if (!bindingResult.hasFieldErrors("confirmPassword")) {
                bindingResult.rejectValue("confirmPassword", "password.mismatch", "Passwords do not match");
            }
        }
        // ensure roles are always present when rendering the form
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", java.util.List.of("STAFF", "ADMIN"));
            return "admin/users/new";
        }

        try {
            SystemUser created = systemUserAdminService.createEmployeeAndSystemUser(form);
            redirectAttributes.addFlashAttribute("createdUsername", created.getUsername());
            return "redirect:/admin/users/new";
        } catch (IllegalArgumentException | IllegalStateException e) {
            bindingResult.reject("createUserError", e.getMessage());
            model.addAttribute("roles", java.util.List.of("STAFF", "ADMIN"));
            return "admin/users/new";
        } catch (Exception e) {
            bindingResult.reject("createUserError", "Unexpected error: " + e.getMessage());
            model.addAttribute("roles", java.util.List.of("STAFF", "ADMIN"));
            return "admin/users/new";
        }
    }


}

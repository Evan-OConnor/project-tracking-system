package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.dto.CreateUserForm;
import ie.universityofgalway.projecttrackingsystem.service.security.SystemUserAdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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

    @PostMapping("/admin/users")
    public String createUser(@Valid @ModelAttribute("createUserForm") CreateUserForm form,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/users/new";
        }

        try {
            systemUserAdminService.createEmployeeAndSystemUser(form);
            return "redirect:/admin/users";
        } catch (IllegalArgumentException | IllegalStateException e) {
            bindingResult.reject("createUserError", e.getMessage());
            return "admin/users/new";
        }
    }

}

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import ie.universityofgalway.projecttrackingsystem.dto.EditUserForm;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Controller responsible for administrative user management.
 *
 * Responsibilities:
 * - Display the create-user and edit-user forms and ensure required model attributes are present.
 * - Handle form submissions to create, update and delete system users and their linked employee records.
 * - Provide paginated listing and search for system users used in the admin UI.
 * - Enforce controller-level safeguards such as preventing edits/deletes of the seeded system admin account.
 *
 * Behaviour:
 * - Delegates business rules and persistence to {@link SystemUserAdminService}.
 * - Performs presentation-layer validation and converts service exceptions into user-facing errors.
 */
@Controller
public class AdminUserController {

    /** Username of the seeded system admin (cannot be edited or deleted) */
    private static final String SEEDED_ADMIN_USERNAME = "U000001";

    /** Available roles for the role dropdown */
    private static final List<String> AVAILABLE_ROLES = List.of("STAFF", "ADMIN");

    /** Number of users displayed per page */
    private static final int PAGE_SIZE = 50;

    private final SystemUserAdminService systemUserAdminService;

    public AdminUserController(SystemUserAdminService systemUserAdminService) {
        this.systemUserAdminService = systemUserAdminService;
    }

    @GetMapping("/admin/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("createUserForm", new CreateUserForm());
        model.addAttribute("roles", AVAILABLE_ROLES);
        return "admin/users/new";
    }

    @GetMapping("/admin/users")
    public String usersList(@RequestParam(value = "q", required = false) String q,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            Model model) {
        Page<SystemUser> usersPage = systemUserAdminService.searchUsers(q, PageRequest.of(page, PAGE_SIZE));
        model.addAttribute("usersPage", usersPage);
        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("q", q);
        model.addAttribute("seededAdminUsername", SEEDED_ADMIN_USERNAME);
        return "admin/users/list";
    }

    @GetMapping("/admin/users/edit")
    public String editUserForm(Long employeeId, Model model, RedirectAttributes redirectAttributes) {
        if (employeeId == null) {
            redirectAttributes.addFlashAttribute("error", "Employee id required");
            return "redirect:/admin/users";
        }

        // load a form DTO populated from the user/employee
        Optional<EditUserForm> maybe = systemUserAdminService.loadEditFormForEmployee(employeeId);
        if (maybe.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Employee not found");
            return "redirect:/admin/users";
        }

        EditUserForm form = maybe.get();
        // block editing the seeded system admin
        Optional<SystemUser> userOpt = systemUserAdminService.findUserByEmployeeId(employeeId);
        if (userOpt.isPresent() && SEEDED_ADMIN_USERNAME.equals(userOpt.get().getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Editing the system admin is not permitted");
            return "redirect:/admin/users";
        }

        model.addAttribute("editUserForm", form);
        return "admin/users/edit";
    }

    @PostMapping("/admin/users/edit")
    public String submitEdit(@Valid @ModelAttribute("editUserForm") EditUserForm form,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        // explicit field-level validation for password length and confirmation so template shows errors next to fields
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            if (form.getPassword().length() < 8) {
                if (!bindingResult.hasFieldErrors("password")) {
                    bindingResult.rejectValue("password", "password.short", "Password must be at least 8 characters long");
                }
            }
            if (form.getConfirmPassword() == null || form.getConfirmPassword().isBlank() || !form.getPassword().equals(form.getConfirmPassword())) {
                if (!bindingResult.hasFieldErrors("confirmPassword")) {
                    bindingResult.rejectValue("confirmPassword", "password.mismatch", "Passwords do not match");
                }
            }
        }

        if (bindingResult.hasErrors()) {
            return "admin/users/edit";
        }

        // prevent editing seeded admin by employee id
        Optional<SystemUser> userOpt = systemUserAdminService.findUserByEmployeeId(form.getEmployeeId());
        if (userOpt.isPresent() && SEEDED_ADMIN_USERNAME.equals(userOpt.get().getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Editing the system admin is not permitted");
            return "redirect:/admin/users";
        }

        try {
            SystemUser updated = systemUserAdminService.updateEmployeeAndUser(form);
            redirectAttributes.addFlashAttribute("message", "Updated user: " + updated.getUsername());
            return "redirect:/admin/users";
        } catch (Exception e) {
            bindingResult.reject("editError", e.getMessage());
            return "admin/users/edit";
        }
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
            model.addAttribute("roles", AVAILABLE_ROLES);
            return "admin/users/new";
        }

        try {
            SystemUser created = systemUserAdminService.createEmployeeAndSystemUser(form);
            redirectAttributes.addFlashAttribute("createdUsername", created.getUsername());
            return "redirect:/admin/users/new";
        } catch (IllegalArgumentException | IllegalStateException e) {
            bindingResult.reject("createUserError", e.getMessage());
            model.addAttribute("roles", AVAILABLE_ROLES);
            return "admin/users/new";
        } catch (Exception e) {
            bindingResult.reject("createUserError", "Unexpected error: " + e.getMessage());
            model.addAttribute("roles", AVAILABLE_ROLES);
            return "admin/users/new";
        }
    }

    @PostMapping("/admin/users/delete")
    public String deleteUser(@ModelAttribute(value = "editUserForm", binding = false) EditUserForm form,
                             @RequestParam(value = "employeeId", required = false) Long employeeId,
                             RedirectAttributes redirectAttributes) {
        // allow employeeId to come either from the edit form model or directly as a request param
        if (employeeId == null && form != null) {
            employeeId = form.getEmployeeId();
        }
        if (employeeId == null) {
            redirectAttributes.addFlashAttribute("error", "Employee id required");
            return "redirect:/admin/users";
        }

        // protect seeded system admin
        Optional<SystemUser> userOpt = systemUserAdminService.findUserByEmployeeId(employeeId);
        if (userOpt.isPresent() && SEEDED_ADMIN_USERNAME.equals(userOpt.get().getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Deleting the system admin is not permitted");
            return "redirect:/admin/users";
        }

        try {
            String deletedUsername = userOpt.map(SystemUser::getUsername).orElse("User");
            systemUserAdminService.deleteEmployeeAndAccount(employeeId);
            redirectAttributes.addFlashAttribute("message", "Deleted user: " + deletedUsername);
        } catch (DataIntegrityViolationException dive) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete employee because related records exists.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Could not delete employee: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }
}

package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.config.SecurityConfig;
import ie.universityofgalway.projecttrackingsystem.domain.core.Employee;
import ie.universityofgalway.projecttrackingsystem.domain.security.SystemRole;
import ie.universityofgalway.projecttrackingsystem.domain.security.SystemUser;
import ie.universityofgalway.projecttrackingsystem.service.security.SystemUserAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Security integration tests for {@link AdminUserController}.
 *
 * Verifies access control and security behaviour, including:
 * - Anonymous users are redirected to login
 * - STAFF role users receive 403 Forbidden for admin endpoints
 * - ADMIN role users can access admin pages
 * - CSRF protection is enforced on POST requests
 * - Form validation returns appropriate error responses
 * - Seeded admin (U000001) cannot be edited or deleted
 *
 * Ensures Spring Security rules are correctly applied to admin endpoints.
 */
@WebMvcTest(AdminUserController.class)
@Import(SecurityConfig.class)
class AdminUserControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SystemUserAdminService systemUserAdminService;

    // --- Access control: anonymous user, STAFF role and ADMIN role ---

    @Test
    @WithAnonymousUser
    void adminUsersPage_anonymousUser_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "U000002", roles = {"STAFF"})
    void adminUsersPage_staffUser_returnsForbidden() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "U000001", roles = {"ADMIN"})
    void adminUsersPage_adminUser_returnsOk() throws Exception {
        Employee emp = new Employee("Test", BigDecimal.valueOf(25.00));
        SystemRole role = new SystemRole("ADMIN");
        SystemUser user = new SystemUser(emp, role, "hash");
        user.setUsername("U000001");

        Page<SystemUser> page = new PageImpl<>(Collections.singletonList(user), PageRequest.of(0, 50), 1);
        when(systemUserAdminService.searchUsers(any(), any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/list"));
    }

    // --- CSRF protection ---

    @Test
    @WithMockUser(username = "U000001", roles = {"ADMIN"})
    void createUser_withoutCsrf_returnsForbidden() throws Exception {
        mockMvc.perform(post("/admin/users")
                        .param("employeeName", "Test")
                        .param("hourlyRate", "25.00")
                        .param("password", "pass123")
                        .param("confirmPassword", "pass123")
                        .param("roleName", "STAFF"))
                .andExpect(status().isForbidden());
    }

    // --- POST: happy path ---

    @Test
    @WithMockUser(username = "U000001", roles = {"ADMIN"})
    void createUser_validForm_redirectsWithSuccess() throws Exception {
        Employee emp = new Employee("New Employee", BigDecimal.valueOf(30.00));
        SystemRole role = new SystemRole("STAFF");
        SystemUser createdUser = new SystemUser(emp, role, "hash");
        createdUser.setUsername("U000099");

        when(systemUserAdminService.createEmployeeAndSystemUser(any()))
                .thenReturn(createdUser);

        mockMvc.perform(post("/admin/users")
                        .with(csrf())
                        .param("employeeName", "New Employee")
                        .param("hourlyRate", "30.00")
                        .param("password", "password123")
                        .param("confirmPassword", "password123")
                        .param("roleName", "STAFF"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users/new"))
                .andExpect(flash().attributeExists("createdUsername"));
    }

    // --- POST: validation errors ---

    @Test
    @WithMockUser(username = "U000001", roles = {"ADMIN"})
    void createUser_validationErrors_returnsFormWithErrors() throws Exception {
        mockMvc.perform(post("/admin/users")
                        .with(csrf())
                        .param("employeeName", "")
                        .param("hourlyRate", "")
                        .param("password", "short")
                        .param("confirmPassword", "short")
                        .param("roleName", "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/new"))
                .andExpect(model().attributeHasFieldErrors("createUserForm", "employeeName", "hourlyRate", "password"));
    }

    @Test
    @WithMockUser(username = "U000001", roles = {"ADMIN"})
    void createUser_passwordMismatch_returnsFormWithError() throws Exception {
        mockMvc.perform(post("/admin/users")
                        .with(csrf())
                        .param("employeeName", "Test User")
                        .param("hourlyRate", "25.00")
                        .param("password", "password123")
                        .param("confirmPassword", "differentPassword")
                        .param("roleName", "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/new"))
                .andExpect(model().attributeHasFieldErrors("createUserForm", "confirmPassword"));
    }

    // --- Seeded admin protection ---

    @Test
    @WithMockUser(username = "U000001", roles = {"ADMIN"})
    void editSeededAdmin_returnsRedirectWithError() throws Exception {
        Employee adminEmp = new Employee("System Admin", BigDecimal.valueOf(50.00));
        SystemRole adminRole = new SystemRole("ADMIN");
        SystemUser seededAdmin = new SystemUser(adminEmp, adminRole, "hash");
        seededAdmin.setUsername("U000001");

        when(systemUserAdminService.findUserByEmployeeId(1L))
                .thenReturn(Optional.of(seededAdmin));

        mockMvc.perform(get("/admin/users/edit")
                        .param("employeeId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithMockUser(username = "U000001", roles = {"ADMIN"})
    void deleteSeededAdmin_returnsRedirectWithError() throws Exception {
        Employee adminEmp = new Employee("System Admin", BigDecimal.valueOf(50.00));
        SystemRole adminRole = new SystemRole("ADMIN");
        SystemUser seededAdmin = new SystemUser(adminEmp, adminRole, "hash");
        seededAdmin.setUsername("U000001");

        when(systemUserAdminService.findUserByEmployeeId(1L))
                .thenReturn(Optional.of(seededAdmin));

        mockMvc.perform(post("/admin/users/delete")
                        .with(csrf())
                        .param("employeeId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andExpect(flash().attribute("error", "Deleting the system admin is not permitted"));
    }

    @Test
    @WithMockUser(username = "U000001", roles = {"ADMIN"})
    void editNonSeededAdmin_proceedsNormally() throws Exception {
        Employee emp = new Employee("Regular User", BigDecimal.valueOf(25.00));
        SystemRole staffRole = new SystemRole("STAFF");
        SystemUser regularUser = new SystemUser(emp, staffRole, "hash");
        regularUser.setUsername("U000002");

        when(systemUserAdminService.findUserByEmployeeId(2L))
                .thenReturn(Optional.of(regularUser));
        when(systemUserAdminService.loadEditFormForEmployee(2L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/admin/users/edit")
                        .param("employeeId", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));
    }
}

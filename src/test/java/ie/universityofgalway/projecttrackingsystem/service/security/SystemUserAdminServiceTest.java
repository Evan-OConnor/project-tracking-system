package ie.universityofgalway.projecttrackingsystem.service.security;

import ie.universityofgalway.projecttrackingsystem.domain.core.Employee;
import ie.universityofgalway.projecttrackingsystem.domain.security.SystemRole;
import ie.universityofgalway.projecttrackingsystem.domain.security.SystemUser;
import ie.universityofgalway.projecttrackingsystem.dto.CreateUserForm;
import ie.universityofgalway.projecttrackingsystem.dto.EditUserForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.EmployeeRepository;
import ie.universityofgalway.projecttrackingsystem.repository.security.SystemRoleRepository;
import ie.universityofgalway.projecttrackingsystem.repository.security.SystemUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.dao.DataIntegrityViolationException;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link SystemUserAdminService}.
 *
 * Verifies business logic for user administration, including:
 * - Employee + SystemUser creation
 * - Username generation
 * - Password encoding and confirmation validation
 * - Update behaviour (including optional password changes)
 * - Deletion of linked employee and account
 * - Search and retrieval operations
 *
 * Ensures validation rules and repository interactions behave as expected.
 */

@ExtendWith(MockitoExtension.class)
class SystemUserAdminServiceTest {

    @Mock
    private SystemUserRepository userRepository;

    @Mock
    private SystemRoleRepository roleRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SystemUserAdminService service;

    private SystemRole staffRole;
    private Employee savedEmployee;

    // Set private id field via reflection

    private void setId(Object entity, Long value) throws Exception {
        Field field = entity.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(entity, value);
    }

    @BeforeEach
    void setUp() throws Exception {
        staffRole = new SystemRole("STAFF");
        setId(staffRole, 1L);

        savedEmployee = new Employee("Jane Doe", new BigDecimal("25.00"));
        setId(savedEmployee, 42L);
    }

    //  --- createEmployeeAndSystemUser ---

    @Test
    void createEmployeeAndSystemUser_happyPath_savesEmployeeAndUser() {
        // Arrange
        CreateUserForm form = new CreateUserForm();
        form.setEmployeeName("Jane Doe");
        form.setHourlyRate(new BigDecimal("25.00"));
        form.setAddress("123 Main St");
        form.setPassword("securePass1");
        form.setConfirmPassword("securePass1");
        form.setRoleName("STAFF");

        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);
        when(roleRepository.findByName("STAFF")).thenReturn(Optional.of(staffRole));
        when(passwordEncoder.encode("securePass1")).thenReturn("ENCODED_PW");
        when(userRepository.save(any(SystemUser.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        SystemUser result = service.createEmployeeAndSystemUser(form);

        // Assert
        verify(employeeRepository).save(any(Employee.class));
        verify(roleRepository).findByName("STAFF");
        verify(passwordEncoder).encode("securePass1");
        verify(userRepository, times(1)).save(any(SystemUser.class));

        assertEquals("ENCODED_PW", result.getPasswordHash());
        assertEquals(savedEmployee, result.getEmployee());
        assertTrue(result.isActive());
        assertEquals("U000042", result.getUsername());
    }

    @Test
    void createEmployeeAndSystemUser_blankPassword_throwsException() {
        CreateUserForm form = new CreateUserForm();
        form.setEmployeeName("Bob");
        form.setHourlyRate(new BigDecimal("20.00"));
        form.setPassword("");
        form.setConfirmPassword("");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createEmployeeAndSystemUser(form));

        assertEquals("Password is required", ex.getMessage());
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void createEmployeeAndSystemUser_nullPassword_throwsException() {
        CreateUserForm form = new CreateUserForm();
        form.setEmployeeName("Bob");
        form.setHourlyRate(new BigDecimal("20.00"));
        form.setPassword(null);
        form.setConfirmPassword(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createEmployeeAndSystemUser(form));

        assertEquals("Password is required", ex.getMessage());
    }

    @Test
    void createEmployeeAndSystemUser_blankName_throwsException() {
        CreateUserForm form = new CreateUserForm();
        form.setEmployeeName("");
        form.setHourlyRate(new BigDecimal("20.00"));
        form.setPassword("securePass1");
        form.setConfirmPassword("securePass1");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createEmployeeAndSystemUser(form));

        assertEquals("Employee name is required", ex.getMessage());
    }

    @Test
    void createEmployeeAndSystemUser_nullHourlyRate_throwsException() {
        CreateUserForm form = new CreateUserForm();
        form.setEmployeeName("Charlie");
        form.setHourlyRate(null);
        form.setPassword("securePass1");
        form.setConfirmPassword("securePass1");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createEmployeeAndSystemUser(form));

        assertEquals("Hourly rate is required", ex.getMessage());
    }

    @Test
    void createEmployeeAndSystemUser_roleNotFound_throwsException() {
        CreateUserForm form = new CreateUserForm();
        form.setEmployeeName("Dave");
        form.setHourlyRate(new BigDecimal("30.00"));
        form.setPassword("securePass1");
        form.setConfirmPassword("securePass1");
        form.setRoleName("NONEXISTENT");

        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);
        when(roleRepository.findByName("NONEXISTENT")).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.createEmployeeAndSystemUser(form));

        assertTrue(ex.getMessage().contains("Role not found"));
    }

    @Test
    void createEmployeeAndSystemUser_dataIntegrityViolation_throwsIllegalStateException() {
        CreateUserForm form = new CreateUserForm();
        form.setEmployeeName("Eve");
        form.setHourlyRate(new BigDecimal("35.00"));
        form.setPassword("securePass1");
        form.setConfirmPassword("securePass1");
        form.setRoleName("STAFF");

        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);
        when(roleRepository.findByName("STAFF")).thenReturn(Optional.of(staffRole));
        when(passwordEncoder.encode("securePass1")).thenReturn("ENCODED_PW");
        when(userRepository.save(any(SystemUser.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate key"));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.createEmployeeAndSystemUser(form));

        assertTrue(ex.getMessage().contains("Could not create user"));
    }

    @Test
    void createEmployeeAndSystemUser_passwordsDontMatch_throwsException() {
        CreateUserForm form = new CreateUserForm();
        form.setEmployeeName("Jane Doe");
        form.setHourlyRate(new BigDecimal("25.00"));
        form.setPassword("password123");
        form.setConfirmPassword("different");
        form.setRoleName("STAFF");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createEmployeeAndSystemUser(form));

        assertEquals("Passwords do not match", ex.getMessage());
        verifyNoInteractions(employeeRepository);
    }

    //  --- deleteEmployeeAndAccount ---

    @Test
    void deleteEmployeeAndAccount_existingUser_deletesBoth() throws Exception {
        SystemUser user = new SystemUser(savedEmployee, staffRole, "hash");
        setId(user, 42L);

        when(userRepository.findByEmployeeId(42L)).thenReturn(Optional.of(user));
        when(employeeRepository.findById(42L)).thenReturn(Optional.of(savedEmployee));

        service.deleteEmployeeAndAccount(42L);

        verify(userRepository).deleteByEmployeeId(42L);
        verify(employeeRepository).deleteById(42L);
    }

    @Test
    void deleteEmployeeAndAccount_nothingExists_noExceptions() {
        when(userRepository.findByEmployeeId(999L)).thenReturn(Optional.empty());
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> service.deleteEmployeeAndAccount(999L));

        verify(userRepository, never()).deleteByEmployeeId(anyLong());
        verify(employeeRepository, never()).deleteById(anyLong());
    }

    //  --- listAllUsers ---

    @Test
    void listAllUsers_returnsAll() throws Exception {
        SystemUser u1 = new SystemUser(savedEmployee, staffRole, "h1");
        setId(u1, 42L);  // matches savedEmployee.id

        Employee emp2 = new Employee("Bob", new BigDecimal("30.00"));
        setId(emp2, 43L);
        SystemUser u2 = new SystemUser(emp2, staffRole, "h2");
        setId(u2, 43L);  // matches emp2.id

        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        List<SystemUser> result = service.listAllUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    //  --- searchUsers ---

    @Test
    void searchUsers_withQuery_callsSearchRepo() {
        when(userRepository.searchByUsernameOrEmployeeName("alice"))
                .thenReturn(List.of());

        List<SystemUser> result = service.searchUsers("alice");

        assertEquals(0, result.size());
        verify(userRepository).searchByUsernameOrEmployeeName("alice");
        verify(userRepository, never()).findAll();
    }

    @Test
    void searchUsers_blankQuery_returnsAll() {
        when(userRepository.findAll()).thenReturn(List.of());

        service.searchUsers("   ");

        verify(userRepository).findAll();
        verify(userRepository, never()).searchByUsernameOrEmployeeName(anyString());
    }

    @Test
    void searchUsers_nullQuery_returnsAll() {
        when(userRepository.findAll()).thenReturn(List.of());

        service.searchUsers(null);

        verify(userRepository).findAll();
    }

    //  --- findUsersByEmployeeId ---

    @Test
    void findUserByEmployeeId_existing_returnsUser() throws Exception {
        SystemUser user = new SystemUser(savedEmployee, staffRole, "hash");
        setId(user, 42L);

        when(userRepository.findByEmployeeId(42L)).thenReturn(Optional.of(user));

        Optional<SystemUser> result = service.findUserByEmployeeId(42L);

        assertTrue(result.isPresent());
        assertEquals(savedEmployee, result.get().getEmployee());
    }

    @Test
    void findUserByEmployeeId_notFound_returnsEmpty() {
        when(userRepository.findByEmployeeId(999L)).thenReturn(Optional.empty());

        Optional<SystemUser> result = service.findUserByEmployeeId(999L);

        assertTrue(result.isEmpty());
    }

    // loadEditFormForEmployee

    @Test
    void loadEditFormForEmployee_existing_returnsPopulatedForm() throws Exception {
        savedEmployee.setAddress("456 Oak Ave");

        SystemUser user = new SystemUser(savedEmployee, staffRole, "hash");
        setId(user, 42L);
        user.setActive(true);

        when(userRepository.findByEmployeeId(42L)).thenReturn(Optional.of(user));

        Optional<EditUserForm> result = service.loadEditFormForEmployee(42L);

        assertTrue(result.isPresent());
        EditUserForm form = result.get();
        assertEquals(42L, form.getEmployeeId());
        assertEquals("Jane Doe", form.getEmployeeName());
        assertEquals(new BigDecimal("25.00"), form.getHourlyRate());
        assertEquals("456 Oak Ave", form.getAddress());
        assertTrue(form.isActive());
    }

    @Test
    void loadEditFormForEmployee_notFound_returnsEmpty() {
        when(userRepository.findByEmployeeId(999L)).thenReturn(Optional.empty());

        Optional<EditUserForm> result = service.loadEditFormForEmployee(999L);

        assertTrue(result.isEmpty());
    }

    // updateEmployeeAndUser

    @Test
    void updateEmployeeAndUser_withNewPassword_encodesAndSaves() throws Exception {
        SystemUser existingUser = new SystemUser(savedEmployee, staffRole, "OLD_HASH");
        setId(existingUser, 42L);
        existingUser.setActive(true);

        when(employeeRepository.findById(42L)).thenReturn(Optional.of(savedEmployee));
        when(userRepository.findByEmployeeId(42L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPassword1")).thenReturn("NEW_HASH");
        when(userRepository.save(any(SystemUser.class))).thenAnswer(i -> i.getArgument(0));

        EditUserForm form = new EditUserForm();
        form.setEmployeeId(42L);
        form.setEmployeeName("Alice Updated");
        form.setHourlyRate(new BigDecimal("28.00"));
        form.setAddress("789 Elm St");
        form.setPassword("newPassword1");
        form.setConfirmPassword("newPassword1");
        form.setActive(false);

        SystemUser result = service.updateEmployeeAndUser(form);

        // Employee fields updated
        assertEquals("Alice Updated", savedEmployee.getName());
        assertEquals(new BigDecimal("28.00"), savedEmployee.getHourlyRate());
        assertEquals("789 Elm St", savedEmployee.getAddress());
        verify(employeeRepository).save(savedEmployee);

        // User password re-encoded and active changed
        assertEquals("NEW_HASH", result.getPasswordHash());
        assertFalse(result.isActive());
        verify(passwordEncoder).encode("newPassword1");
    }

    @Test
    void updateEmployeeAndUser_blankPassword_keepsOldHash() throws Exception {
        SystemUser existingUser = new SystemUser(savedEmployee, staffRole, "OLD_HASH");
        setId(existingUser, 42L);

        when(employeeRepository.findById(42L)).thenReturn(Optional.of(savedEmployee));
        when(userRepository.findByEmployeeId(42L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(SystemUser.class))).thenAnswer(i -> i.getArgument(0));

        EditUserForm form = new EditUserForm();
        form.setEmployeeId(42L);
        form.setEmployeeName("Jane Doe");
        form.setHourlyRate(new BigDecimal("25.00"));
        form.setPassword("");           // blank → keep old
        form.setConfirmPassword("");
        form.setActive(true);

        SystemUser result = service.updateEmployeeAndUser(form);

        // Password should NOT have been re-encoded
        assertEquals("OLD_HASH", result.getPasswordHash());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void updateEmployeeAndUser_nullPassword_keepsOldHash() throws Exception {
        SystemUser existingUser = new SystemUser(savedEmployee, staffRole, "OLD_HASH");
        setId(existingUser, 42L);

        when(employeeRepository.findById(42L)).thenReturn(Optional.of(savedEmployee));
        when(userRepository.findByEmployeeId(42L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(SystemUser.class))).thenAnswer(i -> i.getArgument(0));

        EditUserForm form = new EditUserForm();
        form.setEmployeeId(42L);
        form.setEmployeeName("Jane Doe");
        form.setHourlyRate(new BigDecimal("25.00"));
        form.setPassword(null);         // null → keep old
        form.setConfirmPassword(null);
        form.setActive(true);

        SystemUser result = service.updateEmployeeAndUser(form);

        assertEquals("OLD_HASH", result.getPasswordHash());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void updateEmployeeAndUser_employeeNotFound_throwsException() {
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        EditUserForm form = new EditUserForm();
        form.setEmployeeId(999L);
        form.setEmployeeName("Ghost");
        form.setHourlyRate(new BigDecimal("10.00"));

        assertThrows(IllegalArgumentException.class,
                () -> service.updateEmployeeAndUser(form));
    }

    @Test
    void updateEmployeeAndUser_userNotFound_throwsException() {
        when(employeeRepository.findById(42L)).thenReturn(Optional.of(savedEmployee));
        when(userRepository.findByEmployeeId(42L)).thenReturn(Optional.empty());

        EditUserForm form = new EditUserForm();
        form.setEmployeeId(42L);
        form.setEmployeeName("Alice");
        form.setHourlyRate(new BigDecimal("25.00"));

        assertThrows(IllegalStateException.class,
                () -> service.updateEmployeeAndUser(form));
    }

    @Test
    void updateEmployeeAndUser_passwordsDontMatch_throwsException() throws Exception {
        SystemUser existingUser = new SystemUser(savedEmployee, staffRole, "OLD_HASH");
        setId(existingUser, 42L);

        EditUserForm form = new EditUserForm();
        form.setEmployeeId(42L);
        form.setEmployeeName("Jane Doe");
        form.setHourlyRate(new BigDecimal("25.00"));
        form.setPassword("newPassword1");
        form.setConfirmPassword("differentPassword");
        form.setActive(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateEmployeeAndUser(form));

        assertEquals("Passwords do not match", ex.getMessage());
        verifyNoInteractions(employeeRepository);
    }
}

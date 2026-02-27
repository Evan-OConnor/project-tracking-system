package ie.universityofgalway.projecttrackingsystem.service.security;

import ie.universityofgalway.projecttrackingsystem.domain.core.Employee;
import ie.universityofgalway.projecttrackingsystem.domain.security.SystemRole;
import ie.universityofgalway.projecttrackingsystem.domain.security.SystemUser;
import ie.universityofgalway.projecttrackingsystem.dto.CreateUserForm;
import ie.universityofgalway.projecttrackingsystem.dto.EditUserForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.EmployeeRepository;
import ie.universityofgalway.projecttrackingsystem.repository.security.SystemRoleRepository;
import ie.universityofgalway.projecttrackingsystem.repository.security.SystemUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for administrative operations around system users and the
 * underlying employee records. Encapsulates business rules such as:
 * - creating an Employee record and an associated SystemUser in a single transaction
 * - generating a username based on employee_id (shared PK design: SystemUser.id = Employee.id)
 * - updating employee details and password for a user (password encoding delegated to PasswordEncoder)
 * - searching and listing users
 * - deleting the user account and associated employee
 *
 * Note: The SystemUser table uses employee_id as its primary key (@MapsId), ensuring
 * a 1:1 relationship with Employee and consistent username generation (U000001 format).
 */
@Service
public class SystemUserAdminService {

    private final SystemUserRepository userRepository;
    private final SystemRoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public SystemUserAdminService(SystemUserRepository userRepository,
                                  SystemRoleRepository roleRepository,
                                  EmployeeRepository employeeRepository,
                                  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create an Employee and an associated SystemUser in a single transactional operation.
     * Steps:
     * 1. Basic validation of required fields (password, employee name, hourly rate).
     * 2. Persist an Employee entity.
     * 3. Resolve the requested role and encode the provided password.
     * 4. Create a SystemUser with the employee_id as its primary key (shared PK design).
     *    The username is generated from employee_id in the form U000001.
     *
     * Throws IllegalArgumentException for missing required fields and IllegalStateException
     * when a role cannot be found or when a DB constraint prevents creation.
     */
    @Transactional
    public SystemUser createEmployeeAndSystemUser(CreateUserForm form) {

        // validate password, employee name, hourly rate
        if (form.getPassword() == null || form.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (form.getEmployeeName() == null || form.getEmployeeName().isBlank()) {
            throw new IllegalArgumentException("Employee name is required");
        }

        if (form.getHourlyRate() == null) {
            throw new IllegalArgumentException("Hourly rate is required");
        }

        // create employee
        Employee newEmployee = new Employee(form.getEmployeeName(), form.getHourlyRate());
        newEmployee.setAddress(form.getAddress());
        Employee employee = employeeRepository.save(newEmployee);

        // create user for the employee
        String roleToUse = form.getRoleName();
        SystemRole staffRole = roleRepository.findByName(roleToUse)
                .orElseThrow(() -> new IllegalStateException("Role not found: " + roleToUse));

        String hashPassword = passwordEncoder.encode(form.getPassword());

        SystemUser user = new SystemUser(employee, staffRole, hashPassword);
        user.setActive(true);
        // Username is derived from employee_id (shared PK)
        user.setUsername(String.format("U%06d", employee.getId()));

        try {
            user = userRepository.save(user);
            return user;
        } catch (DataIntegrityViolationException dive) {
            throw new IllegalStateException("Could not create user", dive);
        }
    }

    /**
     * Delete the SystemUser (if present) and then the Employee record. Both deletions
     * are executed within the enclosing transaction. The method first attempts to delete
     * the user by employee id to avoid foreign key constraints when removing the employee.
     */
    @Transactional
    public void deleteEmployeeAndAccount(Long employeeId) {
        userRepository.findByEmployeeId(employeeId).ifPresent(u -> userRepository.deleteByEmployeeId(employeeId));
        employeeRepository.findById(employeeId).ifPresent(e -> employeeRepository.deleteById(employeeId));
    }

    /**
     * Return all system users (no filtering) - used to populate admin lists.
     */
    @Transactional(readOnly = true)
    public List<SystemUser> listAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Search users by username or employee name. If the query is blank, returns
     * the full list via {@link #listAllUsers()}.
     */
    @Transactional(readOnly = true)
    public List<SystemUser> searchUsers(String query) {
        if (query == null || query.isBlank()) return listAllUsers();
        return userRepository.searchByUsernameOrEmployeeName(query.trim());
    }

    /**
     * Search users with pagination support. Returns a Page of users matching the
     * query, or all users if the query is blank.
     */
    @Transactional(readOnly = true)
    public Page<SystemUser> searchUsers(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return userRepository.findAll(pageable);
        }
        return userRepository.searchByUsernameOrEmployeeName(query.trim(), pageable);
    }

    /**
     * Lookup a SystemUser by Employee id.
     */
    @Transactional(readOnly = true)
    public java.util.Optional<SystemUser> findUserByEmployeeId(Long employeeId) {
        return userRepository.findByEmployeeId(employeeId);
    }

    /**
     * Build an {@link EditUserForm} pre-populated from the SystemUser and its Employee.
     * This is useful for the edit page: it returns an Optional.empty() when no user
     * exists for the provided employee id.
     */
    @Transactional(readOnly = true)
    public java.util.Optional<EditUserForm> loadEditFormForEmployee(Long employeeId) {
        return userRepository.findByEmployeeId(employeeId).map(u -> {
            EditUserForm f = new EditUserForm();
            f.setEmployeeId(u.getEmployee().getId());
            f.setEmployeeName(u.getEmployee().getName());
            f.setHourlyRate(u.getEmployee().getHourlyRate());
            f.setAddress(u.getEmployee().getAddress());
            f.setActive(u.isActive());
            return f;
        });
    }

    /**
     * Update both the Employee and SystemUser parts from an edit form. The password
     * field is optional: when blank or null the existing password hash is preserved.
     * Throws exceptions when the employee or system user cannot be found.
     */
    @Transactional
    public SystemUser updateEmployeeAndUser(EditUserForm form) {
        Employee employee = employeeRepository.findById(form.getEmployeeId()).orElseThrow(() -> new IllegalArgumentException("Employee not found: " + form.getEmployeeId()));
        employee.setName(form.getEmployeeName());
        employee.setHourlyRate(form.getHourlyRate());
        employee.setAddress(form.getAddress());
        employeeRepository.save(employee);

        SystemUser user = userRepository.findByEmployeeId(employee.getId()).orElseThrow(() -> new IllegalStateException("System user not found for employee: " + employee.getId()));
        user.setActive(form.isActive());
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        }
        return userRepository.save(user);
    }
}

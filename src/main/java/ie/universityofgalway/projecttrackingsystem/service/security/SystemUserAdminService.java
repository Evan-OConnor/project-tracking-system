package ie.universityofgalway.projecttrackingsystem.service.security;

import ie.universityofgalway.projecttrackingsystem.domain.core.Employee;
import ie.universityofgalway.projecttrackingsystem.domain.security.SystemRole;
import ie.universityofgalway.projecttrackingsystem.domain.security.SystemUser;
import ie.universityofgalway.projecttrackingsystem.dto.CreateUserForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.EmployeeRepository;
import ie.universityofgalway.projecttrackingsystem.repository.security.SystemRoleRepository;
import ie.universityofgalway.projecttrackingsystem.repository.security.SystemUserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Transactional
    public SystemUser createEmployeeAndSystemUser(CreateUserForm form) {

        // validate password
        if (form.getPassword() == null || form.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        // create new employee
        if (form.getEmployeeName() == null || form.getEmployeeName().isBlank()) {
            throw new IllegalArgumentException("Employee name is required");
        }

        if (form.getHourlyRate() == null) {
            throw new IllegalArgumentException("Hourly rate is required");
        }

        Employee newEmployee = new Employee(form.getEmployeeName(), form.getHourlyRate());
        newEmployee.setAddress(form.getAddress());
        Employee employee = employeeRepository.save(newEmployee);

        // create user for the employee - use role from form (fallback to STAFF)
        String roleToUse = form.getRoleName();
        SystemRole staffRole = roleRepository.findByName(roleToUse)
                .orElseThrow(() -> new IllegalStateException("Role not found: " + roleToUse));

        String hashPassword = passwordEncoder.encode(form.getPassword());

        SystemUser user = new SystemUser(employee, staffRole, hashPassword);
        user.setActive(true);

        try {
            user = userRepository.save(user);

            // always generate username using the assigned id
            user.setUsername(String.format("U%06d", user.getId()));
            user = userRepository.save(user);

            return user;
        } catch (DataIntegrityViolationException dive) {
            throw new IllegalStateException("Could not create user", dive);
        }
    }

    @Transactional
    public void deleteEmployeeAndAccount(Long employeeId) {
        // delete user first (if exists) then employee
        userRepository.findByEmployeeId(employeeId).ifPresent(u -> userRepository.deleteByEmployeeId(employeeId));
        employeeRepository.findById(employeeId).ifPresent(e -> employeeRepository.deleteById(employeeId));
    }

    @Transactional(readOnly = true)
    public List<SystemUser> listAllUsers() {
        return userRepository.findAll();
    }


}

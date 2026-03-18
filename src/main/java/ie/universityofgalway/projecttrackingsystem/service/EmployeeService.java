package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.domain.core.Employee;
import ie.universityofgalway.projecttrackingsystem.domain.security.SystemUser;
import ie.universityofgalway.projecttrackingsystem.dto.EmployeeView;
import ie.universityofgalway.projecttrackingsystem.repository.security.SystemUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final SystemUserRepository systemUserRepository;

    public EmployeeService(SystemUserRepository systemUserRepository) {
        this.systemUserRepository = systemUserRepository;
    }

    public List<EmployeeView> searchSummaries(String query) {
        return systemUserRepository
                .searchActiveUsers(query)
                .stream()
                .map(u -> new EmployeeView(
                        u.getEmployee().getId(),
                        u.getEmployee().getName()
                ))
                .toList();
    }

    public List<EmployeeView> listSummaries() {
        return systemUserRepository
                .findActiveUsers()
                .stream()
                .map(u -> new EmployeeView(
                        u.getEmployee().getId(),
                        u.getEmployee().getName()
                ))
                .toList();
    }

    public Employee getCurrentEmployee() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        SystemUser user = systemUserRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getEmployee();
    }
}
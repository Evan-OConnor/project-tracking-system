package ie.universityofgalway.projecttrackingsystem.service.security;

import ie.universityofgalway.projecttrackingsystem.domain.core.Employee;
import ie.universityofgalway.projecttrackingsystem.domain.security.SystemUser;
import ie.universityofgalway.projecttrackingsystem.repository.security.SystemUserRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for retrieving information about the currently authenticated
 * user from the Spring Security context and mapping it to the application's domain model.
 */
@Service
public class CurrentUserService {

    private final SystemUserRepository systemUserRepository;

    public CurrentUserService(SystemUserRepository systemUserRepository) {
        this.systemUserRepository = systemUserRepository;
    }

    /**
     * Retrieves the username of the currently authenticated user from the
     * Spring Security context.
     *
     * Throws IllegalStateException when no authenticated user is present.
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("No authenticated user found");
        }

        return authentication.getName();
    }

    /**
     * Returns the full SystemUser entity for the currently authenticated user.
     *
     * Throws IllegalStateException when no matching user exists in the database.
     */
    public SystemUser getCurrentUser() {
        String username = getCurrentUsername();

        return systemUserRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));
    }

    /**
     * Returns the Employee associated with the currently authenticated user.
     */
    public Employee getCurrentEmployee() {
        return getCurrentUser().getEmployee();
    }

    /**
     * Returns the current employee ID.
     */
    public Long getCurrentEmployeeId() {
        return getCurrentEmployee().getId();
    }
}
package ie.universityofgalway.projecttrackingsystem.service.security;

import ie.universityofgalway.projecttrackingsystem.domain.security.SystemUser;
import ie.universityofgalway.projecttrackingsystem.repository.security.SystemUserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Bridge between the application's {@link SystemUser} domain model and
 * Spring Security's {@link UserDetails} contract.
 *
 * Responsibilities:
 * - load a SystemUser by username from the repository,
 * - translate the domain role into a Spring Security authority ("ROLE_{NAME}"),
 * - build a Spring Security {@link UserDetails} instance with the stored password
 *   hash and account flags (disabled when the domain user is inactive).
 */
@Service
public class SystemUserDetailsService implements UserDetailsService {

    private final SystemUserRepository systemUserRepository;

    public SystemUserDetailsService(SystemUserRepository systemUserRepository) {
        this.systemUserRepository = systemUserRepository;
    }

    /**
     * Look up a domain {@link SystemUser} by username and convert it to a
     * Spring Security {@link UserDetails} instance.
     *
     * Behaviour:
     * - If no matching SystemUser is found a {@link UsernameNotFoundException}
     *   is thrown (required by the UserDetailsService contract).
     * - The user's role name is mapped to a single GrantedAuthority with the
     *   prefix "ROLE_" as expected by Spring Security's role handling.
     * - The returned UserDetails uses the stored password hash and sets the
     *   'disabled' flag when the domain user is not active.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // fetch domain user; throw if not found so Spring Security can handle it
        SystemUser user = systemUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // map domain role -> Spring Security authority (single-role model)
        String roleName = user.getRole().getName();
        String authority = "ROLE_" + roleName; // e.g. ROLE_ADMIN or ROLE_STAFF

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(authority));

        // Build a Spring Security UserDetails object using the stored password hash.
        // Note: disabled = !user.isActive() so inactive users cannot authenticate.
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .disabled(!user.isActive())
                .accountLocked(false)
                .accountExpired(false)
                .credentialsExpired(false)
                .build();
    }
}

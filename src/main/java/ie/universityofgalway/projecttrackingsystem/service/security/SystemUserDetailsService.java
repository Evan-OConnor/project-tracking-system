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

@Service
public class SystemUserDetailsService implements UserDetailsService {

    private final SystemUserRepository systemUserRepository;

    public SystemUserDetailsService(SystemUserRepository systemUserRepository) {
        this.systemUserRepository = systemUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemUser user = systemUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        String roleName = user.getRole().getName();
        String authority = "ROLE_" + roleName;

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(authority));

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

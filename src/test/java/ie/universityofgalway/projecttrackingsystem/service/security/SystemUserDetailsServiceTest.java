package ie.universityofgalway.projecttrackingsystem.service.security;

import ie.universityofgalway.projecttrackingsystem.domain.core.Employee;
import ie.universityofgalway.projecttrackingsystem.domain.security.SystemRole;
import ie.universityofgalway.projecttrackingsystem.domain.security.SystemUser;
import ie.universityofgalway.projecttrackingsystem.repository.security.SystemUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link SystemUserDetailsService}.
 *
 * Verifies that domain SystemUser entities are correctly translated into
 * Spring Security UserDetails, including:
 * - Role mapping (domain role → ROLE_ prefixed authority)
 * - Password hash usage (stored hash passed through, not re-encoded)
 * - Account status handling (active → enabled, inactive → disabled)
 * - Exception handling for missing users
 *
 * Ensures the bridge between domain security model and Spring Security behaves as expected.
 */
@ExtendWith(MockitoExtension.class)
class SystemUserDetailsServiceTest {

    @Mock
    private SystemUserRepository systemUserRepository;

    @InjectMocks
    private SystemUserDetailsService service;

    private Employee employee;
    private SystemRole adminRole;
    private SystemRole staffRole;

    // Set private id field via reflection

    private void setId(Object entity, Long value) throws Exception {
        Field field = entity.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(entity, value);
    }

    @BeforeEach
    void setUp() throws Exception {
        employee = new Employee("Test User", new BigDecimal("25.00"));
        setId(employee, 1L);

        adminRole = new SystemRole("ADMIN");
        setId(adminRole, 1L);

        staffRole = new SystemRole("STAFF");
        setId(staffRole, 2L);
    }

    // --- loadUserByUsername: happy paths ---

    @Test
    void loadUserByUsername_activeAdmin_returnsUserDetailsWithRoleAdmin() throws Exception {
        SystemUser adminUser = new SystemUser(employee, adminRole, "HASHED_PASSWORD");
        setId(adminUser, 1L);
        adminUser.setUsername("U000001");
        adminUser.setActive(true);

        when(systemUserRepository.findByUsername("U000001")).thenReturn(Optional.of(adminUser));

        UserDetails result = service.loadUserByUsername("U000001");

        assertNotNull(result);
        assertEquals("U000001", result.getUsername());
        assertEquals("HASHED_PASSWORD", result.getPassword());
        assertTrue(result.isEnabled());

        assertEquals(1, result.getAuthorities().size());
        GrantedAuthority authority = result.getAuthorities().iterator().next();
        assertEquals("ROLE_ADMIN", authority.getAuthority());

        verify(systemUserRepository).findByUsername("U000001");
    }

    @Test
    void loadUserByUsername_activeStaff_returnsUserDetailsWithRoleStaff() throws Exception {
        SystemUser staffUser = new SystemUser(employee, staffRole, "HASHED_PASSWORD");
        setId(staffUser, 2L);
        staffUser.setUsername("U000002");
        staffUser.setActive(true);

        when(systemUserRepository.findByUsername("U000002")).thenReturn(Optional.of(staffUser));

        UserDetails result = service.loadUserByUsername("U000002");

        assertNotNull(result);
        assertEquals("U000002", result.getUsername());
        assertEquals("HASHED_PASSWORD", result.getPassword());
        assertTrue(result.isEnabled());

        assertEquals(1, result.getAuthorities().size());
        GrantedAuthority authority = result.getAuthorities().iterator().next();
        assertEquals("ROLE_STAFF", authority.getAuthority());

        verify(systemUserRepository).findByUsername("U000002");
    }

    // --- loadUserByUsername: inactive user ---

    @Test
    void loadUserByUsername_inactiveUser_returnsDisabledUserDetails() throws Exception {
        SystemUser inactiveUser = new SystemUser(employee, staffRole, "HASH");
        setId(inactiveUser, 3L);
        inactiveUser.setUsername("U000003");
        inactiveUser.setActive(false);

        when(systemUserRepository.findByUsername("U000003")).thenReturn(Optional.of(inactiveUser));

        UserDetails result = service.loadUserByUsername("U000003");

        assertEquals("U000003", result.getUsername());
        assertFalse(result.isEnabled(), "Inactive user should be disabled");

        verify(systemUserRepository).findByUsername("U000003");
    }

    // --- loadUserByUsername: user not found ---

    @Test
    void loadUserByUsername_userNotFound_throwsUsernameNotFoundException() {
        when(systemUserRepository.findByUsername("NONEXISTENT")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("NONEXISTENT"));

        assertTrue(exception.getMessage().contains("User not found"));
        assertTrue(exception.getMessage().contains("NONEXISTENT"));
        verify(systemUserRepository).findByUsername("NONEXISTENT");
    }

    // --- loadUserByUsername: password hash handling ---

    @Test
    void loadUserByUsername_passwordHashIsUsedDirectly() throws Exception {
        SystemUser user = new SystemUser(employee, staffRole, "BCRYPT_HASH");
        setId(user, 4L);
        user.setUsername("U000004");
        user.setActive(true);

        when(systemUserRepository.findByUsername("U000004")).thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("U000004");

        assertNotNull(result);
        assertEquals("BCRYPT_HASH", result.getPassword());
        verify(systemUserRepository).findByUsername("U000004");
    }
}

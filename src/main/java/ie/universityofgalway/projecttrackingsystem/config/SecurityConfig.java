package ie.universityofgalway.projecttrackingsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity

/**
 * Central Spring Security configuration for the application.
 *
 * Responsibilities:
 * - define a {@link PasswordEncoder} bean to hash and verify passwords,
 * - define the {@link SecurityFilterChain} which manages request authorisation,
 *   login, logout and access-denied behaviour.
 *
 * Notes:
 * - Static resources and error pages are permitted to all users.
 * - URLs under /admin/** require the ADMIN role to access.
 */
public class SecurityConfig {

    // BCrypt work factor - determines hashing speed.
    private static final int BCRYPT_STRENGTH = 13;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // allow access to error pages and static assets without authentication
                        .requestMatchers(
                                "/error", "/error/**",
                                "/css/**", "/js/**", "/images/**"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults())
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/error/403")
                );
        return http.build();
    }
}

package ie.universityofgalway.projecttrackingsystem.config;

import ie.universityofgalway.projecttrackingsystem.dto.CreateUserForm;
import ie.universityofgalway.projecttrackingsystem.service.security.SystemUserAdminService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;

/**
 * Inserts 500 dummy users for testing pagination in the admin/users list.
 * Only active when the "dev" profile is enabled.
 */

@Configuration
@Profile("dev")
public class DevDataInitialiser {

    @Bean
    CommandLineRunner insertDummyUsers(SystemUserAdminService userAdminService) {
        return args -> {
            // Skip if dummy users already exist (check if more than just the seeded admin)
            if (userAdminService.listAllUsers().size() > 1) {
                return;
            }

            for (int i = 1; i <= 499; i++) {
                CreateUserForm form = new CreateUserForm();
                form.setEmployeeName("Test Employee " + i);
                form.setHourlyRate(BigDecimal.valueOf(15.00 + (i % 10)));
                form.setAddress("Test Address " + i);
                form.setPassword("password123");
                form.setConfirmPassword("password123");
                form.setRoleName(i % 10 == 0 ? "ADMIN" : "STAFF");

                try {
                    userAdminService.createEmployeeAndSystemUser(form);
                } catch (Exception e) {
                    System.err.printf("Could not create dummy user %d: %s%n", i, e.getMessage());
                }
            }
        };
    }
}

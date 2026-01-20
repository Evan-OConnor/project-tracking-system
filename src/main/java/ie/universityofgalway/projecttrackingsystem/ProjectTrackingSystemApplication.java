package ie.universityofgalway.projecttrackingsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/*
   Database auto-configuration temporarily disabled to allow application to start before database configured.
   Remove exclusion when adding MySQL database.
 */

@SpringBootApplication(
        exclude = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class
        }
)



public class ProjectTrackingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectTrackingSystemApplication.class, args);
    }

}

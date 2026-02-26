package ie.universityofgalway.projecttrackingsystem.repository.core;

import ie.universityofgalway.projecttrackingsystem.domain.core.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByNameContainingIgnoreCase(String name);
}
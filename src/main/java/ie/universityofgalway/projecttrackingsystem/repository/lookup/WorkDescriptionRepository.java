package ie.universityofgalway.projecttrackingsystem.repository.lookup;

import ie.universityofgalway.projecttrackingsystem.domain.lookup.WorkDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkDescriptionRepository extends JpaRepository<WorkDescription, Long> {
    Optional<WorkDescription> findByName(String name);
}

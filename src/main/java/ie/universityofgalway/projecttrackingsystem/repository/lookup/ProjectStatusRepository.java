package ie.universityofgalway.projecttrackingsystem.repository.lookup;

import ie.universityofgalway.projecttrackingsystem.domain.lookup.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectStatusRepository extends JpaRepository<ProjectStatus, Long> {
    Optional<ProjectStatus> findByName(String name);
}

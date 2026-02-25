package ie.universityofgalway.projecttrackingsystem.repository.core;

import ie.universityofgalway.projecttrackingsystem.domain.core.Project;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @EntityGraph(attributePaths = {"costItems"})
    Optional<Project> findWithCostItemsById(Long id);
}
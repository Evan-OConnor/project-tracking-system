package ie.universityofgalway.projecttrackingsystem.repository.lookup;

import ie.universityofgalway.projecttrackingsystem.domain.lookup.ProjectCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectCategoryRepository extends JpaRepository<ProjectCategory, Long> {
    Optional<ProjectCategory> findByName(String name);
}

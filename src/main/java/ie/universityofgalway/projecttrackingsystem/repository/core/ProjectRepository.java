package ie.universityofgalway.projecttrackingsystem.repository.core;

import ie.universityofgalway.projecttrackingsystem.domain.core.Project;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    List<Project> findByClientContactId(Long clientId);

    @EntityGraph(attributePaths = {"costItems"})
    Optional<Project> findWithCostItemsById(Long id);

    long countByStatus_NameIn(List<String> names);

    Optional<Project> findFirstByTitleContainingIgnoreCase(String title);

    List<Project> findByTitleContainingIgnoreCase(String q);

    // Used by ContactService
    boolean existsByClientContactId(Long contactId);
    boolean existsBySolicitorContactId(Long contactId);
    boolean existsByInsuranceCompanyContactId(Long contactId);

}
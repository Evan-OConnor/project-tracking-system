package ie.universityofgalway.projecttrackingsystem.repository.core;

import ie.universityofgalway.projecttrackingsystem.domain.core.CostItem;
import ie.universityofgalway.projecttrackingsystem.domain.core.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CostItemRepository extends JpaRepository<CostItem, Long> {

    // Find all cost items for a project with a specific type (OUTLAY or EXPENSE)
    List<CostItem> findByProjectAndType(Project project, CostItem.Type type);
}

package ie.universityofgalway.projecttrackingsystem.repository.core;

import ie.universityofgalway.projecttrackingsystem.domain.core.CostItem;
import ie.universityofgalway.projecttrackingsystem.domain.core.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CostItemRepository extends JpaRepository<CostItem, Long> {

    List<CostItem> findByProjectAndType(Project project, CostItem.Type type);

    List<CostItem> findByProjectAndInvoiceIsNull(Project project);

    boolean existsByProjectId(Long projectId);

    @Query("""
    SELECT COALESCE(SUM(c.costAmount),0)
    FROM CostItem c
    WHERE c.project.id = :projectId
    AND c.type = ie.universityofgalway.projecttrackingsystem.domain.core.CostItem.Type.OUTLAY
""")
    BigDecimal sumOutlaysByProjectId(Long projectId);

    @Query("""
    SELECT COALESCE(SUM(c.costAmount),0)
    FROM CostItem c
    WHERE c.project.id = :projectId
    AND c.type = ie.universityofgalway.projecttrackingsystem.domain.core.CostItem.Type.EXPENSE
""")
    BigDecimal sumExpensesByProjectId(Long projectId);

}
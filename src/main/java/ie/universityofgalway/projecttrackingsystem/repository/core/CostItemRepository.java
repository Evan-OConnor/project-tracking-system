package ie.universityofgalway.projecttrackingsystem.repository.core;

import ie.universityofgalway.projecttrackingsystem.domain.core.CostItem;
import ie.universityofgalway.projecttrackingsystem.domain.core.Project;
import ie.universityofgalway.projecttrackingsystem.dto.CostItemView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CostItemRepository extends JpaRepository<CostItem, Long> {

    List<CostItem> findByProjectAndType(Project project, CostItem.Type type);

    List<CostItem> findByProjectAndInvoiceIsNull(Project project);

    boolean existsByProjectId(Long projectId);

    @Query("""
SELECT COALESCE(SUM(c.costAmount),0)
FROM CostItem c
WHERE c.project.id = :projectId
AND c.type = :type
""")
    BigDecimal sumByProjectAndType(@Param("projectId") Long projectId,
                                   @Param("type") CostItem.Type type);

    @Query("""
    SELECT COALESCE(SUM(c.costAmount),0)
    FROM CostItem c
    WHERE c.project.id = :projectId
    AND c.type = ie.universityofgalway.projecttrackingsystem.domain.core.CostItem.Type.EXPENSE
""")
    BigDecimal sumExpensesByProjectId(@Param("projectId") Long projectId);

    @Query("""
    SELECT c FROM CostItem c
    JOIN FETCH c.project
    JOIN FETCH c.employee
    LEFT JOIN FETCH c.supplierContact
    LEFT JOIN FETCH c.invoice
""")
    List<CostItem> findAllWithDetails();

    @Query("""
    SELECT c FROM CostItem c
    JOIN FETCH c.project
    JOIN FETCH c.employee
    LEFT JOIN FETCH c.supplierContact
    LEFT JOIN FETCH c.invoice
    WHERE c.id = :id
""")

    Optional<CostItem> findByIdWithDetails(@Param("id") Long id);

    @Query("""
SELECT COALESCE(SUM(c.costAmount),0)
FROM CostItem c
WHERE c.project.id = :projectId
AND c.type = ie.universityofgalway.projecttrackingsystem.domain.core.CostItem.Type.OUTLAY
""")
    BigDecimal sumOutlaysByProjectId(@Param("projectId") Long projectId);

    @Query("""
SELECT new ie.universityofgalway.projecttrackingsystem.dto.CostItemView(
    c.id,
    p.title,
    e.name,
    s.name,
    c.costDate,
    c.description,
    c.costAmount,
    c.type,
    (c.invoice IS NOT NULL)
)
FROM CostItem c
JOIN c.project p
LEFT JOIN c.employee e
LEFT JOIN c.supplierContact s
WHERE p = :project
AND c.type = :type
""")
    List<CostItemView> findViewsByProjectAndType(Project project, CostItem.Type type);
}
package ie.universityofgalway.projecttrackingsystem.repository.core;

import ie.universityofgalway.projecttrackingsystem.domain.core.Invoice;
import ie.universityofgalway.projecttrackingsystem.domain.core.Project;
import ie.universityofgalway.projecttrackingsystem.domain.core.TimesheetEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimesheetEntryRepository extends JpaRepository<TimesheetEntry, Long> {

    List<TimesheetEntry> findByProject_Id(Long projectId);

    List<TimesheetEntry> findByProjectAndInvoiceIsNull(Project project);

    boolean existsByProjectId(Long projectId);

    List<TimesheetEntry> findByInvoice(Invoice invoice);

    @Query("""
    SELECT COALESCE(SUM(t.hours * e.hourlyRate),0)
    FROM TimesheetEntry t
    JOIN t.employee e
    WHERE t.project.id = :projectId
""")
    BigDecimal sumChargesByProjectId(Long projectId);

    List<TimesheetEntry> findByEmployeeIdAndEntryDateBetween(
            Long employeeId,
            LocalDate from,
            LocalDate to
    );
}
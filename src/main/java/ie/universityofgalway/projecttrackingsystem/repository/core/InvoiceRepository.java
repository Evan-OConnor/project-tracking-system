package ie.universityofgalway.projecttrackingsystem.repository.core;

import ie.universityofgalway.projecttrackingsystem.domain.core.Invoice;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    boolean existsByProjectId(Long projectId);

    @Query("SELECT i FROM Invoice i JOIN FETCH i.project WHERE i.id = :id")
    Optional<Invoice> findByIdWithProject(@Param("id") Long id);

    List<Invoice> findByInvoiceNumberContainingIgnoreCase(String query);

    List<Invoice> findByProjectClientContactIdAndStatusIn(
            Long clientId,
            List<InvoiceStatus> statuses
    );


    List<Invoice> findByStatusIn(List<InvoiceStatus> statuses);

    @Query("""
        SELECT i FROM Invoice i
        JOIN FETCH i.project p
        JOIN FETCH p.clientContact c
        WHERE i.status IN :statuses
    """)
    List<Invoice> findOutstandingWithClient(@Param("statuses") List<InvoiceStatus> statuses);
}

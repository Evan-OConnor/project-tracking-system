package ie.universityofgalway.projecttrackingsystem.repository.core;

import ie.universityofgalway.projecttrackingsystem.domain.core.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    boolean existsByInvoiceId(Long invoiceId);

    Receipt findTopByOrderByIdDesc();

    List<Receipt> findByInvoiceProjectId(Long projectId);

    @Query("""
        SELECT COALESCE(SUM(r.amountPaid), 0)
        FROM Receipt r
        WHERE r.invoice.id = :invoiceId
    """)
    BigDecimal sumPaymentsByInvoiceId(@Param("invoiceId") Long invoiceId);

    @Query("SELECT COALESCE(SUM(r.discount), 0) FROM Receipt r WHERE r.invoice.id = :invoiceId")
    BigDecimal sumDiscountsByInvoiceId(@Param("invoiceId") Long invoiceId);

    Receipt findTopByInvoice_Project_IdAndReceiptNumberStartingWithOrderByReceiptNumberDesc(
            Long projectId,
            String prefix
    );

}
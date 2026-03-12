package ie.universityofgalway.projecttrackingsystem.repository.core;

import ie.universityofgalway.projecttrackingsystem.domain.core.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    boolean existsByInvoiceId(Long invoiceId);

    Receipt findTopByOrderByIdDesc();

    List<Receipt> findByInvoiceProjectId(Long projectId);

}
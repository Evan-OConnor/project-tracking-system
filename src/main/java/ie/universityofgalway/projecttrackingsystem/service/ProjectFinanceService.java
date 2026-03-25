package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.domain.core.CostItem;
import ie.universityofgalway.projecttrackingsystem.domain.core.Invoice;
import ie.universityofgalway.projecttrackingsystem.domain.core.Project;
import ie.universityofgalway.projecttrackingsystem.domain.core.Receipt;
import ie.universityofgalway.projecttrackingsystem.repository.core.*;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ProjectFinanceService {

    private final CostItemRepository costItemRepository;
    private final TimesheetEntryRepository timesheetRepository;
    private final ReceiptRepository receiptRepository;
    private final InvoiceRepository invoiceRepository;

    public ProjectFinanceService(CostItemRepository costItemRepository,
                                 TimesheetEntryRepository timesheetRepository,
                                 ReceiptRepository receiptRepository,
                                 InvoiceRepository invoiceRepository) {
        this.costItemRepository = costItemRepository;
        this.timesheetRepository = timesheetRepository;
        this.receiptRepository = receiptRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public BigDecimal getOutlayTotal(Long projectId) {
        return Optional.ofNullable(
                costItemRepository.sumByProjectAndType(projectId, CostItem.Type.OUTLAY)
        ).orElse(BigDecimal.ZERO);
    }


    public BigDecimal getExpenseTotal(Long projectId) {
        return Optional.ofNullable(
                costItemRepository.sumByProjectAndType(projectId, CostItem.Type.EXPENSE)
        ).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getLabourTotal(Long projectId) {
            return Optional.ofNullable(
                    timesheetRepository.sumChargesByProjectId(projectId)
            ).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getReceiptsTotal(Long projectId) {
        return receiptRepository.findByInvoiceProjectId(projectId)
                .stream()
                .map(Receipt::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public BigDecimal getTotalInvoiced(Project project) {

        return project.getInvoices().stream()
                .map(inv -> {
                    BigDecimal exVat = inv.getTotalExVat();
                    BigDecimal vat = exVat.multiply(new BigDecimal("0.23"));
                    return exVat.add(vat);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getOutstandingInvoices(Project project) {

        BigDecimal totalInvoiced = getTotalInvoiced(project);
        BigDecimal totalReceived = getReceiptsTotal(project.getId());

        return totalInvoiced.subtract(totalReceived);
    }
}
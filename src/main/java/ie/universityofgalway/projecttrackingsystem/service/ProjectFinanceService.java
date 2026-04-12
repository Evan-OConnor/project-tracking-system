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

    private static final BigDecimal VAT_RATE = new BigDecimal("0.23");

    private final CostItemRepository costItemRepository;
    private final TimesheetEntryRepository timesheetRepository;
    private final ReceiptRepository receiptRepository;
    private final InvoiceRepository invoiceRepository;

    // Constructor
    public ProjectFinanceService(CostItemRepository costItemRepository,
                                 TimesheetEntryRepository timesheetRepository,
                                 ReceiptRepository receiptRepository,
                                 InvoiceRepository invoiceRepository) {
        this.costItemRepository = costItemRepository;
        this.timesheetRepository = timesheetRepository;
        this.receiptRepository = receiptRepository;
        this.invoiceRepository = invoiceRepository;
    }

    // Calculate Invoice Total
    private BigDecimal calculateInvoiceTotal(Invoice inv) {
        BigDecimal exVat = inv.getTotalExVat();
        BigDecimal vat = exVat.multiply(VAT_RATE);
        return exVat.add(vat);
    }

    // Outlay Total
    public BigDecimal getOutlayTotal(Long projectId) {
        return Optional.ofNullable(
                costItemRepository.sumByProjectAndType(projectId, CostItem.Type.OUTLAY)
        ).orElse(BigDecimal.ZERO);
    }

    // Expense Total
    public BigDecimal getExpenseTotal(Long projectId) {
        return Optional.ofNullable(
                costItemRepository.sumByProjectAndType(projectId, CostItem.Type.EXPENSE)
        ).orElse(BigDecimal.ZERO);
    }

    // Timesheet Total
    public BigDecimal getLabourTotal(Long projectId) {
        return Optional.ofNullable(
                timesheetRepository.sumChargesByProjectId(projectId)
        ).orElse(BigDecimal.ZERO);
    }

    // Receipt Total
    public BigDecimal getReceiptsTotal(Long projectId) {
        return receiptRepository.findByInvoiceProjectId(projectId)
                .stream()
                .map(r ->
                        Optional.ofNullable(r.getAmountPaid()).orElse(BigDecimal.ZERO)
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Receipts total including discount
    public BigDecimal getEffectiveReceiptsTotal(Long projectId) {
        return receiptRepository.findByInvoiceProjectId(projectId)
                .stream()
                .map(r -> {
                    BigDecimal amountPaid =
                            Optional.ofNullable(r.getAmountPaid()).orElse(BigDecimal.ZERO);

                    BigDecimal discount =
                            Optional.ofNullable(r.getDiscount()).orElse(BigDecimal.ZERO);

                    return amountPaid.add(discount);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Discount Total
    public BigDecimal getDiscountsTotal(Long projectId) {
        return receiptRepository.findByInvoiceProjectId(projectId)
                .stream()
                .map(r -> Optional.ofNullable(r.getDiscount()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    // Total Invoiced
    public BigDecimal getTotalInvoiced(Project project) {

        return project.getInvoices().stream()
                .map(this::calculateInvoiceTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Outstanding total
    public BigDecimal getOutstandingInvoices(Project project) {
        BigDecimal totalInvoiced = getTotalInvoiced(project);
        BigDecimal totalSettled = getEffectiveReceiptsTotal(project.getId());

        return totalInvoiced.subtract(totalSettled).max(BigDecimal.ZERO);
    }
}
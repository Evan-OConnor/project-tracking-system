package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.domain.core.CostItem;
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

    public ProjectFinanceService(CostItemRepository costItemRepository,
                                 TimesheetEntryRepository timesheetRepository,
                                 ReceiptRepository receiptRepository) {
        this.costItemRepository = costItemRepository;
        this.timesheetRepository = timesheetRepository;
        this.receiptRepository = receiptRepository;
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
}
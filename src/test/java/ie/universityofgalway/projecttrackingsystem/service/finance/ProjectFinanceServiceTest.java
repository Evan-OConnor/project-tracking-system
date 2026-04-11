package ie.universityofgalway.projecttrackingsystem.service.finance;

import ie.universityofgalway.projecttrackingsystem.domain.core.*;
import ie.universityofgalway.projecttrackingsystem.repository.core.*;

import ie.universityofgalway.projecttrackingsystem.service.ProjectFinanceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectFinanceServiceTest {

    @Mock private CostItemRepository costRepo;
    @Mock private TimesheetEntryRepository timesheetRepo;
    @Mock private ReceiptRepository receiptRepo;
    @Mock private InvoiceRepository invoiceRepo;

    @InjectMocks
    private ProjectFinanceService service;

    // Cost Totals

    @Test
    void getOutlayTotal_null_returnsZero() {
        when(costRepo.sumByProjectAndType(1L, CostItem.Type.OUTLAY))
                .thenReturn(null);

        BigDecimal result = service.getOutlayTotal(1L);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void getExpenseTotal_returnsValue() {
        when(costRepo.sumByProjectAndType(1L, CostItem.Type.EXPENSE))
                .thenReturn(new BigDecimal("100"));

        BigDecimal result = service.getExpenseTotal(1L);

        assertEquals(new BigDecimal("100"), result);
    }

    // Labour

    @Test
    void getLabourTotal_returnsValue() {
        when(timesheetRepo.sumChargesByProjectId(1L))
                .thenReturn(new BigDecimal("200"));

        BigDecimal result = service.getLabourTotal(1L);

        assertEquals(new BigDecimal("200"), result);
    }

    // Receipts

    @Test
    void getReceiptsTotal_sumsPayments() {

        Receipt r1 = mock(Receipt.class);
        Receipt r2 = mock(Receipt.class);

        when(r1.getAmountPaid()).thenReturn(new BigDecimal("50"));
        when(r2.getAmountPaid()).thenReturn(new BigDecimal("25"));

        when(receiptRepo.findByInvoiceProjectId(1L))
                .thenReturn(List.of(r1, r2));

        BigDecimal result = service.getReceiptsTotal(1L);

        assertEquals(new BigDecimal("75"), result);
    }

    @Test
    void getDiscountsTotal_sumsDiscounts() {

        Receipt r1 = mock(Receipt.class);
        Receipt r2 = mock(Receipt.class);

        when(r1.getDiscount()).thenReturn(new BigDecimal("10"));
        when(r2.getDiscount()).thenReturn(new BigDecimal("5"));

        when(receiptRepo.findByInvoiceProjectId(1L))
                .thenReturn(List.of(r1, r2));

        BigDecimal result = service.getDiscountsTotal(1L);

        assertEquals(new BigDecimal("15"), result);
    }

    // Outstanding Logic

    @Test
    void getOutstandingInvoices_calculatesCorrectly() {

        Project project = mock(Project.class);
        when(project.getId()).thenReturn(1L);

        Invoice inv = mock(Invoice.class);
        when(inv.getTotalExVat()).thenReturn(new BigDecimal("100"));

        when(project.getInvoices()).thenReturn(List.of(inv));

        when(receiptRepo.findByInvoiceProjectId(1L)).thenReturn(List.of());

        BigDecimal result = service.getOutstandingInvoices(project);

        assertNotNull(result);
    }
}
package ie.universityofgalway.projecttrackingsystem.service.dataintegrity;

import ie.universityofgalway.projecttrackingsystem.domain.core.Invoice;
import ie.universityofgalway.projecttrackingsystem.domain.core.Receipt;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.InvoiceStatus;
import ie.universityofgalway.projecttrackingsystem.dto.ReceiptForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.InvoiceRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.ReceiptRepository;

import ie.universityofgalway.projecttrackingsystem.service.InvoiceService;
import ie.universityofgalway.projecttrackingsystem.service.ReceiptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReceiptFinancialTest {

    @InjectMocks
    private ReceiptService receiptService;

    @Mock private ReceiptRepository receiptRepo;
    @Mock private InvoiceRepository invoiceRepo;
    @Mock private InvoiceService invoiceService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRejectOverpayment() {

        Invoice invoice = mock(Invoice.class);
        when(invoice.getStatus()).thenReturn(InvoiceStatus.GENERATED);

        ReceiptForm form = new ReceiptForm();
        form.setInvoiceId(1L);
        form.setAmountPaid(new BigDecimal("500"));
        form.setDiscount(BigDecimal.ZERO);

        when(invoiceRepo.findById(1L)).thenReturn(Optional.of(invoice));
        when(invoiceService.calculateInvoiceTotal(invoice)).thenReturn(new BigDecimal("300"));
        when(receiptRepo.sumPaymentsByInvoiceId(any())).thenReturn(BigDecimal.ZERO);
        when(receiptRepo.sumDiscountsByInvoiceId(any())).thenReturn(BigDecimal.ZERO);

        assertThrows(IllegalStateException.class, () -> receiptService.create(form));
    }

    @Test
    void shouldMarkInvoiceAsPaid() {

        Invoice invoice = mock(Invoice.class);
        when(invoice.getStatus()).thenReturn(InvoiceStatus.GENERATED);

        ReceiptForm form = new ReceiptForm();
        form.setInvoiceId(1L);
        form.setAmountPaid(new BigDecimal("100"));
        form.setDiscount(BigDecimal.ZERO);
        form.setDateReceived(LocalDate.now());

        when(invoiceRepo.findById(1L)).thenReturn(Optional.of(invoice));
        when(invoiceService.calculateInvoiceTotal(invoice)).thenReturn(new BigDecimal("100"));
        when(receiptRepo.sumPaymentsByInvoiceId(any()))
                .thenReturn(BigDecimal.ZERO)
                .thenReturn(new BigDecimal("100"));

        when(receiptRepo.sumDiscountsByInvoiceId(any()))
                .thenReturn(BigDecimal.ZERO);


        Receipt savedReceipt = mock(Receipt.class);
        when(savedReceipt.getId()).thenReturn(1L);
        when(savedReceipt.getDateReceived()).thenReturn(LocalDate.now());

        when(receiptRepo.save(any())).thenReturn(savedReceipt);

        receiptService.create(form);

        verify(invoice).setStatus(InvoiceStatus.PAID);
        verify(invoiceRepo).save(invoice);
    }
}
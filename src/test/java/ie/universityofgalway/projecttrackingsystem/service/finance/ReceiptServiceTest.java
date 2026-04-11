package ie.universityofgalway.projecttrackingsystem.service.finance;

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
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceTest {

    @Mock private ReceiptRepository receiptRepository;
    @Mock private InvoiceRepository invoiceRepository;
    @Mock private InvoiceService invoiceService;

    @InjectMocks
    private ReceiptService service;

    private Invoice invoice;

    @BeforeEach
    void setUp() {
        invoice = mock(Invoice.class);
    }

    // Create Validation Tests
    @Test
    void create_noInvoiceId_throwsException() {
        ReceiptForm form = new ReceiptForm();

        assertThrows(IllegalStateException.class,
                () -> service.create(form));
    }

    @Test
    void create_invoiceNotFound_throwsException() {
        ReceiptForm form = new ReceiptForm();
        form.setInvoiceId(1L);

        when(invoiceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> service.create(form));
    }

    @Test
    void create_invoiceAlreadyPaid_throwsException() {
        when(invoice.getStatus()).thenReturn(InvoiceStatus.PAID);

        ReceiptForm form = new ReceiptForm();
        form.setInvoiceId(1L);

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        assertThrows(IllegalStateException.class,
                () -> service.create(form));
    }

    // Payment Logic Test
    @Test
    void create_partialPayment_setsInvoiceToPartiallyPaid() {

        when(invoice.getId()).thenReturn(1L);
        when(invoice.getStatus()).thenReturn(InvoiceStatus.GENERATED);

        ReceiptForm form = new ReceiptForm();
        form.setInvoiceId(1L);
        form.setAmountPaid(new BigDecimal("30"));
        form.setDiscount(BigDecimal.ZERO);
        form.setDateReceived(LocalDate.now());

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(invoiceService.calculateInvoiceTotal(invoice)).thenReturn(new BigDecimal("100"));

        // before = 50, after = 80 → still not fully paid
        when(receiptRepository.sumPaymentsByInvoiceId(1L))
                .thenReturn(new BigDecimal("50"))
                .thenReturn(new BigDecimal("80"));

        when(receiptRepository.sumDiscountsByInvoiceId(1L))
                .thenReturn(BigDecimal.ZERO)
                .thenReturn(BigDecimal.ZERO);

        when(receiptRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.create(form);

        verify(invoice).setStatus(InvoiceStatus.PARTIALLY_PAID);
    }

    @Test
    void create_fullPayment_setsInvoiceToPaid() {

        when(invoice.getId()).thenReturn(1L);
        when(invoice.getStatus()).thenReturn(InvoiceStatus.GENERATED);

        ReceiptForm form = new ReceiptForm();
        form.setInvoiceId(1L);
        form.setAmountPaid(new BigDecimal("100"));
        form.setDiscount(BigDecimal.ZERO);
        form.setDateReceived(LocalDate.now());

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(invoiceService.calculateInvoiceTotal(invoice)).thenReturn(new BigDecimal("100"));

        // simulate before + after save
        when(receiptRepository.sumPaymentsByInvoiceId(1L))
                .thenReturn(BigDecimal.ZERO)
                .thenReturn(new BigDecimal("100"));

        when(receiptRepository.sumDiscountsByInvoiceId(1L))
                .thenReturn(BigDecimal.ZERO)
                .thenReturn(BigDecimal.ZERO);

        when(receiptRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.create(form);

        verify(invoice).setStatus(InvoiceStatus.PAID);
    }

    @Test
    void create_withDiscount_affectsEffectivePayment() {

        when(invoice.getId()).thenReturn(1L);
        when(invoice.getStatus()).thenReturn(InvoiceStatus.GENERATED);

        ReceiptForm form = new ReceiptForm();
        form.setInvoiceId(1L);
        form.setAmountPaid(new BigDecimal("80"));
        form.setDiscount(new BigDecimal("20"));
        form.setDateReceived(LocalDate.now());

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(invoiceService.calculateInvoiceTotal(invoice)).thenReturn(new BigDecimal("100"));

        when(receiptRepository.sumPaymentsByInvoiceId(1L))
                .thenReturn(BigDecimal.ZERO)
                .thenReturn(new BigDecimal("80"));

        when(receiptRepository.sumDiscountsByInvoiceId(1L))
                .thenReturn(BigDecimal.ZERO)
                .thenReturn(new BigDecimal("20"));

        when(receiptRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.create(form);

        verify(invoice).setStatus(InvoiceStatus.PAID);
    }

    // Overpayment
    @Test
    void create_overpayment_throwsException() {

        when(invoice.getId()).thenReturn(1L);
        when(invoice.getStatus()).thenReturn(InvoiceStatus.GENERATED);

        ReceiptForm form = new ReceiptForm();
        form.setInvoiceId(1L);
        form.setAmountPaid(new BigDecimal("200"));
        form.setDiscount(BigDecimal.ZERO);
        form.setDateReceived(LocalDate.now());

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(invoiceService.calculateInvoiceTotal(invoice)).thenReturn(new BigDecimal("100"));
        when(receiptRepository.sumPaymentsByInvoiceId(1L)).thenReturn(BigDecimal.ZERO);
        when(receiptRepository.sumDiscountsByInvoiceId(1L)).thenReturn(BigDecimal.ZERO);

        assertThrows(IllegalStateException.class,
                () -> service.create(form));
    }

    // Delete Logic
    @Test
    void delete_receipt_resetsInvoiceToGenerated() {

        Receipt receipt = mock(Receipt.class);
        when(receipt.getInvoice()).thenReturn(invoice);

        when(receiptRepository.findById(1L)).thenReturn(Optional.of(receipt));

        service.delete(1L);

        verify(invoice).setStatus(InvoiceStatus.GENERATED);
        verify(receiptRepository).delete(receipt);
        verify(invoiceRepository).save(invoice);
    }

    // Update Logic

    @Test
    void update_valid_updatesReceipt() {

        when(invoice.getId()).thenReturn(1L);

        Receipt receipt = mock(Receipt.class);
        when(receipt.getInvoice()).thenReturn(invoice);

        ReceiptForm form = new ReceiptForm();
        form.setAmountPaid(new BigDecimal("50"));
        form.setDiscount(BigDecimal.ZERO);
        form.setDateReceived(LocalDate.now());

        when(receiptRepository.findById(1L)).thenReturn(Optional.of(receipt));
        when(invoiceService.calculateInvoiceTotal(invoice)).thenReturn(new BigDecimal("100"));
        when(receiptRepository.sumPaymentsByInvoiceId(1L)).thenReturn(BigDecimal.ZERO);
        when(receiptRepository.sumDiscountsByInvoiceId(1L)).thenReturn(BigDecimal.ZERO);

        when(receiptRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.update(1L, form);

        verify(receipt).setAmountPaid(new BigDecimal("50"));
        verify(receiptRepository).save(receipt);
    }
}
package ie.universityofgalway.projecttrackingsystem.service.finance;

import ie.universityofgalway.projecttrackingsystem.domain.core.*;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.InvoiceStatus;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.VatRate;
import ie.universityofgalway.projecttrackingsystem.repository.core.*;
import ie.universityofgalway.projecttrackingsystem.repository.lookup.VatRateRepository;
import ie.universityofgalway.projecttrackingsystem.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for InvoiceService.
 * Uses Mockito to mock dependencies and isolate business logic.
 */
@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    // Mock repositories (dependencies of InvoiceService)
    @Mock private InvoiceRepository invoiceRepo;
    @Mock private TimesheetEntryRepository timesheetRepo;
    @Mock private CostItemRepository costItemRepo;
    @Mock private InvoiceLineItemRepository lineItemRepo;
    @Mock private ProjectRepository projectRepo;
    @Mock private VatRateRepository vatRateRepo;
    @Mock private ReceiptRepository receiptRepo;

    // Inject mocks into the service under test
    @InjectMocks
    private InvoiceService service;

    private Project project;
    private VatRate vatRate;

    /**
     * Runs before each test.
     * Creates reusable mock objects.
     */
    @BeforeEach
    void setUp() {
        project = mock(Project.class);
        vatRate = mock(VatRate.class);
    }

    // Helper method to stub a default VAT rate (23%).
    private void stubDefaultVat() {
        when(vatRate.getRateDecimal()).thenReturn(new BigDecimal("0.23"));
        when(vatRate.getRatePercent()).thenReturn(new BigDecimal("23.00"));
        when(vatRateRepo.findByRatePercent(any())).thenReturn(Optional.of(vatRate));
    }

    /**
     * Test: Generating an invoice with no billable items
     * should throw an IllegalStateException.
     */
    @Test
    void generateInvoice_noItems_throwsException() {
        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
        when(timesheetRepo.findByProjectAndInvoiceIsNull(project)).thenReturn(List.of());
        when(costItemRepo.findByProjectAndInvoiceIsNull(project)).thenReturn(List.of());

        assertThrows(IllegalStateException.class, () -> service.generateInvoice(1L));
    }

    // Test: If VAT is not configured, invoice generation should fail.
    @Test
    void generateInvoice_noVatConfigured_throwsException() {
        TimesheetEntry entry = mock(TimesheetEntry.class);

        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
        when(timesheetRepo.findByProjectAndInvoiceIsNull(project)).thenReturn(List.of(entry));
        when(costItemRepo.findByProjectAndInvoiceIsNull(project)).thenReturn(List.of());
        when(vatRateRepo.findByRatePercent(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.generateInvoice(1L));
    }

    /**
     * Test: Valid invoice generation
     * - Creates invoice
     * - Creates line items
     * - Links timesheet entries to the invoice
     */
    @Test
    void generateInvoice_withItems_createsInvoice_andLinksTimesheet() {
        stubDefaultVat();

        TimesheetEntry entry = mock(TimesheetEntry.class);
        Employee emp = mock(Employee.class);

        // Mock timesheet + employee data
        when(entry.getEmployee()).thenReturn(emp);
        when(entry.getHours()).thenReturn(new BigDecimal("2"));
        when(emp.getName()).thenReturn("John");
        when(emp.getHourlyRate()).thenReturn(new BigDecimal("50"));

        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
        when(timesheetRepo.findByProjectAndInvoiceIsNull(project)).thenReturn(List.of(entry));
        when(costItemRepo.findByProjectAndInvoiceIsNull(project)).thenReturn(List.of());

        // Simulate saving returning the same object
        when(invoiceRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> service.generateInvoice(1L));

        // Verify interactions
        verify(invoiceRepo, atLeast(2)).save(any());
        verify(lineItemRepo, atLeastOnce()).save(any());
        verify(entry).setInvoice(any());
    }

    //Test: Invoice total calculation including VAT.
    @Test
    void calculateInvoiceTotal_returnsCorrectAmount() {
        stubDefaultVat();

        Invoice invoice = mock(Invoice.class);
        when(invoice.getId()).thenReturn(1L);

        // Mock project and client details
        Project invoiceProject = mock(Project.class);
        Contact contact = mock(Contact.class);

        when(invoice.getProject()).thenReturn(invoiceProject);
        when(invoiceProject.getTitle()).thenReturn("Project A");
        when(invoiceProject.getClientContact()).thenReturn(contact);
        when(contact.getName()).thenReturn("Client");
        when(contact.getAddress()).thenReturn("Address");
        when(invoice.getStatus()).thenReturn(InvoiceStatus.GENERATED);

        // Mock line item
        InvoiceLineItem line = mock(InvoiceLineItem.class);
        when(line.getId()).thenReturn(10L);
        when(line.getDescription()).thenReturn("Professional Services");
        when(line.getQuantity()).thenReturn(new BigDecimal("2"));
        when(line.getUnitRate()).thenReturn(new BigDecimal("50"));

        when(lineItemRepo.findByInvoice(invoice)).thenReturn(List.of(line));
        when(receiptRepo.sumPaymentsByInvoiceId(1L)).thenReturn(BigDecimal.ZERO);
        when(receiptRepo.sumDiscountsByInvoiceId(1L)).thenReturn(BigDecimal.ZERO);

        BigDecimal result = service.calculateInvoiceTotal(invoice);

        // 2 * 50 = 100 + 23% VAT = 123
        assertEquals(new BigDecimal("123.00"), result);
    }

    //Test: Cannot delete a fully paid invoice.
    @Test
    void deleteInvoice_paidInvoice_throwsException() {
        Invoice invoice = mock(Invoice.class);
        when(invoice.getStatus()).thenReturn(InvoiceStatus.PAID);
        when(invoiceRepo.findByIdWithProject(1L)).thenReturn(Optional.of(invoice));

        assertThrows(IllegalStateException.class, () -> service.deleteInvoice(1L));
    }

    // Test: Cannot delete a partially paid invoice.
    @Test
    void deleteInvoice_partiallyPaid_throwsException() {
        Invoice invoice = mock(Invoice.class);
        when(invoice.getStatus()).thenReturn(InvoiceStatus.PARTIALLY_PAID);
        when(invoiceRepo.findByIdWithProject(1L)).thenReturn(Optional.of(invoice));

        assertThrows(IllegalStateException.class, () -> service.deleteInvoice(1L));
    }

    /**
     * Test: Valid invoice deletion
     * - Unlinks timesheets and cost items
     * - Deletes line items
     * - Deletes invoice
     */
    @Test
    void deleteInvoice_valid_removesInvoice_andUnlinksEntities() {
        Invoice invoice = mock(Invoice.class);
        TimesheetEntry entry = mock(TimesheetEntry.class);
        CostItem cost = mock(CostItem.class);

        when(invoice.getStatus()).thenReturn(InvoiceStatus.GENERATED);
        when(invoiceRepo.findByIdWithProject(1L)).thenReturn(Optional.of(invoice));
        when(lineItemRepo.findByInvoice(invoice)).thenReturn(List.of());
        when(timesheetRepo.findByInvoice(invoice)).thenReturn(List.of(entry));
        when(costItemRepo.findByInvoice(invoice)).thenReturn(List.of(cost));

        service.deleteInvoice(1L);

        verify(entry).setInvoice(null);
        verify(cost).setInvoice(null);
        verify(lineItemRepo).deleteAll(any());
        verify(invoiceRepo).delete(invoice);
    }

    // Test: Retrieving non-existent invoice should throw exception.
    @Test
    void getInvoiceById_notFound_throwsException() {
        when(invoiceRepo.findByIdWithProject(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.getInvoiceById(1L));
    }

    //Test: Retrieving all invoices when none exist.
    @Test
    void getAllInvoices_returnsEmptyList() {
        when(invoiceRepo.findAll()).thenReturn(List.of());

        List<?> result = service.getAllInvoices();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(invoiceRepo).findAll();
    }

    // Test: Search functionality delegates to repository.
    @Test
    void searchInvoices_callsRepository() {
        when(invoiceRepo.findByInvoiceNumberContainingIgnoreCase("INV")).thenReturn(List.of());

        service.searchInvoices("INV");

        verify(invoiceRepo).findByInvoiceNumberContainingIgnoreCase("INV");
    }
}
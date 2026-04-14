package ie.universityofgalway.projecttrackingsystem.service.dataintegrity;

import ie.universityofgalway.projecttrackingsystem.domain.core.*;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.VatRate;
import ie.universityofgalway.projecttrackingsystem.dto.InvoiceDTO;
import ie.universityofgalway.projecttrackingsystem.repository.core.*;
import ie.universityofgalway.projecttrackingsystem.repository.lookup.VatRateRepository;

import ie.universityofgalway.projecttrackingsystem.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoiceFinancialTest {

    @InjectMocks
    private InvoiceService invoiceService;

    @Mock
    private InvoiceRepository invoiceRepo;
    @Mock
    private TimesheetEntryRepository timesheetRepo;
    @Mock
    private CostItemRepository costItemRepo;
    @Mock
    private InvoiceLineItemRepository lineItemRepo;
    @Mock
    private ProjectRepository projectRepo;
    @Mock
    private VatRateRepository vatRateRepo;
    @Mock
    private ReceiptRepository receiptRepo;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldGenerateInvoiceFromUnbilledItems() {

        Project project = new Project();
        project.setTitle("Test Project");

        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));

        TimesheetEntry entry = mock(TimesheetEntry.class);
        Employee emp = mock(Employee.class);

        when(emp.getName()).thenReturn("John");
        when(emp.getHourlyRate()).thenReturn(new BigDecimal("100"));

        when(entry.getEmployee()).thenReturn(emp);
        when(entry.getHours()).thenReturn(new BigDecimal("2"));

        when(timesheetRepo.findByProjectAndInvoiceIsNull(project))
                .thenReturn(List.of(entry));

        when(costItemRepo.findByProjectAndInvoiceIsNull(project))
                .thenReturn(List.of());

        when(vatRateRepo.findByRatePercent(any()))
                .thenReturn(Optional.of(new VatRate(new BigDecimal("23.00"))));

        when(invoiceRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        when(lineItemRepo.findByInvoice(any()))
                .thenReturn(List.of(
                        new InvoiceLineItem(
                                null,
                                "Service",
                                new BigDecimal("2"),
                                new BigDecimal("100")
                        )
                ));

        InvoiceDTO dto = invoiceService.generateInvoice(1L);

        assertEquals(0, dto.getSubtotal().compareTo(new BigDecimal("200.00")));
    }

    @Test
    void shouldHandleNullPaymentsAndDiscounts() {

        Project project = new Project();
        project.setTitle("Test Project");

        Invoice invoice = new Invoice(project, LocalDate.now(), "INV-002");

        when(invoiceRepo.findByIdWithProject(1L))
                .thenReturn(Optional.of(invoice));

        when(lineItemRepo.findByInvoice(invoice)).thenReturn(List.of());

        when(vatRateRepo.findByRatePercent(any()))
                .thenReturn(Optional.of(new VatRate(new BigDecimal("23.00"))));

        when(receiptRepo.sumPaymentsByInvoiceId(any())).thenReturn(null);
        when(receiptRepo.sumDiscountsByInvoiceId(any())).thenReturn(null);

        InvoiceDTO dto = invoiceService.getInvoiceById(1L);

        assertEquals(0, dto.getTotalPaid().compareTo(BigDecimal.ZERO));
        assertEquals(0, dto.getTotalDiscount().compareTo(BigDecimal.ZERO));
    }
}
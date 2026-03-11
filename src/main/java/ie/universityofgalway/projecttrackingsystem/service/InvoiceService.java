package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.domain.core.*;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.VatRate;
import ie.universityofgalway.projecttrackingsystem.dto.InvoiceDTO;
import ie.universityofgalway.projecttrackingsystem.dto.InvoiceLineItemDTO;
import ie.universityofgalway.projecttrackingsystem.repository.core.*;
import ie.universityofgalway.projecttrackingsystem.repository.lookup.VatRateRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class InvoiceService {

    private static final BigDecimal DEFAULT_VAT_RATE = new BigDecimal("23.00");
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private final InvoiceRepository invoiceRepo;
    private final TimesheetEntryRepository timesheetRepo;
    private final CostItemRepository costItemRepo;
    private final InvoiceLineItemRepository lineItemRepo;
    private final ProjectRepository projectRepo;
    private final VatRateRepository vatRateRepo;

    public InvoiceService(InvoiceRepository invoiceRepo,
                          TimesheetEntryRepository timesheetRepo,
                          CostItemRepository costItemRepo,
                          InvoiceLineItemRepository lineItemRepo,
                          ProjectRepository projectRepo,
                          VatRateRepository vatRateRepo) {

        this.invoiceRepo = invoiceRepo;
        this.timesheetRepo = timesheetRepo;
        this.costItemRepo = costItemRepo;
        this.lineItemRepo = lineItemRepo;
        this.projectRepo = projectRepo;
        this.vatRateRepo = vatRateRepo;
    }

    // GENERATE INVOICE
    public InvoiceDTO generateInvoice(Long projectId) {

        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        List<TimesheetEntry> timesheets =
                timesheetRepo.findByProjectAndInvoiceIsNull(project);

        List<CostItem> costs =
                costItemRepo.findByProjectAndInvoiceIsNull(project);

        if (timesheets.isEmpty() && costs.isEmpty()) {
            throw new IllegalStateException(
                    "No unbilled items available for this project."
            );
        }

        VatRate standardVat = getDefaultVatRate();
        String invoiceNumber = generateInvoiceNumber();

        Invoice invoice = new Invoice(
                project,
                LocalDate.now(),
                invoiceNumber
        );

        invoiceRepo.save(invoice);

        // PROFESSIONAL FEES
        for (TimesheetEntry entry : timesheets) {

            InvoiceLineItem line = new InvoiceLineItem(
                    invoice,
                    standardVat,
                    "Professional Services - " + entry.getEmployee().getName(),
                    entry.getHours(),
                    entry.getEmployee().getHourlyRate()
            );

            lineItemRepo.save(line);
            entry.setInvoice(invoice);
        }

        // EXPENSES
        for (CostItem cost : costs) {

            InvoiceLineItem line = new InvoiceLineItem(
                    invoice,
                    standardVat,
                    cost.getDescription(),
                    BigDecimal.ONE,
                    cost.getCostAmount()
            );

            lineItemRepo.save(line);
            cost.setInvoice(invoice);
        }

        return mapToDTO(invoice);
    }

    // UPDATE DISCOUNT
    public void updateLineItemDiscount(Long lineItemId, BigDecimal discountPercent) {

        InvoiceLineItem lineItem = lineItemRepo.findById(lineItemId)
                .orElseThrow(() -> new RuntimeException("Line item not found"));

        if (discountPercent.compareTo(BigDecimal.ZERO) < 0 ||
                discountPercent.compareTo(ONE_HUNDRED) > 0) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }

        lineItem.setDiscountPercent(discountPercent);
    }

    // LIST ALL INVOICES
    public List<InvoiceDTO> getAllInvoices() {

        List<InvoiceDTO> result = new ArrayList<>();

        for (Invoice invoice : invoiceRepo.findAll()) {
            result.add(mapToDTO(invoice));
        }

        return result;
    }

    // GET SINGLE INVOICE
    public InvoiceDTO getInvoiceById(Long id) {

        Invoice invoice = invoiceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        return mapToDTO(invoice);
    }

    // SUPPORT METHODS

    public List<Project> getAllProjects() {
        return projectRepo.findAll();
    }

    public List<VatRate> getAllVatRates() {
        return vatRateRepo.findAll();
    }

    private String generateInvoiceNumber() {

        Invoice lastInvoice = invoiceRepo.findTopByOrderByIdDesc();

        long nextNumber = (lastInvoice == null) ? 1 : lastInvoice.getId() + 1;

        return String.format("INV-%05d", nextNumber);
    }

    private VatRate getDefaultVatRate() {

        return vatRateRepo.findByRatePercent(DEFAULT_VAT_RATE)
                .orElseThrow(() ->
                        new RuntimeException("Default VAT rate not configured in database"));
    }

    // ENTITY → DTO WITH DISCOUNT + VAT CALCULATION
    private InvoiceDTO mapToDTO(Invoice invoice) {

        List<InvoiceLineItem> lines =
                lineItemRepo.findByInvoice(invoice);

        List<InvoiceLineItemDTO> items = new ArrayList<>();

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal vatAmount = BigDecimal.ZERO;

        for (InvoiceLineItem line : lines) {

            BigDecimal net = line.getQuantity()
                    .multiply(line.getUnitRate())
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal discountPercent = line.getDiscountPercent() == null
                    ? BigDecimal.ZERO
                    : line.getDiscountPercent();

            BigDecimal discountAmount = net
                    .multiply(discountPercent)
                    .divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);

            BigDecimal discountedNet = net.subtract(discountAmount);

            BigDecimal vat = discountedNet
                    .multiply(line.getVatRate().getRateDecimal())
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal gross = discountedNet.add(vat)
                    .setScale(2, RoundingMode.HALF_UP);

            subtotal = subtotal.add(discountedNet);
            vatAmount = vatAmount.add(vat);

            items.add(new InvoiceLineItemDTO(
                    line.getId(),
                    line.getDescription(),
                    line.getQuantity(),
                    line.getUnitRate(),
                    net,
                    discountPercent,
                    discountAmount,
                    line.getVatRate().getRatePercent(),
                    vat,
                    gross
            ));
        }

        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
        vatAmount = vatAmount.setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = subtotal.add(vatAmount)
                .setScale(2, RoundingMode.HALF_UP);

        return new InvoiceDTO(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getProject().getTitle(),
                invoice.getInvoiceDate(),
                invoice.getStatus(),
                items,
                subtotal,
                vatAmount,
                total
        );
    }
}
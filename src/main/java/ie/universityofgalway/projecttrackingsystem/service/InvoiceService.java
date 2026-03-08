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
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InvoiceService {

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

        // Prevent empty invoice creation
        if (timesheets.isEmpty() && costs.isEmpty()) {
            throw new IllegalStateException(
                    "No unbilled items available for this project."
            );
        }

        VatRate standardVat = vatRateRepo
                .findByRatePercent(new BigDecimal("23.00"))
                .orElseThrow(() ->
                        new RuntimeException("23% VAT rate not configured in database"));

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

    // LIST ALL INVOICES
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepo.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
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
        long count = invoiceRepo.count() + 1;
        return String.format("INV-%05d", count);
    }

    // ENTITY → DTO (VAT PER LINE CALCULATION)
    private InvoiceDTO mapToDTO(Invoice invoice) {

        List<InvoiceLineItem> lines =
                lineItemRepo.findByInvoice(invoice);

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal vatAmount = BigDecimal.ZERO;

        for (InvoiceLineItem line : lines) {

            BigDecimal net = line.getQuantity()
                    .multiply(line.getUnitRate());

            BigDecimal vat = net
                    .multiply(line.getVatRate().getRateDecimal());

            subtotal = subtotal.add(net);
            vatAmount = vatAmount.add(vat);
        }

        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
        vatAmount = vatAmount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(vatAmount)
                .setScale(2, RoundingMode.HALF_UP);

        List<InvoiceLineItemDTO> items =
                lines.stream()
                        .map(line -> {

                            BigDecimal net = line.getQuantity()
                                    .multiply(line.getUnitRate())
                                    .setScale(2, RoundingMode.HALF_UP);

                            BigDecimal vat = net
                                    .multiply(line.getVatRate().getRateDecimal())
                                    .setScale(2, RoundingMode.HALF_UP);

                            BigDecimal gross = net.add(vat)
                                    .setScale(2, RoundingMode.HALF_UP);

                            return new InvoiceLineItemDTO(
                                    line.getDescription(),
                                    line.getQuantity(),
                                    line.getUnitRate(),
                                    net,
                                    line.getVatRate().getRatePercent(),
                                    vat,
                                    gross
                            );
                        })
                        .collect(Collectors.toList());

        return new InvoiceDTO(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getProject().getTitle(),
                invoice.getInvoiceDate(),
                items,
                subtotal,
                vatAmount,
                total
        );
    }
}
package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.domain.core.*;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.InvoiceStatus;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.VatRate;
import ie.universityofgalway.projecttrackingsystem.dto.InvoiceDTO;
import ie.universityofgalway.projecttrackingsystem.dto.InvoiceLineItemDTO;
import ie.universityofgalway.projecttrackingsystem.dto.InvoiceSearchDTO;
import ie.universityofgalway.projecttrackingsystem.repository.core.*;
import ie.universityofgalway.projecttrackingsystem.repository.lookup.VatRateRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class InvoiceService {

    private static final BigDecimal DEFAULT_VAT_RATE = new BigDecimal("23.00");
    private static final int INVOICE_LINE_ITEM_DESCRIPTION_MAX_LENGTH = 255;

    private final InvoiceRepository invoiceRepo;
    private final TimesheetEntryRepository timesheetRepo;
    private final CostItemRepository costItemRepo;
    private final InvoiceLineItemRepository lineItemRepo;
    private final ProjectRepository projectRepo;
    private final VatRateRepository vatRateRepo;
    private final ReceiptRepository receiptRepo;

    public InvoiceService(InvoiceRepository invoiceRepo,
                          TimesheetEntryRepository timesheetRepo,
                          CostItemRepository costItemRepo,
                          InvoiceLineItemRepository lineItemRepo,
                          ProjectRepository projectRepo,
                          VatRateRepository vatRateRepo,
                          ReceiptRepository receiptRepo) {

        this.invoiceRepo = invoiceRepo;
        this.timesheetRepo = timesheetRepo;
        this.costItemRepo = costItemRepo;
        this.lineItemRepo = lineItemRepo;
        this.projectRepo = projectRepo;
        this.vatRateRepo = vatRateRepo;
        this.receiptRepo = receiptRepo;
    }

    // Helper method to safely truncate description

    private String truncateDescription(String description) {
        if (description == null) {
            return "";
        }

        if (description.length() <= INVOICE_LINE_ITEM_DESCRIPTION_MAX_LENGTH) {
            return description;
        }

        // Truncate and append ellipsis while keeping total <= 255 chars
        int ellipsisLength = 3; // "..."
        int maxContentLength = INVOICE_LINE_ITEM_DESCRIPTION_MAX_LENGTH - ellipsisLength;
        return description.substring(0, maxContentLength) + "...";
    }

    // Generate Invoice

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

        String tempNumber = "TEMP-" + UUID.randomUUID();

        Invoice invoice = new Invoice(
                project,
                LocalDate.now(),
                tempNumber
        );

        invoice = invoiceRepo.save(invoice);

        String finalNumber = generateInvoiceNumber(
                invoice.getId(),
                invoice.getInvoiceDate()
        );

        invoice.setInvoiceNumber(finalNumber);

        invoice = invoiceRepo.save(invoice);

        // Professional Fees
        for (TimesheetEntry entry : timesheets) {

            InvoiceLineItem line = new InvoiceLineItem(
                    invoice,
                    "Professional Services - " + entry.getEmployee().getName(),
                    entry.getHours(),
                    entry.getEmployee().getHourlyRate()
            );

            lineItemRepo.save(line);
            entry.setInvoice(invoice);
        }

        // Expenses
        for (CostItem cost : costs) {

            InvoiceLineItem line = new InvoiceLineItem(
                    invoice,
                    truncateDescription(cost.getDescription()),
                    BigDecimal.ONE,
                    cost.getCostAmount()
            );

            lineItemRepo.save(line);
            cost.setInvoice(invoice);
        }

        return mapToDTO(invoice);
    }

    // List all invoices

    public List<InvoiceDTO> getAllInvoices() {

        List<InvoiceDTO> result = new ArrayList<>();

        for (Invoice invoice : invoiceRepo.findAll()) {
            result.add(mapToDTO(invoice));
        }

        return result;
    }

    // Get single invoice

    public InvoiceDTO getInvoiceById(Long id) {

        Invoice invoice = invoiceRepo.findByIdWithProject(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        return mapToDTO(invoice);
    }

    public BigDecimal calculateInvoiceTotal(Invoice invoice) {
        return mapToDTO(invoice).getGrossTotal();
    }

    // Delete Invoice
    public void deleteInvoice(Long invoiceId) {

        Invoice invoice = invoiceRepo.findByIdWithProject(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        // Cannot delete if paid or partially paid
        if (invoice.getStatus() == InvoiceStatus.PAID ||
                invoice.getStatus() == InvoiceStatus.PARTIALLY_PAID) {

            throw new IllegalStateException(
                    "Cannot delete a paid or partially paid invoice."
            );
        }

        // 1. Delete invoice line items (they depend on invoice)
        lineItemRepo.deleteAll(lineItemRepo.findByInvoice(invoice));

        // 2. Unlink timesheets - automatically becomes unbilled
        for (TimesheetEntry entry : timesheetRepo.findByInvoice(invoice)) {
            entry.setInvoice(null);
        }

        // 3. Unlink cost items - automatically becomes unbilled
        for (CostItem cost : costItemRepo.findByInvoice(invoice)) {
            cost.setInvoice(null);
        }

        // 4. Delete the invoice itself
        invoiceRepo.delete(invoice);
    }

    // Support Methods

    public List<Project> getAllProjects() {
        return projectRepo.findAll();
    }

    public List<VatRate> getAllVatRates() {
        return vatRateRepo.findAll();
    }

    public long getOutstandingInvoiceCount() {
        return invoiceRepo.findByStatusIn(
                List.of(InvoiceStatus.GENERATED, InvoiceStatus.PARTIALLY_PAID)
        ).size();
    }

    private String generateInvoiceNumber(Long invoiceId, LocalDate invoiceDate) {
        return "INV-" + invoiceDate.getYear() + "-" +
                String.format("%06d", invoiceId);
    }
    private VatRate getDefaultVatRate() {

        return vatRateRepo.findByRatePercent(DEFAULT_VAT_RATE)
                .orElseThrow(() ->
                        new RuntimeException("Default VAT rate not configured in database"));
    }

    public List<InvoiceDTO> searchInvoices(String query) {

        List<Invoice> invoices =
                invoiceRepo.findByInvoiceNumberContainingIgnoreCase(query);

        List<InvoiceDTO> results = new ArrayList<>();

        for (Invoice invoice : invoices) {
            results.add(mapToDTO(invoice));
        }

        return results;
    }

    private InvoiceStatus calculateStatus (
            BigDecimal total,
            BigDecimal effectivePaid    )
    {
        BigDecimal outstanding = total
                .subtract(effectivePaid)
                .max(BigDecimal.ZERO);

        if (outstanding.compareTo(BigDecimal.ZERO) == 0) {
            return InvoiceStatus.PAID;
        } else if (effectivePaid.compareTo(BigDecimal.ZERO) > 0)
        {
            return InvoiceStatus.PARTIALLY_PAID;
        } else {
            return InvoiceStatus.GENERATED;
        }
    }

    // ENTITY - DTO WITH VAT + PAYMENT CALCULATION

    private InvoiceDTO mapToDTO(Invoice invoice) {

        List<InvoiceLineItem> lines =
                lineItemRepo.findByInvoice(invoice);

        List<InvoiceLineItemDTO> items = new ArrayList<>();

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal vatAmount = BigDecimal.ZERO;

        VatRate vatRate = getDefaultVatRate();
        BigDecimal vatDecimal = vatRate.getRateDecimal();
        BigDecimal vatPercent = vatRate.getRatePercent();

        for (InvoiceLineItem line : lines) {

            BigDecimal net = line.getQuantity()
                    .multiply(line.getUnitRate())
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal vat = net
                    .multiply(vatDecimal)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal gross = net.add(vat)
                    .setScale(2, RoundingMode.HALF_UP);

            subtotal = subtotal.add(net);
            vatAmount = vatAmount.add(vat);

            items.add(new InvoiceLineItemDTO(
                    line.getId(),
                    line.getDescription(),
                    line.getQuantity(),
                    line.getUnitRate(),
                    net,
                    vatPercent,
                    vat,
                    gross
            ));
        }

        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
        vatAmount = vatAmount.setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = subtotal.add(vatAmount)
                .setScale(2, RoundingMode.HALF_UP);

        //  PAYMENT CALCULATIONS

        BigDecimal totalPaid =
                receiptRepo.sumPaymentsByInvoiceId(invoice.getId());

        BigDecimal totalDiscount =
                receiptRepo.sumDiscountsByInvoiceId(invoice.getId());

        if (totalPaid == null) totalPaid = BigDecimal.ZERO;
        if (totalDiscount == null) totalDiscount = BigDecimal.ZERO;

        BigDecimal effectivePaid = totalPaid.add(totalDiscount);

        BigDecimal outstanding =
                total.subtract(effectivePaid)
                        .max(BigDecimal.ZERO);

        //  DETERMINE STATUS
        InvoiceStatus status =
                invoice.getStatus() == InvoiceStatus.VOID
                        ? InvoiceStatus.VOID
                        : calculateStatus(total, effectivePaid);

        // Client details (null if project/contact are not present)
        String clientName = null;
        String clientAddress = null;
        if (invoice.getProject() != null && invoice.getProject().getClientContact() != null) {
            Contact contact = invoice.getProject().getClientContact();
            clientName = contact.getName();
            clientAddress = contact.getAddress();
        }

        BigDecimal vatRatePercent = vatRate != null ? vatRate.getRatePercent() : BigDecimal.ZERO;

        return new InvoiceDTO(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getProject().getTitle(),
                invoice.getInvoiceDate(),
                status,
                items,
                subtotal,
                vatAmount,
                total,
                totalPaid,
                outstanding,
                clientName,
                clientAddress,
                vatRatePercent,
                totalDiscount,
                effectivePaid
        );
    }

    public List<InvoiceSearchDTO> searchOutstandingInvoices(String query) {
        return invoiceRepo
                .searchOutstandingInvoices(
                        query,
                        List.of(InvoiceStatus.GENERATED, InvoiceStatus.PARTIALLY_PAID)
                )
                .stream()
                .map(i -> new InvoiceSearchDTO(
                        i.getId(),
                        i.getInvoiceNumber(),
                        i.getProject().getTitle(),
                        i.getTotalExVat()
                ))
                .toList();
    }

}
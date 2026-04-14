package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.domain.core.Invoice;
import ie.universityofgalway.projecttrackingsystem.domain.core.Receipt;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.InvoiceStatus;
import ie.universityofgalway.projecttrackingsystem.dto.ReceiptForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.InvoiceRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.ReceiptRepository;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class ReceiptService implements BaseService<Receipt, ReceiptForm> {

    private final ReceiptRepository receiptRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;

    // Constructor
    public ReceiptService(ReceiptRepository receiptRepository,
                          InvoiceRepository invoiceRepository,
                          InvoiceService invoiceService) {
        this.receiptRepository = receiptRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceService = invoiceService;
    }

    // List
    @Override
    public List<Receipt> list() {
        return receiptRepository.findAll(
                Sort.by(Sort.Direction.DESC, "dateReceived")
        );
    }

    // Get by id
    @Override
    public Receipt getById(Long id) {
        return receiptRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Receipt not found"));
    }

    // Form by Id
    @Override
    public ReceiptForm getFormById(Long id) {
        return mapToForm(getById(id));
    }

    // Create Receipt
    @Override
    public Receipt create(ReceiptForm form) {


        if (form.getInvoiceId() == null) {
            throw new IllegalStateException("Invoice must be selected");
        }

        Invoice invoice = invoiceRepository.findById(form.getInvoiceId())
                .orElseThrow(() -> new IllegalStateException("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("Invoice has already been paid");
        }

        validateAmounts(form, invoice);

        //  TEMP value (DB requires NOT NULL)
        String tempNumber = "TEMP-" + java.util.UUID.randomUUID();

        Receipt receipt = new Receipt(
                invoice,
                tempNumber,
                form.getDateReceived(),
                form.getDiscount(),
                form.getAmountPaid(),
                form.getPaymentMethod()
        );

        //  First save then generates ID
        receipt = receiptRepository.save(receipt);

        // Generate real receipt number
        String receiptNumber = generateReceiptNumber(
                receipt.getId(),
                receipt.getDateReceived()
        );

        receipt.setReceiptNumber(receiptNumber);

        receipt = receiptRepository.save(receipt);

        BigDecimal total = invoiceService.calculateInvoiceTotal(invoice);

        BigDecimal totalPaid = receiptRepository.sumPaymentsByInvoiceId(invoice.getId());
        BigDecimal totalDiscount = receiptRepository.sumDiscountsByInvoiceId(invoice.getId());

        if (totalPaid == null) totalPaid = BigDecimal.ZERO;
        if (totalDiscount == null) totalDiscount = BigDecimal.ZERO;

        BigDecimal effectivePaid = totalPaid.add(totalDiscount);

        if (effectivePaid.compareTo(total) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }

        invoiceRepository.save(invoice);

        return receipt;
    }

    // Update form
    @Override
    public Receipt update(Long id, ReceiptForm form) {

        Receipt receipt = getById(id);

        validateAmounts(form, receipt.getInvoice());

        updateEntity(receipt, form);

        return receiptRepository.save(receipt);
    }

    // Delete
    @Override
    public void delete(Long id) {

        Receipt receipt = getById(id);

        Invoice invoice = receipt.getInvoice();

        receiptRepository.delete(receipt);

        // if receipt removed, invoice becomes unpaid again
        invoice.setStatus(InvoiceStatus.GENERATED);
        invoiceRepository.save(invoice);
    }

    // Update entity
    @Override
    public void updateEntity(Receipt receipt, ReceiptForm form) {

        receipt.setDateReceived(form.getDateReceived());
        receipt.setDiscount(form.getDiscount());
        receipt.setAmountPaid(form.getAmountPaid());
        receipt.setPaymentMethod(form.getPaymentMethod());
    }

    // Map entity to form
    @Override
    public ReceiptForm mapToForm(Receipt receipt) {

        ReceiptForm form = new ReceiptForm();

        form.setId(receipt.getId());
        form.setInvoiceId(receipt.getInvoice().getId());
        form.setInvoiceNumber(receipt.getInvoice().getInvoiceNumber());
        form.setReceiptNumber(receipt.getReceiptNumber());
        form.setDateReceived(receipt.getDateReceived());
        form.setDiscount(receipt.getDiscount());
        form.setAmountPaid(receipt.getAmountPaid());
        form.setPaymentMethod(receipt.getPaymentMethod());

        return form;
    }

    // Generate Receipt No.
    private String generateReceiptNumber(Long receiptId, LocalDate receiptDate) {
        return "RC-" + receiptDate.getYear() + "-" +
                String.format("%06d", receiptId);
    }

    // Validate Invoice Amounts
    private void validateAmounts(ReceiptForm form, Invoice invoice) {

        BigDecimal total = invoiceService.calculateInvoiceTotal(invoice);

        BigDecimal paid = receiptRepository.sumPaymentsByInvoiceId(invoice.getId());
        BigDecimal discounts = receiptRepository.sumDiscountsByInvoiceId(invoice.getId());

        if (paid == null) paid = BigDecimal.ZERO;
        if (discounts == null) discounts = BigDecimal.ZERO;

        // subtract current receipt if editing
        if (form.getId() != null) {
            Receipt existing = receiptRepository.findById(form.getId()).orElse(null);
            if (existing != null) {
                paid = paid.subtract(existing.getAmountPaid());
                discounts = discounts.subtract(existing.getDiscount());
            }
        }

        BigDecimal outstanding = total.subtract(paid.add(discounts));

        BigDecimal expectedAmount = outstanding.subtract(form.getDiscount());

        if (form.getAmountPaid().compareTo(expectedAmount) > 0) {
            throw new IllegalStateException(
                    "Amount paid cannot exceed outstanding balance after discount"
            );
        }
    }
}
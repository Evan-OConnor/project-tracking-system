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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class ReceiptService implements BaseService<Receipt, ReceiptForm> {

    private final ReceiptRepository receiptRepository;
    private final InvoiceRepository invoiceRepository;

    public ReceiptService(ReceiptRepository receiptRepository,
                          InvoiceRepository invoiceRepository) {
        this.receiptRepository = receiptRepository;
        this.invoiceRepository = invoiceRepository;
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

    @Override
    public ReceiptForm getFormById(Long id) {
        return mapToForm(getById(id));
    }

    // Create
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

        validateAmounts(form,invoice);

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

        //  First save → generates ID
        receipt = receiptRepository.save(receipt);

        // Generate real receipt number
        String receiptNumber = generateReceiptNumber(
                receipt.getId(),
                receipt.getDateReceived()
        );

        receipt.setReceiptNumber(receiptNumber);

        receipt = receiptRepository.save(receipt);

        // mark invoice as paid
        BigDecimal subtotal = invoice.getTotalExVat();
        BigDecimal vatRate = new BigDecimal("0.23");
        BigDecimal vatAmount = subtotal.multiply(vatRate);
        BigDecimal total = subtotal.add(vatAmount);

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
        form.setReceiptNumber(receipt.getReceiptNumber());
        form.setDateReceived(receipt.getDateReceived());
        form.setDiscount(receipt.getDiscount());
        form.setAmountPaid(receipt.getAmountPaid());
        form.setPaymentMethod(receipt.getPaymentMethod());

        return form;
    }

    // Helpers
    private String generateReceiptNumber(Long receiptId, LocalDate receiptDate) {
        return "RC-" + receiptDate.getYear() + "-" +
                String.format("%06d", receiptId);
    }

    private void validateAmounts(ReceiptForm form, Invoice invoice) {

        if (form.getDiscount() == null) {
            form.setDiscount(BigDecimal.ZERO);
        }

        if (form.getDiscount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Discount cannot be negative");
        }

        if (form.getAmountPaid() == null || form.getAmountPaid().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Amount paid must be greater than zero");
        }

        BigDecimal subtotal = invoice.getTotalExVat();
        BigDecimal vatRate = new BigDecimal("0.23");
        BigDecimal vatAmount = subtotal.multiply(vatRate);
        BigDecimal total = subtotal.add(vatAmount);

        // Only payments affect outstanding
        BigDecimal paid = receiptRepository.sumPaymentsByInvoiceId(invoice.getId());
        if (paid == null) paid = BigDecimal.ZERO;

        BigDecimal outstanding = total.subtract(paid);

        //  Discount cannot exceed outstanding
        if (form.getDiscount().compareTo(outstanding) > 0) {
            throw new IllegalStateException("Discount cannot exceed outstanding balance");
        }

        // Rule
        BigDecimal expectedAmount = outstanding.subtract(form.getDiscount());

        if (form.getAmountPaid().compareTo(expectedAmount) > 0) {
            throw new IllegalStateException("Amount paid cannot exceed outstanding balance after discount");
        }
    }
}
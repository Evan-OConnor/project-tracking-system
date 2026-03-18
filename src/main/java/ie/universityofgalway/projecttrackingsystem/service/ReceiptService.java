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

        Long invoiceId = form.getInvoiceId();

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalStateException("Invoice not found"));

        // prevent duplicate payment
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("Invoice has already been paid");
        }

        validateAmounts(form);

        String receiptNumber = generateReceiptNumber(invoice.getProject().getId());

        Receipt receipt = new Receipt(
                invoice,
                receiptNumber,
                form.getDateReceived(),
                form.getDiscount(),
                form.getAmountPaid(),
                form.getPaymentMethod()
        );

        Receipt savedReceipt = receiptRepository.save(receipt);

        // mark invoice as paid
        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.save(invoice);

        return savedReceipt;
    }

    // Update form
    @Override
    public Receipt update(Long id, ReceiptForm form) {

        Receipt receipt = getById(id);

        validateAmounts(form);

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
    private String generateReceiptNumber(Long projectId) {

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));
        // e.g. 180326

        String prefix = "RT" + today + "-" + projectId;

        Receipt lastReceipt = receiptRepository
                .findTopByInvoice_Project_IdAndReceiptNumberStartingWithOrderByReceiptNumberDesc(
                        projectId, prefix
                );

        int nextSequence = 1;

        if (lastReceipt != null) {
            String lastNumber = lastReceipt.getReceiptNumber();

            String[] parts = lastNumber.split("-");
            int lastSequence = Integer.parseInt(parts[2]);
            nextSequence = lastSequence + 1;
        }

        return String.format("RT%s-%d-%02d", today, projectId, nextSequence);
    }
    private void validateAmounts(ReceiptForm form) {

        if (form.getDiscount() == null) {
            form.setDiscount(BigDecimal.ZERO);
        }

        if (form.getDiscount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Discount cannot be negative");
        }

        if (form.getAmountPaid() == null || form.getAmountPaid().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Amount paid must be greater than zero");
        }
    }
}
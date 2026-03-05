package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.domain.core.Invoice;
import ie.universityofgalway.projecttrackingsystem.domain.core.Receipt;
import ie.universityofgalway.projecttrackingsystem.dto.ReceiptForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.InvoiceRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.ReceiptRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public List<Receipt> list() {
        return receiptRepository.findAll();
    }

    @Override
    public Receipt getById(Long id) {
        return receiptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found"));
    }

    @Override
    public ReceiptForm getFormById(Long id) {

        Receipt receipt = getById(id);

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

    @Override
    public Receipt create(ReceiptForm form) {

        Long invoiceId = form.getInvoiceId();

        if (receiptRepository.existsByInvoiceId(invoiceId)) {
            throw new IllegalStateException("Receipt already exists for this invoice");
        }

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        String receiptNumber = String.format("RCPT-%05d", receiptRepository.count() + 1);

        Receipt receipt = new Receipt(
                invoice,
                receiptNumber,
                form.getDateReceived(),
                form.getDiscount(),
                form.getAmountPaid(),
                form.getPaymentMethod()
        );

        return receiptRepository.save(receipt);
    }

    @Override
    public Receipt update(Long id, ReceiptForm form) {

        Receipt receipt = getById(id);

        receipt.setDateReceived(form.getDateReceived());
        receipt.setDiscount(form.getDiscount());
        receipt.setAmountPaid(form.getAmountPaid());
        receipt.setPaymentMethod(form.getPaymentMethod());

        return receiptRepository.save(receipt);
    }

    @Override
    public void delete(Long id) {
        receiptRepository.deleteById(id);
    }

    // ===============================
    // REQUIRED BY BaseService
    // ===============================

    @Override
    public void updateEntity(Receipt receipt, ReceiptForm form) {
        receipt.setDateReceived(form.getDateReceived());
        receipt.setDiscount(form.getDiscount());
        receipt.setAmountPaid(form.getAmountPaid());
        receipt.setPaymentMethod(form.getPaymentMethod());
    }

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
}
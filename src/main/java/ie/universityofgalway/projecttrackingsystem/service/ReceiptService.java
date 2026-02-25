package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.domain.core.Invoice;
import ie.universityofgalway.projecttrackingsystem.domain.core.Receipt;
import ie.universityofgalway.projecttrackingsystem.dto.ReceiptForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.InvoiceRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.ReceiptRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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
        return receiptRepository.findById(id).orElseThrow();
    }

    @Override
    public ReceiptForm getFormById(Long id) {
        Receipt receipt = getById(id);
        ReceiptForm form = new ReceiptForm();
        form.setId(receipt.getId());
        form.setInvoiceId(receipt.getInvoice().getId());
        form.setDateReceived(receipt.getDateReceived());
        form.setAmountPaid(receipt.getAmountPaid());
        form.setDiscount(receipt.getDiscount());
        return form;
    }

    @Override
    public Receipt create(ReceiptForm form) {
        Receipt receipt = new Receipt();
        Invoice invoice = invoiceRepository.findById(form.getInvoiceId()).orElseThrow();
        receipt.setInvoice(invoice);
        receipt.setDateReceived(form.getDateReceived());
        receipt.setAmountPaid(form.getAmountPaid());
        receipt.setDiscount(form.getDiscount());

        // Save receipt
        Receipt savedReceipt = receiptRepository.save(receipt);

        // Link receipt to invoice
        invoice.setReceipt(savedReceipt);
        invoiceRepository.save(invoice);

        return savedReceipt;
    }

    @Override
    public Receipt update(Long id, ReceiptForm form) {
        Receipt receipt = getById(id);
        Invoice invoice = invoiceRepository.findById(form.getInvoiceId()).orElseThrow();

        receipt.setInvoice(invoice);
        receipt.setDateReceived(form.getDateReceived());
        receipt.setAmountPaid(form.getAmountPaid());
        receipt.setDiscount(form.getDiscount());

        Receipt savedReceipt = receiptRepository.save(receipt);

        // Ensure invoice points to updated receipt
        invoice.setReceipt(savedReceipt);
        invoiceRepository.save(invoice);

        return savedReceipt;
    }

    @Override
    public void delete(Long id) {
        Receipt receipt = getById(id);
        Invoice invoice = receipt.getInvoice();
        if (invoice != null) {
            invoice.setReceipt(null);
            invoiceRepository.save(invoice);
        }
        receiptRepository.deleteById(id);
    }
}
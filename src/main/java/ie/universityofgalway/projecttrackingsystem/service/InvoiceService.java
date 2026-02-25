package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.domain.core.Invoice;
import ie.universityofgalway.projecttrackingsystem.domain.core.Receipt;
import ie.universityofgalway.projecttrackingsystem.domain.core.InvoiceLineItem;
import ie.universityofgalway.projecttrackingsystem.dto.InvoiceForm;
import ie.universityofgalway.projecttrackingsystem.dto.InvoiceLineItemForm;
import ie.universityofgalway.projecttrackingsystem.dto.ReceiptForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.InvoiceRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.ProjectRepository;
import ie.universityofgalway.projecttrackingsystem.repository.lookup.VatRateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService implements BaseService<Invoice, InvoiceForm> {

    private final InvoiceRepository invoiceRepository;
    private final ProjectRepository projectRepository;
    private final VatRateRepository vatRateRepository;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          ProjectRepository projectRepository,
                          VatRateRepository vatRateRepository) {
        this.invoiceRepository = invoiceRepository;
        this.projectRepository = projectRepository;
        this.vatRateRepository = vatRateRepository;
    }

    @Override
    public List<Invoice> list() {
        return invoiceRepository.findAll();
    }

    @Override
    public Invoice getById(Long id) {
        return invoiceRepository.findById(id).orElseThrow();
    }

    @Override
    public InvoiceForm getFormById(Long id) {
        Invoice invoice = getById(id);
        InvoiceForm form = new InvoiceForm();
        form.setId(invoice.getId());
        form.setProjectId(invoice.getProject().getId());
        form.setVatRateId(invoice.getVatRate().getId());
        form.setInvoiceDate(invoice.getInvoiceDate());

        // Map line items to DTO
        for (InvoiceLineItem item : invoice.getItems()) {
            InvoiceLineItemForm lineItemForm = new InvoiceLineItemForm();
            lineItemForm.setDescription(item.getDescription());
            lineItemForm.setDetails(item.getDetails());
            lineItemForm.setQuantity(item.getQuantity());
            lineItemForm.setUnitRate(item.getUnitRate());
            lineItemForm.setTotal(item.getNetAmount()); // entity net amount
            form.getItems().add(lineItemForm);
        }

        // Populate totals from entity
        form.setSubtotal(invoice.getNetTotal());
        form.setTotalIncludingVat(invoice.getVatTotal());

        // Map receipt entity to ReceiptForm if exists
        if (invoice.getReceipt() != null) {
            ReceiptForm receiptForm = new ReceiptForm();
            receiptForm.setId(invoice.getReceipt().getId());
            receiptForm.setInvoiceId(invoice.getId());
            receiptForm.setDateReceived(invoice.getReceipt().getDateReceived());
            receiptForm.setAmountPaid(invoice.getReceipt().getAmountPaid());
            receiptForm.setDiscount(invoice.getReceipt().getDiscount());
            form.setReceipt(receiptForm);
        }

        if (invoice.getReceipt() != null) {
            Receipt receipt = invoice.getReceipt();
            ReceiptForm receiptForm = new ReceiptForm();
            receiptForm.setId(receipt.getId());
            receiptForm.setInvoiceId(invoice.getId());
            receiptForm.setDateReceived(receipt.getDateReceived());
            receiptForm.setDiscount(receipt.getDiscount());
            receiptForm.setAmountPaid(receipt.getAmountPaid());

            form.setReceipt(receiptForm);
        }

        return form;
    }


    @Override
    public Invoice create(InvoiceForm form) {
        Invoice invoice = new Invoice();
        invoice.setProject(projectRepository.findById(form.getProjectId()).orElseThrow());
        invoice.setVatRate(vatRateRepository.findById(form.getVatRateId()).orElseThrow());
        invoice.setInvoiceDate(form.getInvoiceDate());

        // Map line items
        List<InvoiceLineItem> items = form.getItems().stream()
                .map(itemForm -> {
                    InvoiceLineItem item = new InvoiceLineItem();
                    item.setDescription(itemForm.getDescription());
                    item.setDetails(itemForm.getDetails());
                    item.setQuantity(itemForm.getQuantity());
                    item.setUnitRate(itemForm.getUnitRate());
                    item.setInvoice(invoice); // link to parent
                    return item;
                })
                .collect(Collectors.toList());
        invoice.setItems(items);

        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice update(Long id, InvoiceForm form) {
        Invoice invoice = getById(id);
        invoice.setProject(projectRepository.findById(form.getProjectId()).orElseThrow());
        invoice.setVatRate(vatRateRepository.findById(form.getVatRateId()).orElseThrow());
        invoice.setInvoiceDate(form.getInvoiceDate());

        // Replace line items
        invoice.getItems().clear();
        List<InvoiceLineItem> items = form.getItems().stream()
                .map(itemForm -> {
                    InvoiceLineItem item = new InvoiceLineItem();
                    item.setDescription(itemForm.getDescription());
                    item.setDetails(itemForm.getDetails());
                    item.setQuantity(itemForm.getQuantity());
                    item.setUnitRate(itemForm.getUnitRate());
                    item.setInvoice(invoice);
                    return item;
                })
                .collect(Collectors.toList());
        invoice.getItems().addAll(items);

        return invoiceRepository.save(invoice);
    }

    @Override
    public void delete(Long id) {
        invoiceRepository.deleteById(id);
    }
}
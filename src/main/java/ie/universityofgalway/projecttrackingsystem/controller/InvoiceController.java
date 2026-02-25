package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.domain.core.Invoice;
import ie.universityofgalway.projecttrackingsystem.domain.core.Receipt;
import ie.universityofgalway.projecttrackingsystem.dto.InvoiceForm;
import ie.universityofgalway.projecttrackingsystem.dto.ReceiptForm;
import ie.universityofgalway.projecttrackingsystem.dto.InvoiceLineItemForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.ProjectRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.ReceiptRepository;
import ie.universityofgalway.projecttrackingsystem.repository.lookup.VatRateRepository;
import ie.universityofgalway.projecttrackingsystem.service.BaseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/invoices")
public class InvoiceController extends BaseController<Invoice, InvoiceForm> {

    private final ProjectRepository projectRepository;
    private final VatRateRepository vatRateRepository;
    private final ReceiptRepository receiptRepository;

    public InvoiceController(BaseService<Invoice, InvoiceForm> service,
                             ProjectRepository projectRepository,
                             VatRateRepository vatRateRepository,
                             ReceiptRepository receiptRepository) {
        super(service);
        this.projectRepository = projectRepository;
        this.vatRateRepository = vatRateRepository;
        this.receiptRepository = receiptRepository;
    }

    // Redirect /invoices -> /invoices/list
    @GetMapping
    public String redirectToList() {
        return "redirect:/invoices/list";
    }

    // LIST
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute(getListAttributeName(), service.list());
        return getListView();
    }

    // VIEW (read-only)
    @GetMapping("/{id}/view")
    public String viewInvoice(@PathVariable Long id, Model model) {
        InvoiceForm invoiceForm = service.getFormById(id); // <-- returns InvoiceForm

        // Attach receipt if exists
        Invoice invoice = service.getById(id);
        Receipt receipt = invoice.getReceipt();
        if (receipt != null) {
            ReceiptForm receiptForm = new ReceiptForm();
            receiptForm.setId(receipt.getId());
            receiptForm.setInvoiceId(invoice.getId());
            receiptForm.setDateReceived(receipt.getDateReceived());
            receiptForm.setDiscount(receipt.getDiscount());
            receiptForm.setAmountPaid(receipt.getAmountPaid());
            invoiceForm.setReceipt(receiptForm);
        }

        model.addAttribute(getEntityAttributeName(), invoiceForm); // Pass InvoiceForm
        return getDetailsView(); // "invoices/form" or your view template
    }

    @GetMapping("/{id}/print")
    public String printInvoice(@PathVariable Long id, Model model) {
        InvoiceForm invoiceForm = service.getFormById(id); // <-- returns InvoiceForm

        // Attach receipt if exists
        Invoice invoice = service.getById(id);
        Receipt receipt = invoice.getReceipt();
        if (receipt != null) {
            ReceiptForm receiptForm = new ReceiptForm();
            receiptForm.setId(receipt.getId());
            receiptForm.setInvoiceId(invoice.getId());
            receiptForm.setDateReceived(receipt.getDateReceived());
            receiptForm.setDiscount(receipt.getDiscount());
            receiptForm.setAmountPaid(receipt.getAmountPaid());
            invoiceForm.setReceipt(receiptForm);
        }

        model.addAttribute(getEntityAttributeName(), invoiceForm); // Pass InvoiceForm
        return "invoices/print"; // Separate print template
    }
    // NEW FORM
    @GetMapping("/new")
    public String createForm(Model model) {
        InvoiceForm invoiceForm = new InvoiceForm();
        invoiceForm.getItems().add(new InvoiceLineItemForm()); // <-- top-level class
        model.addAttribute(getEntityAttributeName(), invoiceForm);
        return getDetailsView();
    }

    // EDIT FORM
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        InvoiceForm invoiceForm = service.getFormById(id);

        if (invoiceForm.getItems().isEmpty()) {
            invoiceForm.getItems().add(new InvoiceLineItemForm()); // <-- top-level class
        }
// Attach receipt if it exists
        Invoice invoice = service.getById(id);
        Receipt receipt = invoice.getReceipt(); // may be null

        if (receipt != null) {
            ReceiptForm receiptForm = new ReceiptForm();
            receiptForm.setId(receipt.getId());
            receiptForm.setInvoiceId(invoice.getId());
            receiptForm.setDateReceived(receipt.getDateReceived());
            receiptForm.setDiscount(receipt.getDiscount());
            receiptForm.setAmountPaid(receipt.getAmountPaid());

            invoiceForm.setReceipt(receiptForm);
        }

        model.addAttribute(getEntityAttributeName(), invoiceForm);
        return getDetailsView();
    }
    // SAVE (CREATE OR UPDATE)
    @PostMapping
    public String save(@ModelAttribute InvoiceForm invoiceForm) {
        if (invoiceForm.getId() == null) {
            service.create(invoiceForm);
        } else {
            service.update(invoiceForm.getId(), invoiceForm);
        }
        return "redirect:/invoices/list";
    }

    // DELETE
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/invoices/list";
    }

    // Model attributes for dropdowns
    @ModelAttribute("projects")
    public Object getProjects() {
        return projectRepository.findAll();
    }

    @ModelAttribute("vatRates")
    public Object getVatRates() {
        return vatRateRepository.findAll();
    }

    // BaseController overrides
    @Override
    protected String getListView() {
        return "invoices/list";
    }

    @Override
    protected String getDetailsView() {
        return "invoices/form";
    }

    @Override
    protected String getBaseUrl() {
        return "/invoices";
    }

    @Override
    protected String getListAttributeName() {
        return "invoices";
    }

    @Override
    protected String getEntityAttributeName() {
        return "invoice";
    }
}
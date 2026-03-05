package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.domain.core.Receipt;
import ie.universityofgalway.projecttrackingsystem.dto.ReceiptForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.InvoiceRepository;
import ie.universityofgalway.projecttrackingsystem.service.BaseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.math.BigDecimal;

@Controller
@RequestMapping("/receipts")
public class ReceiptController extends BaseController<Receipt, ReceiptForm> {

    private final InvoiceRepository invoiceRepository;

    public ReceiptController(BaseService<Receipt, ReceiptForm> service,
                             InvoiceRepository invoiceRepository) {
        super(service);
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    protected String getListView() {
        return "receipts/list";
    }

    @Override
    protected String getDetailsView() {
        return "receipts/form";
    }

    @Override
    protected String getBaseUrl() {
        return "/receipts";
    }

    @Override
    protected String getListAttributeName() {
        return "receipts";
    }

    @Override
    protected String getEntityAttributeName() {
        return "receipt";
    }

    // =====================================================
    // INVOICES DROPDOWN
    // =====================================================

    @ModelAttribute("invoices")
    public Object getInvoices() {
        return invoiceRepository.findAll();
    }

    // =====================================================
    // CREATE FORM
    // =====================================================

    @GetMapping("/new")
    public String newReceipt(Model model) {

        ReceiptForm form = new ReceiptForm();
        form.setDateReceived(LocalDate.now());
        form.setDiscount(BigDecimal.ZERO);
        form.setAmountPaid(BigDecimal.ZERO);

        model.addAttribute(getEntityAttributeName(), form);

        return getDetailsView();
    }

    // =====================================================
    // CREATE
    // =====================================================

    @PostMapping
    public String createReceipt(@ModelAttribute("receipt") ReceiptForm form) {

        service.create(form);

        return "redirect:" + getBaseUrl();
    }

    // =====================================================
    // EDIT FORM
    // =====================================================

    @GetMapping("/{id}/edit")
    public String editReceipt(@PathVariable Long id, Model model) {

        ReceiptForm form = service.getFormById(id);

        model.addAttribute(getEntityAttributeName(), form);

        return getDetailsView();
    }

    // =====================================================
    // UPDATE
    // =====================================================

    @PostMapping("/{id}/edit")
    public String updateReceipt(@PathVariable Long id,
                                @ModelAttribute("receipt") ReceiptForm form) {

        service.update(id, form);

        return "redirect:" + getBaseUrl();
    }

    // =====================================================
    // QUICK CREATE FROM INVOICE
    // =====================================================

    @PostMapping("/createFromInvoice")
    public String createReceiptFromInvoice(@RequestParam("invoiceId") Long invoiceId) {

        ReceiptForm form = new ReceiptForm();

        form.setInvoiceId(invoiceId);
        form.setDateReceived(LocalDate.now());
        form.setDiscount(BigDecimal.ZERO);
        form.setAmountPaid(BigDecimal.ZERO);
        form.setPaymentMethod("Bank Transfer");

        service.create(form);

        return "redirect:" + getBaseUrl();
    }

    // =====================================================
    // PRINT RECEIPT
    // =====================================================

    @GetMapping("/{id}/print")
    public String printReceipt(@PathVariable Long id, Model model) {

        Receipt receipt = service.getById(id);

        model.addAttribute("receipt", receipt);

        return "receipts/print";
    }
}
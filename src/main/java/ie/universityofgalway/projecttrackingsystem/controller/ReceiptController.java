package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.domain.core.Receipt;
import ie.universityofgalway.projecttrackingsystem.dto.ReceiptForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.InvoiceRepository;
import ie.universityofgalway.projecttrackingsystem.service.BaseService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/receipts")
public class ReceiptController extends BaseController<Receipt, ReceiptForm> {

    private final InvoiceRepository invoiceRepository;


    // Constructor
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

    // Model Data

    @ModelAttribute("invoices")
    public Object getInvoices() {
        return invoiceRepository.findAll();
    }

    // View Redirect
    @Override
    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        return "redirect:/receipts/" + id + "/print";
    }

    // New Receipt
    @GetMapping("/new")
    public String newReceipt(Model model) {

        ReceiptForm form = new ReceiptForm();
        form.setDateReceived(LocalDate.now());
        form.setDiscount(BigDecimal.ZERO);
        form.setAmountPaid(BigDecimal.ZERO);
        form.setPaymentMethod("Bank Transfer");

        model.addAttribute(getEntityAttributeName(), form);

        return getDetailsView();
    }

    // Create Receipt
    @PostMapping
    public String createReceipt(
            @Valid @ModelAttribute("receipt") ReceiptForm form,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            return getDetailsView();
        }

        try {

            service.create(form);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Receipt created successfully."
            );

            return "redirect:" + getBaseUrl();

        } catch (IllegalStateException ex) {

            model.addAttribute("errorMessage", ex.getMessage());
            return getDetailsView();
        }
    }

    // Edit Form
    @GetMapping("/{id}/edit")
    public String editReceipt(@PathVariable Long id, Model model) {

        ReceiptForm form = service.getFormById(id);

        model.addAttribute(getEntityAttributeName(), form);

        return getDetailsView();
    }

    // Update Form
    @PostMapping("/{id}/edit")
    public String updateReceipt(
            @PathVariable Long id,
            @Valid @ModelAttribute("receipt") ReceiptForm form,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            return getDetailsView();
        }

        try {

            service.update(id, form);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Receipt updated successfully."
            );

            return "redirect:" + getBaseUrl();

        } catch (IllegalStateException ex) {

            model.addAttribute("errorMessage", ex.getMessage());
            return getDetailsView();
        }
    }

    // Quick Create Receipt from Invoice
    @PostMapping("/createFromInvoice")
    public String createReceiptFromInvoice(
            @RequestParam("invoiceId") Long invoiceId,
            RedirectAttributes redirectAttributes) {

        try {

            ReceiptForm form = new ReceiptForm();
            form.setInvoiceId(invoiceId);
            form.setDateReceived(LocalDate.now());
            form.setDiscount(BigDecimal.ZERO);
            form.setAmountPaid(BigDecimal.ZERO);
            form.setPaymentMethod("Bank Transfer");

            service.create(form);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Receipt created from invoice."
            );

        } catch (IllegalStateException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage()
            );
        }

        return "redirect:" + getBaseUrl();
    }

    // Print Receipt
    @GetMapping("/{id}/print")
    public String printReceipt(@PathVariable Long id, Model model) {

        Receipt receipt = service.getById(id);

        model.addAttribute("receipt", receipt);

        return "receipts/print";
    }

}
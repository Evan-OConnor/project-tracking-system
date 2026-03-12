package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.dto.InvoiceDTO;
import ie.universityofgalway.projecttrackingsystem.service.InvoiceService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    // ------------------------------------------------
    // LIST INVOICES
    // ------------------------------------------------

    @GetMapping
    public String listInvoices(Model model) {
        model.addAttribute("invoices", invoiceService.getAllInvoices());
        return "invoice/list";
    }

    // ------------------------------------------------
    // VIEW INVOICE
    // ------------------------------------------------

    @GetMapping("/{id}")
    public String viewInvoice(@PathVariable Long id, Model model) {

        model.addAttribute("invoice",
                invoiceService.getInvoiceById(id));

        return "invoice/view";
    }

    // ------------------------------------------------
    // SHOW GENERATE PAGE
    // ------------------------------------------------

    @GetMapping("/generate")
    public String showGeneratePage(Model model) {

        model.addAttribute("projects",
                invoiceService.getAllProjects());

        model.addAttribute("vatRates",
                invoiceService.getAllVatRates());

        return "invoice/generate";
    }

    // ------------------------------------------------
    // GENERATE INVOICE
    // ------------------------------------------------

    @PostMapping("/generate")
    public String generateInvoice(@RequestParam Long projectId,
                                  RedirectAttributes redirectAttributes) {

        try {

            InvoiceDTO invoice =
                    invoiceService.generateInvoice(projectId);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Invoice generated successfully."
            );

            return "redirect:/invoices/" + invoice.getInvoiceId();

        } catch (IllegalStateException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage()
            );

            return "redirect:/invoices/generate";
        }
    }

    // ------------------------------------------------
    // API: GET TOTAL AMOUNT (existing endpoint)
    // ------------------------------------------------

    @GetMapping("/api/{id}/total")
    @ResponseBody
    public BigDecimal getInvoiceTotal(@PathVariable Long id) {

        InvoiceDTO invoice =
                invoiceService.getInvoiceById(id);

        return invoice.getGrossTotal();
    }

    // ------------------------------------------------
    // API: GET PAYMENT INFO (for receipt form autofill)
    // ------------------------------------------------

    @GetMapping("/api/{id}/payment-info")
    @ResponseBody
    public Map<String, Object> getInvoicePaymentInfo(@PathVariable Long id) {

        InvoiceDTO invoice =
                invoiceService.getInvoiceById(id);

        Map<String, Object> data = new HashMap<>();

        data.put("total", invoice.getGrossTotal());

        // if your invoice supports a discount field later
        data.put("discount", BigDecimal.ZERO);

        return data;
    }
}
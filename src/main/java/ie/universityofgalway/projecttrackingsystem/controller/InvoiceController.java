package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.dto.InvoiceDTO;
import ie.universityofgalway.projecttrackingsystem.service.InvoiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    // LIST INVOICES
    @GetMapping
    public String listInvoices(Model model) {
        model.addAttribute("invoices", invoiceService.getAllInvoices());
        return "invoice/list";
    }

    // VIEW INVOICE
    @GetMapping("/{id}")
    public String viewInvoice(@PathVariable Long id, Model model) {
        model.addAttribute("invoice", invoiceService.getInvoiceById(id));
        return "invoice/view";
    }

    // SHOW GENERATE PAGE
    @GetMapping("/generate")
    public String showGeneratePage(Model model) {
        model.addAttribute("projects", invoiceService.getAllProjects());
        model.addAttribute("vatRates", invoiceService.getAllVatRates());
        return "invoice/generate";
    }


    // GENERATE INVOICE
    @PostMapping("/generate")
    public String generateInvoice(@RequestParam Long projectId,
                                  RedirectAttributes redirectAttributes) {

        try {
            invoiceService.generateInvoice(projectId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Invoice generated successfully.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    ex.getMessage());
        }

        return "redirect:/invoices/generate";
    }

    @GetMapping("/api/{id}/total")
    @ResponseBody
    public BigDecimal getInvoiceTotal(@PathVariable Long id) {

        InvoiceDTO invoice = invoiceService.getInvoiceById(id);

        return invoice.getGrossTotal();
    }
}
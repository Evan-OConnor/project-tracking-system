package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.dto.InvoiceDTO;
import ie.universityofgalway.projecttrackingsystem.service.InvoiceService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;


    // Constructor
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    // List Invoices
    @GetMapping
    public String listInvoices(Model model) {

        model.addAttribute("invoices", invoiceService.getAllInvoices());

        return "invoice/list";
    }

    // View Invoice
    @GetMapping("/{id}")
    public String viewInvoice(@PathVariable Long id, Model model) {

        model.addAttribute("invoice",
                invoiceService.getInvoiceById(id));

        return "invoice/view";
    }

    // Show Generate Page
    @GetMapping("/generate")
    public String showGeneratePage(Model model) {

        model.addAttribute("projects",
                invoiceService.getAllProjects());

        model.addAttribute("vatRates",
                invoiceService.getAllVatRates());

        return "invoice/generate";
    }

    // Generate Invoice
    @PostMapping("/generate")
    public String generateInvoice(@RequestParam Long projectId,
                                  RedirectAttributes redirectAttributes) {

        try {
            if (projectId == null) {
                throw new IllegalArgumentException("Project ID is required");
            }

            InvoiceDTO invoice =
                    invoiceService.generateInvoice(projectId);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Invoice generated successfully."
            );

            return "redirect:/invoices/" + invoice.getInvoiceId();

        } catch (IllegalStateException ex) {
            // Business rule failure (e.g. invoice already exists)
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage()
            );

            return "redirect:/invoices/generate";

        } catch (Exception ex) {
            // Unexpected errors (DB, concurrency, etc.)
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Something went wrong while generating the invoice."
            );

            return "redirect:/invoices/generate";
        }
    }

    @PostMapping("/{id}/void")
    public String voidInvoice(@PathVariable Long id,
                              RedirectAttributes redirectAttributes) {

        try {

            invoiceService.voidInvoice(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Invoice voided successfully."
            );

        } catch (Exception ex) {

        redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Cannot void partially paid or paid invoices."
        );
    }

        return "redirect:/invoices";
    }
}
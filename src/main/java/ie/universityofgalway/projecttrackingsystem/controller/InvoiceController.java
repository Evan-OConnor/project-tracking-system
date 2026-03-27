package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.dto.InvoiceDTO;
import ie.universityofgalway.projecttrackingsystem.service.InvoiceService;
import ie.universityofgalway.projecttrackingsystem.service.document.DocumentService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final DocumentService documentService;


    // Constructor
    public InvoiceController(InvoiceService invoiceService, DocumentService documentService) {
        this.invoiceService = invoiceService;
        this.documentService = documentService;
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

    /**
     * Returns a PDF representation of the specified invoice.
     *
     * @param id the unique identifier of the invoice
     * @return a PDF response for the invoice, or 404 if the invoice does not exist
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getInvoicePdf(@PathVariable Long id) {
        try {
            byte[] pdf = documentService.generateInvoicePdf(id);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=invoice-" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteInvoice(@PathVariable Long id,
                              RedirectAttributes redirectAttributes) {

        try {

            invoiceService.deleteInvoice(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Invoice voided successfully."
            );

        } catch (IllegalStateException ex) {

        redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Cannot void partially paid or paid invoices."
        );
    }

        return "redirect:/invoices";
    }
}
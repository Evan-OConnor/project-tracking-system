package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.dto.InvoiceDTO;
import ie.universityofgalway.projecttrackingsystem.service.InvoiceService;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceRestController {

    private final InvoiceService invoiceService;

    public InvoiceRestController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    //Search
    @GetMapping("/search")
    public List<InvoiceDTO> searchInvoices(@RequestParam String query) {
        return invoiceService.searchInvoices(query);
    }

    // Get invoice summary
    @GetMapping("/{id}/summary")
    public InvoiceDTO getInvoiceSummary(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id);
    }

    // Get payment info
    @GetMapping("/{id}/payment-info")
    public Map<String, Object> getInvoicePaymentInfo(@PathVariable Long id) {

        InvoiceDTO invoice = invoiceService.getInvoiceById(id);

        Map<String, Object> data = new HashMap<>();
        data.put("total", invoice.getGrossTotal());

        return data;
    }
}
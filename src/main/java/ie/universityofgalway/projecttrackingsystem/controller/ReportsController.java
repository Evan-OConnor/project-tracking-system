package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.domain.core.*;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.InvoiceStatus;
import ie.universityofgalway.projecttrackingsystem.dto.ClientSummaryDto;
import ie.universityofgalway.projecttrackingsystem.repository.core.*;
import ie.universityofgalway.projecttrackingsystem.service.ContactService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reports")
public class ReportsController {

    private final ProjectRepository projectRepository;
    private final InvoiceRepository invoiceRepository;
    private final TimesheetEntryRepository timesheetRepository;
    private final ContactRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final ReceiptRepository receiptRepository;

    // Constructor
    public ReportsController(ProjectRepository projectRepository,
                             InvoiceRepository invoiceRepository,
                             TimesheetEntryRepository timesheetRepository,
                             ContactRepository clientRepository,
                             EmployeeRepository employeeRepository,
                             ReceiptRepository receiptRepository) {
        this.projectRepository = projectRepository;
        this.invoiceRepository = invoiceRepository;
        this.timesheetRepository = timesheetRepository;
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.receiptRepository = receiptRepository;
    }

    // Dashboard
    @GetMapping
    public String reportsHome(Model model) {
        model.addAttribute("clients", clientRepository.findAll());
        model.addAttribute("employees", employeeRepository.findAll());
        return "reports/index";
    }

    // Client Report
    @GetMapping("/client")
    public String clientReport(@RequestParam(required = false) Long clientId, Model model) {

        if (clientId == null) {
            return "redirect:/reports?error=selectClient";
        }

        var client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));

        List<Project> projects = projectRepository.findByClientContactId(clientId);

        List<Invoice> outstandingInvoices =
                invoiceRepository.findByProjectClientContactIdAndStatusIn(
                        clientId,
                        List.of(InvoiceStatus.GENERATED, InvoiceStatus.PARTIALLY_PAID)
                );

        List<Receipt> receipts = receiptRepository.findByClientId(clientId);

        int receiptCount = receipts.size();

        double totalReceived = receipts.stream()
                .mapToDouble(r -> r.getAmountPaid() != null ? r.getAmountPaid().doubleValue() : 0.0)
                .sum();

        double totalOutstanding = outstandingInvoices.stream()
                .mapToDouble(i -> i.getTotalExVat().doubleValue())
                .sum();

        model.addAttribute("totalOutstanding", totalOutstanding);
        model.addAttribute("client", client);
        model.addAttribute("projects", projects);
        model.addAttribute("invoices", outstandingInvoices);
        model.addAttribute("receipts", receipts);
        model.addAttribute("receiptCount", receiptCount);
        model.addAttribute("totalReceived", totalReceived);

        return "reports/client-report";
    }

    // All clients Summary
    @GetMapping("/summary")
    public String summaryReport(Model model) {

        List<ClientSummaryDto> summaries = clientRepository.getClientSummaries();

        model.addAttribute("summaries", summaries);

        return "reports/summary-report";
    }

    // Office Outstanding Invoices
    @GetMapping("/outstanding-invoices")
    public String outstandingInvoices(Model model) {

        List<Invoice> outstanding =
                invoiceRepository.findOutstandingWithClient(
                        List.of(InvoiceStatus.GENERATED, InvoiceStatus.PARTIALLY_PAID)
                );

        model.addAttribute("invoices", outstanding);

        return "reports/outstanding-invoices";
    }

    // Employee timesheet report
    @GetMapping("/timesheets")
    public String timesheetReport(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Model model) {

        model.addAttribute("employees", employeeRepository.findAll());

        if (employeeId == null || from == null || to == null) {
            return "reports/timesheet-report";
        }

        List<TimesheetEntry> entries =
                timesheetRepository.findByEmployeeIdAndEntryDateBetween(employeeId, from, to);

        double totalHours = entries.stream()
                .mapToDouble(e -> e.getHours().doubleValue())
                .sum();

        Map<String, Double> hoursByProject =
                entries.stream()
                        .collect(Collectors.groupingBy(
                                e -> e.getProject().getTitle(),
                                Collectors.summingDouble(e -> e.getHours().doubleValue())
                        ));

        model.addAttribute("entries", entries);
        model.addAttribute("totalHours", totalHours);
        model.addAttribute("hoursByProject", hoursByProject);

        return "reports/timesheet-report";
    }
}
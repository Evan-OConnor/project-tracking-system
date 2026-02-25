package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.dto.report.*;
import ie.universityofgalway.projecttrackingsystem.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // Project Summary Report

    @GetMapping("/projects/summary")
    public String projectSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String clientName,
            @RequestParam(required = false) String assessorName,
            Model model
    ) {
        List<ProjectSummary> summaries = reportService.getProjectSummary(startDate, endDate, status, clientName, assessorName);
        model.addAttribute("projectSummaries", summaries);
        return "reports/projectSummary";
    }

    // Outstanding Invoices Report

    @GetMapping("/invoices/outstanding")
    public String outstandingInvoices(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String clientName,
            @RequestParam(required = false) String status,
            Model model
    ) {
        List<OutstandingInvoice> invoices = reportService.getOutstandingInvoices(startDate, endDate, clientName, status);
        model.addAttribute("outstandingInvoices", invoices);
        return "reports/outstandingInvoices";
    }

    //  Invoice Register Report

    @GetMapping("/invoices/register")
    public String invoiceRegister(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String clientName,
            @RequestParam(required = false) String projectTitle,
            Model model
    ) {
        List<InvoiceRegister> invoices = reportService.getInvoiceRegister(startDate, endDate, clientName, projectTitle);
        model.addAttribute("invoiceRegisters", invoices);
        return "reports/invoiceRegister";
    }


    // Cost & Outlay Report

    @GetMapping("/costs")
    public String costItems(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String projectTitle,
            @RequestParam(required = false) String employeeName,
            Model model
    ) {
        List<CostItemReport> costItems = reportService.getCostItems(startDate, endDate, projectTitle, employeeName);
        model.addAttribute("costItems", costItems);
        return "reports/costItems";
    }


    // Timesheet & Workload Report

    @GetMapping("/timesheets")
    public String timesheetReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String projectTitle,
            Model model
    ) {
        List<TimesheetReport> timesheets = reportService.getTimesheetReport(startDate, endDate, employeeName, projectTitle);
        model.addAttribute("timesheetReports", timesheets);
        return "reports/timesheetReport";
    }

    // Revenue Summary Report

    @GetMapping("/revenue/summary")
    public String revenueSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model
    ) {
        RevenueSummary summary = reportService.getRevenueSummary(startDate, endDate);
        model.addAttribute("revenueSummary", summary);
        return "reports/revenueSummary";
    }
    @GetMapping
    public String reportsHomeRedirect() {
        return "redirect:/reports/home";
    }

    @GetMapping("/home")
    public String reportsHome() {
        return "reports/home";
    }


}

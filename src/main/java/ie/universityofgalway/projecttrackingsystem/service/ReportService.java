package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.dto.report.*;
import ie.universityofgalway.projecttrackingsystem.domain.core.*;
import ie.universityofgalway.projecttrackingsystem.repository.core.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final ProjectRepository projectRepository;
    private final InvoiceRepository invoiceRepository;
    private final TimesheetEntryRepository timesheetRepository;
    private final CostItemRepository costItemRepository;

    public ReportService(ProjectRepository projectRepository,
                         InvoiceRepository invoiceRepository,
                         TimesheetEntryRepository timesheetRepository,
                         CostItemRepository costItemRepository) {
        this.projectRepository = projectRepository;
        this.invoiceRepository = invoiceRepository;
        this.timesheetRepository = timesheetRepository;
        this.costItemRepository = costItemRepository;
    }


    // Project Summary Report

    public List<ProjectSummary> getProjectSummary(LocalDate startDate, LocalDate endDate,
                                                  String status, String clientName, String assessorName) {

        return projectRepository.findAll().stream()
                .filter(p -> startDate == null || !p.getStartDate().isBefore(startDate))
                .filter(p -> endDate == null || !p.getStartDate().isAfter(endDate))
                .filter(p -> (status == null ||
                        (p.getStatus() != null && p.getStatus().getName().equalsIgnoreCase(status))))
                .filter(p -> clientName == null || (p.getClientContact() != null && p.getClientContact().getName().equalsIgnoreCase(clientName)))
                .map(p -> {
                    // Assessor names from timesheets
                    String assessorNames = p.getTimesheets().stream()
                            .map(ts -> ts.getEmployee().getName())
                            .distinct()
                            .collect(Collectors.joining(", "));

                    // Total hours
                    BigDecimal totalHours = p.getTimesheets().stream()
                            .map(TimesheetEntry::getHours)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // Total costs
                    BigDecimal totalCosts = p.getCostItems().stream()
                            .map(CostItem::getCostAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // Total invoiced and received
                    BigDecimal totalInvoiced = p.getInvoices().stream()
                            .map(Invoice::getGrossTotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal totalReceived = p.getInvoices().stream()
                            .map(Invoice::getAmountPaid)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal outstanding = totalInvoiced.subtract(totalReceived);

                    return new ProjectSummary(
                            p.getId(),
                            p.getTitle(),
                            p.getClientContact() != null ? p.getClientContact().getName() : "",
                            p.getStatus() != null ? p.getStatus().getName() : "",
                            p.getStartDate(),
                            assessorNames,
                            totalHours,
                            totalCosts,
                            totalInvoiced,
                            totalReceived,
                            outstanding
                    );
                })
                // Filter by assessor if provided
                .filter(ps -> assessorName == null || ps.getAssessorNames().toLowerCase().contains(assessorName.toLowerCase()))
                .collect(Collectors.toList());
    }

    //  Outstanding Invoices Report

    public List<OutstandingInvoice> getOutstandingInvoices(LocalDate startDate, LocalDate endDate,
                                                           String clientName, String status) {

        return invoiceRepository.findAll().stream()
                .filter(i -> startDate == null || !i.getInvoiceDate().isBefore(startDate))
                .filter(i -> endDate == null || !i.getInvoiceDate().isAfter(endDate))
                .filter(i -> clientName == null || (i.getProject().getClientContact() != null &&
                        i.getProject().getClientContact().getName().equalsIgnoreCase(clientName)))
                .map(i -> {
                    BigDecimal discount = i.getDiscount() != null ? i.getDiscount() : BigDecimal.ZERO;
                    BigDecimal outstanding = i.getGrossTotal().subtract(i.getAmountPaid()).subtract(discount);

                    String invStatus;
                    if (i.getAmountPaid().compareTo(BigDecimal.ZERO) == 0) invStatus = "Unpaid";
                    else if (i.getAmountPaid().compareTo(i.getGrossTotal().subtract(discount)) < 0) invStatus = "Part-paid";
                    else invStatus = "Paid";

                    long daysOutstanding = ChronoUnit.DAYS.between(i.getInvoiceDate(), LocalDate.now());

                    return new OutstandingInvoice(
                            i.getId(),
                            i.getProject() != null ? i.getProject().getTitle() : "",
                            i.getProject() != null && i.getProject().getClientContact() != null ?
                                    i.getProject().getClientContact().getName() : "",
                            i.getInvoiceDate(),
                            i.getNetTotal(),
                            i.getVatTotal(),
                            i.getGrossTotal(),
                            discount,
                            i.getAmountPaid(),
                            outstanding,
                            daysOutstanding,
                            invStatus
                    );
                })
                .filter(o -> status == null || o.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }


    //  Invoice Register Report

    public List<InvoiceRegister> getInvoiceRegister(LocalDate startDate, LocalDate endDate,
                                                    String clientName, String projectTitle) {

        return invoiceRepository.findAll().stream()
                .filter(i -> startDate == null || !i.getInvoiceDate().isBefore(startDate))
                .filter(i -> endDate == null || !i.getInvoiceDate().isAfter(endDate))
                .filter(i -> clientName == null || (i.getProject().getClientContact() != null &&
                        i.getProject().getClientContact().getName().equalsIgnoreCase(clientName)))
                .filter(i -> projectTitle == null || (i.getProject() != null &&
                        i.getProject().getTitle().equalsIgnoreCase(projectTitle)))
                .map(i -> new InvoiceRegister(
                        i.getId(),
                        i.getInvoiceDate(),
                        i.getProject() != null ? i.getProject().getTitle() : "",
                        i.getProject() != null && i.getProject().getClientContact() != null ?
                                i.getProject().getClientContact().getName() : "",
                        i.getNetTotal(),
                        i.getVatTotal(),
                        i.getGrossTotal(),
                        i.getAmountPaid(),
                        i.getInvoiceDate()
                ))
                .collect(Collectors.toList());
    }

    //  Cost & Outlay Report
    public List<CostItemReport> getCostItems(LocalDate startDate, LocalDate endDate,
                                             String projectTitle, String employeeName) {

        return costItemRepository.findAll().stream()
                .filter(ci -> startDate == null || !ci.getCostDate().isBefore(startDate))
                .filter(ci -> endDate == null || !ci.getCostDate().isAfter(endDate))
                .filter(ci -> projectTitle == null || (ci.getProject() != null &&
                        ci.getProject().getTitle().equalsIgnoreCase(projectTitle)))
                .filter(ci -> employeeName == null || (ci.getEmployee() != null &&
                        ci.getEmployee().getName().toLowerCase().contains(employeeName.toLowerCase())))
                .map(ci -> new CostItemReport(
                        ci.getProject() != null ? ci.getProject().getTitle() : "",
                        ci.getEmployee() != null ? ci.getEmployee().getName() : "",
                        ci.getCostDate(),
                        ci.getDescription(),
                        ci.getCostAmount(),
                        ci.getDescription(),
                        ci.getSupplierContact() != null ? ci.getSupplierContact().getName() : ""
                ))
                .collect(Collectors.toList());
    }


    //  Timesheet & Workload Report

    public List<TimesheetReport> getTimesheetReport(LocalDate startDate, LocalDate endDate,
                                                    String employeeName, String projectTitle) {

        return timesheetRepository.findAll().stream()
                .filter(ts -> startDate == null || !ts.getEntryDate().isBefore(startDate))
                .filter(ts -> endDate == null || !ts.getEntryDate().isAfter(endDate))
                .filter(ts -> employeeName == null || (ts.getEmployee() != null &&
                        ts.getEmployee().getName().toLowerCase().contains(employeeName.toLowerCase())))
                .filter(ts -> projectTitle == null || (ts.getProject() != null &&
                        ts.getProject().getTitle().equalsIgnoreCase(projectTitle)))
                .map(ts -> new TimesheetReport(
                        ts.getEmployee() != null ? ts.getEmployee().getName() : "",
                        ts.getProject() != null ? ts.getProject().getTitle() : "",
                        ts.getEntryDate(),
                        ts.getHours(),
                        ts.getEmployee().getHourlyRate(),
                        ts.getHours().multiply(ts.getEmployee().getHourlyRate())
                ))
                .collect(Collectors.toList());
    }

    //  Revenue Summary Report

    public RevenueSummary getRevenueSummary(LocalDate startDate, LocalDate endDate) {

        List<Invoice> invoices = invoiceRepository.findAll();
        List<CostItem> costs = costItemRepository.findAll();

        BigDecimal totalNet = invoices.stream()
                .filter(i -> startDate == null || !i.getInvoiceDate().isBefore(startDate))
                .filter(i -> endDate == null || !i.getInvoiceDate().isAfter(endDate))
                .map(Invoice::getNetTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalVat = invoices.stream()
                .filter(i -> startDate == null || !i.getInvoiceDate().isBefore(startDate))
                .filter(i -> endDate == null || !i.getInvoiceDate().isAfter(endDate))
                .map(i -> i.getNetTotal().multiply(i.getVatRate().getRatePercent())
                        .divide(BigDecimal.valueOf(100))) // assuming ratePercent is like 23 for 23%
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal totalGross = invoices.stream()
                .filter(i -> startDate == null || !i.getInvoiceDate().isBefore(startDate))
                .filter(i -> endDate == null || !i.getInvoiceDate().isAfter(endDate))
                .map(Invoice::getGrossTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalReceived = invoices.stream()
                .filter(i -> startDate == null || !i.getInvoiceDate().isBefore(startDate))
                .filter(i -> endDate == null || !i.getInvoiceDate().isAfter(endDate))
                .map(Invoice::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOutstanding = totalGross.subtract(totalReceived);

        BigDecimal totalCosts = costs.stream()
                .filter(c -> startDate == null || !c.getCostDate().isBefore(startDate))
                .filter(c -> endDate == null || !c.getCostDate().isAfter(endDate))
                .map(CostItem::getCostAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netRevenue = totalGross.subtract(totalCosts);

        return new RevenueSummary(
                totalNet, totalVat, totalGross,
                totalReceived, totalOutstanding,
                totalCosts, netRevenue
        );
    }
}

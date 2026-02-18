package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.dto.ContactSummary;
import ie.universityofgalway.projecttrackingsystem.dto.EmployeeTimesheet;
import ie.universityofgalway.projecttrackingsystem.dto.InvoiceSummary;
import ie.universityofgalway.projecttrackingsystem.dto.ProjectReport;
import ie.universityofgalway.projecttrackingsystem.domain.core.Contact;
import ie.universityofgalway.projecttrackingsystem.domain.core.Employee;
import ie.universityofgalway.projecttrackingsystem.domain.core.Invoice;
import ie.universityofgalway.projecttrackingsystem.domain.core.Receipt;
import ie.universityofgalway.projecttrackingsystem.domain.core.Project;
import ie.universityofgalway.projecttrackingsystem.repository.core.EmployeeRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.InvoiceRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.ProjectRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final ProjectRepository projectRepository;
    private final InvoiceRepository invoiceRepository;
    private final EmployeeRepository employeeRepository;

    public ReportService(ProjectRepository projectRepository,
                         InvoiceRepository invoiceRepository,
                         EmployeeRepository employeeRepository) {
        this.projectRepository = projectRepository;
        this.invoiceRepository = invoiceRepository;
        this.employeeRepository = employeeRepository;
    }

    // --- All contacts summary ---
    public List<ContactSummary> getAllContactsSummary() {
        return projectRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(p -> p.getClientContact().getId()))
                .entrySet()
                .stream()
                .map(entry -> {
                    Contact contact = entry.getValue().get(0).getClientContact();
                    String contactName = contact.getName();
                    int projectCount = entry.getValue().size();

                    BigDecimal totalInvoices = entry.getValue().stream()
                            .flatMap(p -> p.getInvoices().stream())
                            .map(inv -> inv.getItems().stream()
                                    .map(i -> i.getUnitRate().multiply(i.getQuantity()))
                                    .reduce(BigDecimal.ZERO, BigDecimal::add))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalReceipts = entry.getValue().stream()
                            .flatMap(p -> p.getInvoices().stream())
                            .map(Invoice::getReceipt)
                            .filter(r -> r != null)
                            .map(Receipt::getAmountPaid)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // Outstanding invoices per contact
                    List<InvoiceSummary> outstanding = entry.getValue().stream()
                            .flatMap(p -> p.getInvoices().stream())
                            .filter(inv -> inv.getReceipt() == null)
                            .map(inv -> {
                                InvoiceSummary dto = new InvoiceSummary();
                                dto.setInvoiceId(inv.getId());
                                dto.setInvoiceDate(inv.getInvoiceDate());
                                dto.setTotalIncludingVat(inv.getItems().stream()
                                        .map(i -> i.getUnitRate().multiply(i.getQuantity()))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add));
                                dto.setContactName(contact.getName());
                                dto.setSolicitorName(contact.getName());
                                dto.setPhoneNumber(contact.getPhone());
                                return dto;
                            }).collect(Collectors.toList());

                    // Project reports/documents per contact
                    List<ProjectReport> reports = entry.getValue().stream()
                            .flatMap(p -> p.getProjectReports().stream())
                            .map(r -> {
                                ProjectReport pr = new ProjectReport();
                                pr.setTitle(r.getTitle());
                                pr.setDocumentType(r.getDocumentType().getName());
                                pr.setUploadedBy(r.getUploadedBy().getName());
                                pr.setStorageLocation(r.getStorageLocation());
                                return pr;
                            }).collect(Collectors.toList());

                    ContactSummary cs = new ContactSummary();
                    cs.setContactId(contact.getId());
                    cs.setContactName(contactName);
                    cs.setProjectCount(projectCount);
                    cs.setTotalInvoices(totalInvoices);
                    cs.setTotalReceipts(totalReceipts);
                    cs.setOutstandingInvoices(outstanding);
                    cs.setProjectReports(reports);
                    return cs;
                })
                .collect(Collectors.toList());
    }

    // --- Office-wide outstanding invoices ---
    public List<InvoiceSummary> getOfficeOutstandingInvoices() {
        return invoiceRepository.findAll()
                .stream()
                .filter(inv -> inv.getReceipt() == null)
                .map(inv -> {
                    InvoiceSummary dto = new InvoiceSummary();
                    dto.setInvoiceId(inv.getId());
                    dto.setInvoiceDate(inv.getInvoiceDate());
                    dto.setTotalIncludingVat(inv.getItems().stream()
                            .map(i -> i.getUnitRate().multiply(i.getQuantity()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                    Contact contact = inv.getProject().getClientContact();
                    dto.setContactName(contact.getName());
                    dto.setSolicitorName(contact.getName());
                    dto.setPhoneNumber(contact.getPhone());
                    return dto;
                }).collect(Collectors.toList());
    }

    // --- Employee timesheets ---
    public List<EmployeeTimesheet> getEmployeeTimesheets(Long employeeId, LocalDate start, LocalDate end) {
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeId));

        return emp.getTimesheetEntries().stream()
                .filter(te -> (start == null || !te.getEntryDate().isBefore(start)) &&
                        (end == null || !te.getEntryDate().isAfter(end)))
                .map(te -> {
                    EmployeeTimesheet dto = new EmployeeTimesheet();
                    dto.setEmployeeId(emp.getId());
                    dto.setEmployeeName(emp.getName());
                    dto.setDate(te.getEntryDate());
                    dto.setHoursWorked(te.getHours());
                    dto.setProjectTitle(te.getProject() != null ? te.getProject().getTitle() : "N/A");
                    return dto;
                }).collect(Collectors.toList());
    }

    // --- Filtered contacts ---
    public List<ContactSummary> getFilteredContacts(Long contactId, LocalDate start, LocalDate end) {
        return getAllContactsSummary().stream()
                .filter(c -> contactId == null || c.getContactId().equals(contactId))
                .filter(c -> {
                    if (start == null && end == null) return true;
                    return c.getProjectReports().stream()
                            .anyMatch(r -> {
                                LocalDate reportDate = r.getDateCreated().toLocalDate();
                                return (start == null || !reportDate.isBefore(start)) &&
                                        (end == null || !reportDate.isAfter(end));
                            });
                }).collect(Collectors.toList());
    }

    // --- Filtered employee timesheets ---
    public List<EmployeeTimesheet> getFilteredEmployeeTimesheets(Long employeeId, LocalDate start, LocalDate end) {
        return getEmployeeTimesheets(employeeId, start, end);
    }
}

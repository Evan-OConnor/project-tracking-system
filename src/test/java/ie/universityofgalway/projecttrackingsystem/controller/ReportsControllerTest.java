package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.domain.core.*;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.InvoiceStatus;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.ProjectStatus;
import ie.universityofgalway.projecttrackingsystem.dto.ClientSummaryDto;
import ie.universityofgalway.projecttrackingsystem.repository.core.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportsController.class)
@WithMockUser(username = "testuser", roles = {"USER"})
class ReportsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectRepository projectRepository;

    @MockitoBean
    private InvoiceRepository invoiceRepository;

    @MockitoBean
    private TimesheetEntryRepository timesheetRepository;

    @MockitoBean
    private ContactRepository clientRepository;

    @MockitoBean
    private EmployeeRepository employeeRepository;

    // -------------------------
    // DASHBOARD
    // -------------------------

    @Test
    void reportsHome_returnsDashboard() throws Exception {
        when(clientRepository.findAll()).thenReturn(List.of());
        when(employeeRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/reports"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/index"))
                .andExpect(model().attributeExists("clients", "employees"));
    }

    // -------------------------
    // CLIENT REPORT
    // -------------------------

    @Test
    void clientReport_missingClientId_redirects() throws Exception {
        mockMvc.perform(get("/reports/client"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reports?error=selectClient"));
    }

    @Test
    void clientReport_validClient_returnsReport() throws Exception {
        Long clientId = 1L;

        Contact client = mock(Contact.class);
        Project project = mock(Project.class);
        Invoice invoice = mock(Invoice.class);

        ProjectStatus status = mock(ProjectStatus.class);
        when(status.getName()).thenReturn("ACTIVE");
        when(project.getStatus()).thenReturn(status);

        when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));

        when(projectRepository.findByClientContactId(clientId))
                .thenReturn(List.of(project));

        when(invoiceRepository.findByProjectClientContactIdAndStatusIn(any(), any()))
                .thenReturn(List.of(invoice));

        mockMvc.perform(get("/reports/client")
                        .param("clientId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/client-report"))
                .andExpect(model().attributeExists("client", "projects", "invoices"));
    }

    @Test
    void clientReport_invalidClient_throwsException() throws Exception {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/reports/client")
                        .param("clientId", "99"))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("Client not found")); // optional but nice
    }

    // -------------------------
    // SUMMARY REPORT
    // -------------------------

    @Test
    void summaryReport_returnsSummaries() throws Exception {
        // FIX: use long instead of BigDecimal
        ClientSummaryDto dto = new ClientSummaryDto("Client A", 2L, 100L, 50L);

        when(clientRepository.getClientSummaries())
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/reports/summary"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/summary-report"))
                .andExpect(model().attributeExists("summaries"));
    }

    @Test
    void summaryReport_empty_returnsEmptyList() throws Exception {
        when(clientRepository.getClientSummaries())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reports/summary"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("summaries", Collections.emptyList()));
    }

    // -------------------------
    // OUTSTANDING INVOICES
    // -------------------------

    @Test
    void outstandingInvoices_returnsInvoices() throws Exception {
        Invoice invoice = mock(Invoice.class);
        Project project = mock(Project.class);
        Contact client = mock(Contact.class);

        when(client.getName()).thenReturn("Client A");
        when(project.getClientContact()).thenReturn(client);
        when(invoice.getProject()).thenReturn(project);

        when(invoiceRepository.findOutstandingWithClient(
                List.of(InvoiceStatus.GENERATED, InvoiceStatus.PARTIALLY_PAID)))
                .thenReturn(List.of(invoice));

        mockMvc.perform(get("/reports/outstanding-invoices"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/outstanding-invoices"))
                .andExpect(model().attributeExists("invoices"));
    }

    @Test
    void outstandingInvoices_emptyList() throws Exception {
        when(invoiceRepository.findOutstandingWithClient(any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reports/outstanding-invoices"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("invoices", Collections.emptyList()));
    }

    // -------------------------
    // TIMESHEET REPORT
    // -------------------------

    @Test
    void timesheetReport_missingParams_returnsEmptyView() throws Exception {
        when(employeeRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/reports/timesheets"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/timesheet-report"))
                .andExpect(model().attributeExists("employees"));
    }

    @Test
    void timesheetReport_validParams_calculatesTotals() throws Exception {
        // FIX: mock instead of new TimesheetEntry()
        TimesheetEntry e1 = mock(TimesheetEntry.class);
        TimesheetEntry e2 = mock(TimesheetEntry.class);

        Project project = mock(Project.class);

        when(project.getTitle()).thenReturn("Project A");

        when(e1.getHours()).thenReturn(BigDecimal.valueOf(5));
        when(e1.getProject()).thenReturn(project);

        when(e2.getHours()).thenReturn(BigDecimal.valueOf(3));
        when(e2.getProject()).thenReturn(project);

        when(employeeRepository.findAll()).thenReturn(List.of());
        when(timesheetRepository.findByEmployeeIdAndEntryDateBetween(any(), any(), any()))
                .thenReturn(List.of(e1, e2));

        mockMvc.perform(get("/reports/timesheets")
                        .param("employeeId", "1")
                        .param("from", "2024-01-01")
                        .param("to", "2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("entries", "totalHours", "hoursByProject"))
                .andExpect(model().attribute("totalHours", 8.0));
    }

    @Test
    void timesheetReport_noEntries_returnsZeroTotals() throws Exception {
        when(employeeRepository.findAll()).thenReturn(List.of());
        when(timesheetRepository.findByEmployeeIdAndEntryDateBetween(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reports/timesheets")
                        .param("employeeId", "1")
                        .param("from", "2024-01-01")
                        .param("to", "2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("totalHours", 0.0));
    }
}
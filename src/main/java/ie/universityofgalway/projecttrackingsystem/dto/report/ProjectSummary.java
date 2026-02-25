package ie.universityofgalway.projecttrackingsystem.dto.report;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProjectSummary {

    private Long projectId;
    private String projectTitle;
    private String clientName;
    private String status;

    private LocalDate startDate;

    private String assessorNames; // derived from timesheets

    private BigDecimal totalHoursLogged;
    private BigDecimal totalCosts; // from CostItem

    private BigDecimal totalInvoicedGross;
    private BigDecimal totalReceived;
    private BigDecimal outstandingBalance;

    public ProjectSummary(
            Long projectId,
            String projectTitle,
            String clientName,
            String status,
            LocalDate startDate,
            String assessorNames,
            BigDecimal totalHoursLogged,
            BigDecimal totalCosts,
            BigDecimal totalInvoicedGross,
            BigDecimal totalReceived,
            BigDecimal outstandingBalance
    ) {
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.clientName = clientName;
        this.status = status;
        this.startDate = startDate;
        this.assessorNames = assessorNames;
        this.totalHoursLogged = totalHoursLogged;
        this.totalCosts = totalCosts;
        this.totalInvoicedGross = totalInvoicedGross;
        this.totalReceived = totalReceived;
        this.outstandingBalance = outstandingBalance;
    }

    public Long getProjectId() { return projectId; }
    public String getProjectTitle() { return projectTitle; }
    public String getClientName() { return clientName; }
    public String getStatus() { return status; }
    public LocalDate getStartDate() { return startDate; }
    public String getAssessorNames() { return assessorNames; }
    public BigDecimal getTotalHoursLogged() { return totalHoursLogged; }
    public BigDecimal getTotalCosts() { return totalCosts; }
    public BigDecimal getTotalInvoicedGross() { return totalInvoicedGross; }
    public BigDecimal getTotalReceived() { return totalReceived; }
    public BigDecimal getOutstandingBalance() { return outstandingBalance; }
}

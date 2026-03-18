package ie.universityofgalway.projecttrackingsystem.dto;

import ie.universityofgalway.projecttrackingsystem.domain.core.*;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.DocumentType;

import java.math.BigDecimal;
import java.util.List;

public class ProjectDetailsView {

    private Project project;
    private List<CostItemView> outlays;
    private List<CostItemView> expenses;
    private BigDecimal outlayTotal;
    private BigDecimal expenseTotal;
    private List<TimesheetEntryView> timesheets;
    private BigDecimal labourTotal;
    private BigDecimal totalExVat;
    private List<Receipt> receipts;
    private BigDecimal receiptsTotal;
    private List<ProjectReportDocument> reports;
    private List<DocumentType> documentTypes;

    // Getters

    public Project getProject() { return project; }

    public List<CostItemView> getOutlays() { return outlays; }

    public List<CostItemView> getExpenses() { return expenses; }

    public BigDecimal getOutlayTotal() { return outlayTotal; }

    public BigDecimal getExpenseTotal() { return expenseTotal; }

    public List<TimesheetEntryView> getTimesheets() { return timesheets; }

    public BigDecimal getLabourTotal() { return labourTotal; }

    public BigDecimal getTotalExVat() { return totalExVat; }

    public List<Receipt> getReceipts() { return receipts; }

    public BigDecimal getReceiptsTotal() { return receiptsTotal; }

    public List<ProjectReportDocument> getReports() { return reports; }

    public List<DocumentType> getDocumentTypes() { return documentTypes; }

    // Setters
    public void setProject(Project project) { this.project = project; }

    public void setOutlays(List<CostItemView> outlays) { this.outlays = outlays; }

    public void setExpenses(List<CostItemView> expenses) { this.expenses = expenses; }

    public void setOutlayTotal(BigDecimal outlayTotal) { this.outlayTotal = outlayTotal; }

    public void setExpenseTotal(BigDecimal expenseTotal) { this.expenseTotal = expenseTotal; }

    public void setTimesheets(List<TimesheetEntryView> timesheets) { this.timesheets = timesheets; }

    public void setLabourTotal(BigDecimal labourTotal) { this.labourTotal = labourTotal; }

    public void setTotalExVat(BigDecimal totalExVat) { this.totalExVat = totalExVat; }

    public void setReceipts(List<Receipt> receipts) { this.receipts = receipts; }

    public void setReceiptsTotal(BigDecimal receiptsTotal) { this.receiptsTotal = receiptsTotal; }

    public void setReports(List<ProjectReportDocument> reports) { this.reports = reports; }

    public void setDocumentTypes(List<DocumentType> documentTypes) { this.documentTypes = documentTypes; }
}
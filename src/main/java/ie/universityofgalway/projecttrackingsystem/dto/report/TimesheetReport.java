package ie.universityofgalway.projecttrackingsystem.dto.report;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TimesheetReport {

    private String employeeName;
    private String projectTitle;

    private LocalDate entryDate;
    private BigDecimal hours;

    private BigDecimal hourlyRate;
    private BigDecimal totalChargeable;

    public TimesheetReport(
            String employeeName,
            String projectTitle,
            LocalDate entryDate,
            BigDecimal hours,
            BigDecimal hourlyRate,
            BigDecimal totalChargeable
    ) {
        this.employeeName = employeeName;
        this.projectTitle = projectTitle;
        this.entryDate = entryDate;
        this.hours = hours;
        this.hourlyRate = hourlyRate;
        this.totalChargeable = totalChargeable;
    }

    public String getEmployeeName() { return employeeName; }
    public String getProjectTitle() { return projectTitle; }
    public LocalDate getEntryDate() { return entryDate; }
    public BigDecimal getHours() { return hours; }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public BigDecimal getTotalChargeable() { return totalChargeable; }
}

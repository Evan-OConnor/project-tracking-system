package ie.universityofgalway.projecttrackingsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TimesheetEntryView {
    private Long id;
    private String projectTitle;
    private String employeeName;
    private String workDescription;
    private LocalDate entryDate;
    private BigDecimal hours;
    private BigDecimal charge;
    private boolean billed;

    public TimesheetEntryView(Long id, String projectTitle, String employeeName,
                              String workDescription, LocalDate entryDate, BigDecimal hours, BigDecimal charge, boolean billed) {
        this.id = id;
        this.projectTitle = projectTitle;
        this.employeeName = employeeName;
        this.workDescription = workDescription;
        this.entryDate = entryDate;
        this.hours = hours;
        this.charge = charge;
        this.billed = billed;
    }

    // Getters
    public Long getId() { return id; }
    public String getProjectTitle() { return projectTitle; }
    public String getEmployeeName() { return employeeName; }
    public String getWorkDescription() { return workDescription; }
    public LocalDate getEntryDate() { return entryDate; }
    public BigDecimal getHours() { return hours; }
    public BigDecimal getCharge() {return charge;}
    public boolean isBilled(){return billed;}
}

package ie.universityofgalway.projecttrackingsystem.dto;

import java.math.BigDecimal;

public class EmployeeAdminView {
    private final Long id;
    private final String name;
    private final String address;
    private final BigDecimal hourlyRate;

    public EmployeeAdminView(Long id, String name, String address, BigDecimal hourlyRate) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.hourlyRate = hourlyRate;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public BigDecimal getHourlyRate() { return hourlyRate; }
}
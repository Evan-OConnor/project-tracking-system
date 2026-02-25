package ie.universityofgalway.projecttrackingsystem.dto;

import java.math.BigDecimal;

public class EmployeeView {
    private Long id;
    private String name;
    private String address;
    private BigDecimal hourlyRate;

    public EmployeeView(Long id, String name, String address, BigDecimal hourlyRate) {
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

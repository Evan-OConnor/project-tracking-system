package ie.universityofgalway.projecttrackingsystem.domain.core;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long id;

    @NotBlank(message = "Employee name is required")
    @Size(max = 150, message = "Employee name cannot exceed 150 characters")
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Positive
    @Column(name = "hourly_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimesheetEntry> timesheetEntries = new ArrayList<>();

    protected Employee() {
    }

    public Employee(String name, BigDecimal hourlyRate) {
        this.name = name;
        this.hourlyRate = hourlyRate;
    }

    // Getters

    public Long getId() { return id; }

    public String getName() { return name; }

    public String getAddress() { return address; }

    public BigDecimal getHourlyRate() { return hourlyRate; }

    public List<TimesheetEntry> getTimesheetEntries() { return timesheetEntries; }


    // Setters

    public void setName(String name) { this.name = name; }

    public void setAddress(String address) { this.address = address; }

    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }

    public void setTimesheetEntries(List<TimesheetEntry> timesheetEntries) { this.timesheetEntries = timesheetEntries; }

    // Utility

    @Override
    public String toString() {
        return name;
    }
}
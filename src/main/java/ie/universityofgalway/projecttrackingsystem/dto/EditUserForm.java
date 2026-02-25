package ie.universityofgalway.projecttrackingsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

public class EditUserForm {

    private Long employeeId;

    @NotBlank(message = "Employee name is required")
    private String employeeName;

    @NotNull(message = "Hourly rate is required")
    @Positive(message = "Hourly rate must be positive")
    @Digits(integer = 8, fraction = 2, message = "Hourly rate invalid")
    private BigDecimal hourlyRate;

    private String address;

    // optional password fields
    private String password;
    private String confirmPassword;

    private boolean active;

    public EditUserForm() {}

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordConfirmed() {
        if (this.password == null || this.password.isBlank()) return true;
        if (this.confirmPassword == null || this.confirmPassword.isBlank()) return false;
        return this.password.equals(this.confirmPassword);
    }

    @AssertTrue(message = "Password must be at least 8 characters long")
    public boolean isPasswordLongEnough() {
        if (this.password == null || this.password.isBlank()) return true;
        return this.password.length() >= 8;
    }
}

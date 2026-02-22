package ie.universityofgalway.projecttrackingsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;

import java.math.BigDecimal;

public class CreateUserForm {

    @NotBlank(message = "Employee name is required")
    private String employeeName;

    @NotNull(message = "Hourly rate is required")
    @Positive(message = "Hourly rate must be positive")
    private BigDecimal hourlyRate;

    private String address;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    // Constructors

    public CreateUserForm() {
    }

    // Getters

    public String getEmployeeName() {
        return employeeName;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public String getAddress() {
        return address;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    // Setters

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordConfirmed() {
        if (this.password == null || this.confirmPassword == null) {
            return true;
        }
        return this.password.equals(this.confirmPassword);
    }
}

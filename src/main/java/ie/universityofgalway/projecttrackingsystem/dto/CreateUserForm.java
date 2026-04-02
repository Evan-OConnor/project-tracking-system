package ie.universityofgalway.projecttrackingsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

public class CreateUserForm {

    @NotBlank(message = "Employee name is required")
    @Size(max = 150, message = "Employee name cannot exceed 150 characters")
    private String employeeName;

    @NotNull(message = "Hourly rate is required")
    @Positive(message = "Hourly rate must be positive")
    @Digits(integer = 8, fraction = 2, message = "Hourly rate invalid")
    private BigDecimal hourlyRate;

    private String address;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @NotBlank(message = "Role is required")
    private String roleName = "STAFF";

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

    public String getRoleName() {
        return roleName;
    }

    // Setters

    public void setEmployeeName(String employeeName) {
        this.employeeName = (employeeName != null) ? employeeName.trim() : null;
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

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordConfirmed() {
        if (this.password == null || this.confirmPassword == null) {
            return true;
        }
        return this.password.equals(this.confirmPassword);
    }
}

package ie.universityofgalway.projecttrackingsystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binds company-related configuration properties from application.properties.
 */
@Component
@ConfigurationProperties(prefix = "company")
public class CompanyProperties {

    private String name;
    private String address;
    private String phone;
    private String email;

    // Getters

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    // Setters

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
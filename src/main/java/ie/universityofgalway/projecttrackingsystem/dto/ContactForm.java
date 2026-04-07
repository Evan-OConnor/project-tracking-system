package ie.universityofgalway.projecttrackingsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public class ContactForm {

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
    private String name;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    @Pattern(regexp = "^[0-9+\\-\\s]*$", message = "Phone number can only contain digits, spaces, + or -")
    @Size(max = 50, message = "Phone cannot exceed 50 characters")
    private String phone;

    @Pattern(regexp = "^[0-9+\\-\\s]*$", message = "Fax can only contain digits, spaces, + or -")
    @Size(max = 50, message = "Fax cannot exceed 50 characters")
    private String fax;

    @Size(max = 1000, message = "Comments cannot exceed 1000 characters")
    private String comments;


    // Getters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getFax() {
        return fax;
    }

    public String getComments() {
        return comments;
    }

    // Setters

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = trim(name);
    }

    public void setAddress(String address) {
        this.address = trim(address);
    }

    public void setPhone(String phone) {this.phone = trim(phone);}

    public void setFax(String fax) {
        this.fax = trim(fax);
    }

    public void setComments(String comments) {
        this.comments = trim(comments);
    }

    private String trim(String value) {
        if (value == null) return null;

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
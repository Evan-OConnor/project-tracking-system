package ie.universityofgalway.projecttrackingsystem.dto;

public class ContactView {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String fax;

    public ContactView(Long id, String name, String address, String phone, String fax) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.fax = fax;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getFax() { return fax; }
}

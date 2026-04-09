package ie.universityofgalway.projecttrackingsystem.dto;

public class EmployeeView {
    private final Long id;
    private final String name;
    private final String role;

    public EmployeeView(Long id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }


    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
}
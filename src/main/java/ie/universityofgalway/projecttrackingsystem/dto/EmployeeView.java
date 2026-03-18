package ie.universityofgalway.projecttrackingsystem.dto;

public class EmployeeView {
    private final Long id;
    private final String name;

    public EmployeeView(Long id, String name) {
        this.id = id;
        this.name = name;
    }


    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
}
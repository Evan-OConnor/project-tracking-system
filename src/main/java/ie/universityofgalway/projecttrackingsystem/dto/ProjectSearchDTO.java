package ie.universityofgalway.projecttrackingsystem.dto;

public class ProjectSearchDTO {
    private Long id;
    private String title;

    // Constructor
    public ProjectSearchDTO(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
}

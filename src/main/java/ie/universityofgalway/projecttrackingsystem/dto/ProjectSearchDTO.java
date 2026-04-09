package ie.universityofgalway.projecttrackingsystem.dto;

public class ProjectSearchDTO {
    private Long id;
    private String title;

    public ProjectSearchDTO(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
}

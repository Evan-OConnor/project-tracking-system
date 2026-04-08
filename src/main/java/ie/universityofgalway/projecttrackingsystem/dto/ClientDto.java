package ie.universityofgalway.projecttrackingsystem.dto;

public class ClientDto {

    private Long id;
    private String name;

    public ClientDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}

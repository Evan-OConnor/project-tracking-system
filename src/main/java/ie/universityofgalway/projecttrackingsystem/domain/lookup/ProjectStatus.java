package ie.universityofgalway.projecttrackingsystem.domain.lookup;

import jakarta.persistence.*;

@Entity
@Table(
        name = "project_status",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_project_status_name",
                columnNames = "name"
        )
)
public class ProjectStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    protected ProjectStatus() {
    }

    public ProjectStatus(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {return name;}
}

package ie.universityofgalway.projecttrackingsystem.domain.lookup;

import jakarta.persistence.*;

@Entity
@Table(
        name = "project_category",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_project_category_name",
                columnNames = "name"
        )
)
public class ProjectCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    protected ProjectCategory() {
    }

    public ProjectCategory(String name) {
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
}

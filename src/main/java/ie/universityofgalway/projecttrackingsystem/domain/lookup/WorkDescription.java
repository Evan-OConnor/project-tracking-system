package ie.universityofgalway.projecttrackingsystem.domain.lookup;

import jakarta.persistence.*;

@Entity
@Table(
        name = "work_description",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_work_description_name",
                columnNames = "name"
        )
)
public class WorkDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_description_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    protected WorkDescription() {
    }

    public WorkDescription (String name) {
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

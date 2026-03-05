package ie.universityofgalway.projecttrackingsystem.domain.security;

import jakarta.persistence.*;

@Entity
@Table(
        name = "system_role",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_system_role_name",
                        columnNames = "name"
                )
        }
)
public class SystemRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    // Constructors

    protected SystemRole() {
    }

    public SystemRole(String name) {
        this.name = name;
    }

    // Getters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

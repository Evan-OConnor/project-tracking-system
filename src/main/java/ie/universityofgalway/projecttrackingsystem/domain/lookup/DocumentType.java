package ie.universityofgalway.projecttrackingsystem.domain.lookup;

import jakarta.persistence.*;

@Entity
@Table(
        name = "document_type",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_document_type_name",
                columnNames = "name"
        )
)
public class DocumentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_type_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    protected DocumentType() {
    }

    public DocumentType(String name) {
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

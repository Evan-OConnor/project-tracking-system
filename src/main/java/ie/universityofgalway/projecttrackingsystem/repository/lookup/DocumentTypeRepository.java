package ie.universityofgalway.projecttrackingsystem.repository.lookup;

import ie.universityofgalway.projecttrackingsystem.domain.lookup.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {
}

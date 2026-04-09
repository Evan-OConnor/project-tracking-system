package ie.universityofgalway.projecttrackingsystem.repository.core;

import ie.universityofgalway.projecttrackingsystem.domain.core.Contact;
import ie.universityofgalway.projecttrackingsystem.dto.ClientSummaryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    Optional<Contact> findByNameIgnoreCase(String name);

    List<Contact> findByNameContainingIgnoreCase(String name);

    List<Contact> findByNameContainingIgnoreCaseOrPhoneContaining(String name, String phone);

    @Query("""
    SELECT new ie.universityofgalway.projecttrackingsystem.dto.ClientSummaryDto(
        c.name,
        COUNT(DISTINCT p.id),
        COUNT(DISTINCT i.id),
        COUNT(DISTINCT r.id)
    )
    FROM Contact c
    LEFT JOIN Project p ON p.clientContact.id = c.id
    LEFT JOIN Invoice i ON i.project.id = p.id
    LEFT JOIN Receipt r ON r.invoice.id = i.id
    GROUP BY c.id, c.name
""")
    List<ClientSummaryDto> getClientSummaries();
}




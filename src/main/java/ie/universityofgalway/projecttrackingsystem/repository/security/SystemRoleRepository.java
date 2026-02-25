package ie.universityofgalway.projecttrackingsystem.repository.security;

import ie.universityofgalway.projecttrackingsystem.domain.security.SystemRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemRoleRepository extends JpaRepository<SystemRole, Long> {
    Optional<SystemRole> findByName(String name);
}

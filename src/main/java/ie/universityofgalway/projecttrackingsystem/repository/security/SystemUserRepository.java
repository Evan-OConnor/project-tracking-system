package ie.universityofgalway.projecttrackingsystem.repository.security;

import ie.universityofgalway.projecttrackingsystem.domain.security.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemUserRepository extends JpaRepository<SystemUser, Long> {
    Optional<SystemUser> findByUsername(String username);

    Optional<SystemUser> findByEmployeeId(Long employeeId);

    void deleteByEmployeeId(Long employeeId);
}

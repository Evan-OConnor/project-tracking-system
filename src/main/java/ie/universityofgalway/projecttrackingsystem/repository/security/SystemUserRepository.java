package ie.universityofgalway.projecttrackingsystem.repository.security;

import ie.universityofgalway.projecttrackingsystem.domain.security.SystemUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemUserRepository extends JpaRepository<SystemUser, Long> {
    Optional<SystemUser> findByUsername(String username);

    Optional<SystemUser> findByEmployeeId(Long employeeId);

    void deleteByEmployeeId(Long employeeId);

    @Query("select u from SystemUser u join u.employee e where lower(u.username) like lower(concat('%', :q, '%')) or lower(e.name) like lower(concat('%', :q, '%'))")
    List<SystemUser> searchByUsernameOrEmployeeName(@Param("q") String q);

    @Query("select u from SystemUser u join u.employee e where lower(u.username) like lower(concat('%', :q, '%')) or lower(e.name) like lower(concat('%', :q, '%'))")
    Page<SystemUser> searchByUsernameOrEmployeeName(@Param("q") String q, Pageable pageable);

    @Query("""
    select u
    from SystemUser u
    join u.employee e
    where u.active = true
    and (
    lower(u.username) like lower(concat('%', :q, '%'))
    or lower(e.name) like lower(concat('%', :q, '%'))
    )
    """)
    List<SystemUser> searchActiveUsers(@Param("q") String q);

    @Query("""
    select u
    from SystemUser u
    join u.employee e
    where u.active = true
    """)
    List<SystemUser> findActiveUsers();
}

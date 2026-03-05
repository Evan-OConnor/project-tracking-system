package ie.universityofgalway.projecttrackingsystem.domain.security;

import ie.universityofgalway.projecttrackingsystem.domain.core.Employee;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "system_user",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_system_user_username",
                        columnNames = "username"
                )
        }
)
public class SystemUser {

    @Id
    @Column(name = "employee_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private SystemRole role;

    @Column(name = "username", length = 20)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;

    // Constructors

    protected SystemUser() {
    }

    public SystemUser(Employee employee, SystemRole role, String passwordHash) {
        this.employee = employee;
        this.role = role;
        this.passwordHash = passwordHash;
        this.active = true;
    }

    // Getters

    public Long getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public SystemRole getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setRole(SystemRole role) {
        this.role = role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

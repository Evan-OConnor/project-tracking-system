-- 1) Create employee row & temporarily store employee_id
INSERT INTO employee (name, hourly_rate)
VALUES ('System Admin', 1.00);

SET @employee_id := LAST_INSERT_ID();

-- 2) Create system_user row linked to employee with ADMIN role
INSERT INTO system_user (employee_id, role_id, password_hash)
SELECT
    @employee_id,
    r.role_id,
    '$2a$10$3alQB1meumun9zEHyBbOGeFNiN/FCnucaw9TXE.x1jB14/BEOBG5m'
FROM system_role r
WHERE r.name = 'ADMIN';

-- 3) Generate username for system_user (now uses employee_id as PK)
UPDATE system_user
SET username = CONCAT('U', LPAD(employee_id, 6, '0'))
WHERE employee_id = @employee_id;

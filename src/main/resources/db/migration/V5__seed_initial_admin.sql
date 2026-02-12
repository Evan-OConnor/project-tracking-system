-- 1) Create employee row & temporarily store employee_id
INSERT INTO employee (name, hourly_rate)
VALUES ('System Admin', 1.00);

SET @employee_id := LAST_INSERT_ID();

-- 2) Create system_user row linked to employee & temporarily store user_id
INSERT INTO system_user (employee_id, password_hash) VALUES
    (@employee_id,
     '$2a$10$3alQB1meumun9zEHyBbOGeFNiN/FCnucaw9TXE.x1jB14/BEOBG5m');

SET @user_id := LAST_INSERT_ID();

-- 3) Generate username for system_user
UPDATE system_user
SET username = CONCAT('U', LPAD(user_id, 6, '0'))
WHERE user_id = @user_id;

-- 4) Assign ADMIN role to system_user
INSERT INTO system_user_role (user_id, role_id)
SELECT @user_id, r.role_id
FROM system_role r
WHERE r.name = 'ADMIN';
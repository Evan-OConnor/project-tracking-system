CREATE TABLE system_role (
    role_id BIGINT NOT NULL AUTO_INCREMENT,

    name VARCHAR(50) NOT NULL,

    CONSTRAINT pk_system_role PRIMARY KEY (role_id),

    CONSTRAINT uk_system_role_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE system_user (
    employee_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,

    username VARCHAR(20) NULL,
    password_hash VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_system_user PRIMARY KEY (employee_id),

    CONSTRAINT fk_system_user_employee FOREIGN KEY (employee_id) REFERENCES employee(employee_id),
    CONSTRAINT fk_system_user_role FOREIGN KEY (role_id) REFERENCES system_role(role_id),

    CONSTRAINT uk_system_user_username UNIQUE (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

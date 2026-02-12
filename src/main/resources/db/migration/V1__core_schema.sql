-- Lookup Tables

CREATE TABLE project_category (
    category_id BIGINT NOT NULL AUTO_INCREMENT,

    name VARCHAR(100) NOT NULL,

    CONSTRAINT pk_project_category PRIMARY KEY (category_id),

    CONSTRAINT uk_project_category_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE project_status (
    status_id BIGINT NOT NULL AUTO_INCREMENT,

    name VARCHAR(100) NOT NULL,

    CONSTRAINT pk_project_status PRIMARY KEY (status_id),

    CONSTRAINT uk_project_status_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE vat_rate (
    vat_rate_id BIGINT NOT NULL AUTO_INCREMENT,

    rate_percent DECIMAL(5,2) NOT NULL,

    CONSTRAINT pk_vat_rate PRIMARY KEY(vat_rate_id),

    CONSTRAINT uk_vat_rate_percent UNIQUE (rate_percent),

    CONSTRAINT ck_vat_rate_percent_range CHECK (rate_percent >= 0 AND rate_percent <= 100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE work_description (
    work_description_id BIGINT NOT NULL AUTO_INCREMENT,

    name VARCHAR(100) NOT NULL,

    CONSTRAINT pk_work_description PRIMARY KEY (work_description_id),

    CONSTRAINT uk_work_description_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE document_type (
    document_type_id BIGINT NOT NULL AUTO_INCREMENT,

    name VARCHAR(100) NOT NULL,

    CONSTRAINT pk_document_type PRIMARY KEY (document_type_id),

    CONSTRAINT uk_document_type_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Core Tables

CREATE TABLE contact (
    contact_id BIGINT NOT NULL AUTO_INCREMENT,

    name VARCHAR(150) NOT NULL,
    address VARCHAR(255) NULL,
    phone VARCHAR(50) NULL,
    fax VARCHAR(50) NULL,
    comments TEXT NULL,

    CONSTRAINT pk_contact PRIMARY KEY (contact_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE employee (
    employee_id BIGINT NOT NULL AUTO_INCREMENT,

    name VARCHAR(150) NOT NULL,
    address VARCHAR(255) NULL,
    hourly_rate DECIMAL(10,2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_employee PRIMARY KEY (employee_id),

    CONSTRAINT ck_employee_hourly_rate_positive CHECK (hourly_rate > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE project (
    project_id BIGINT NOT NULL AUTO_INCREMENT,

    category_id BIGINT NOT NULL,
    status_id BIGINT NOT NULL,
    client_contact_id BIGINT NOT NULL,
    solicitor_contact_id BIGINT NULL,
    insurance_company_contact_id BIGINT NULL,

    start_date DATE NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NULL,

    CONSTRAINT pk_project PRIMARY KEY (project_id),

    CONSTRAINT fk_project_category FOREIGN KEY (category_id) REFERENCES project_category(category_id),
    CONSTRAINT fk_project_status FOREIGN KEY (status_id) REFERENCES project_status(status_id),
    CONSTRAINT fk_project_client_contact FOREIGN KEY (client_contact_id) REFERENCES contact(contact_id),
    CONSTRAINT fk_project_solicitor_contact FOREIGN KEY (solicitor_contact_id) REFERENCES contact(contact_id),
    CONSTRAINT fk_project_insurance_contact FOREIGN KEY (insurance_company_contact_id) REFERENCES contact(contact_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE timesheet_entry (
    timesheet_entry_id BIGINT NOT NULL AUTO_INCREMENT,

    project_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    work_description_id BIGINT NOT NULL,

    entry_date DATE NOT NULL,
    hours DECIMAL(6,2) NOT NULL,

    CONSTRAINT pk_timesheet_entry PRIMARY KEY (timesheet_entry_id),

    CONSTRAINT fk_timesheet_project FOREIGN KEY (project_id) REFERENCES project(project_id),
    CONSTRAINT fk_timesheet_employee FOREIGN KEY (employee_id) REFERENCES employee(employee_id),
    CONSTRAINT fk_timesheet_work_description FOREIGN KEY (work_description_id) REFERENCES work_description(work_description_id),

    CONSTRAINT ck_timesheet_hours_positive CHECK (hours > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE cost_item (
    cost_item_id BIGINT NOT NULL AUTO_INCREMENT,

    project_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    supplier_contact_id BIGINT NULL,

    cost_date DATE NOT NULL,
    description TEXT NOT NULL,
    cost_amount DECIMAL(10,2) NOT NULL,
    type VARCHAR(20) NOT NULL,

    CONSTRAINT pk_cost_item PRIMARY KEY (cost_item_id),

    CONSTRAINT fk_cost_item_project FOREIGN KEY (project_id) REFERENCES project(project_id),
    CONSTRAINT fk_cost_item_employee FOREIGN KEY (employee_id) REFERENCES employee(employee_id),
    CONSTRAINT fk_cost_item_supplier FOREIGN KEY (supplier_contact_id) REFERENCES contact(contact_id),

    CONSTRAINT ck_cost_item_cost_amount_nonnegative CHECK (cost_amount >= 0),
    CONSTRAINT ck_cost_item_type CHECK (type IN ('OUTLAY', 'EXPENSE')),

    -- Business rule: OUTLAY requires supplier, EXPENSE must have no supplier
    CONSTRAINT ck_cost_item_supplier_rule CHECK (
        (type = 'OUTLAY' AND supplier_contact_id IS NOT NULL) OR
        (type = 'EXPENSE' AND supplier_contact_id IS NULL)
        )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE invoice (
    invoice_id BIGINT NOT NULL AUTO_INCREMENT,

    project_id BIGINT NOT NULL,
    vat_rate_id BIGINT NOT NULL,

    invoice_date DATE NOT NULL,

    CONSTRAINT pk_invoice PRIMARY KEY (invoice_id),

    CONSTRAINT fk_invoice_project FOREIGN KEY (project_id) REFERENCES project(project_id),
    CONSTRAINT fk_invoice_vat_rate FOREIGN KEY (vat_rate_id) REFERENCES vat_rate(vat_rate_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE invoice_line_item (
    invoice_line_item_id BIGINT NOT NULL AUTO_INCREMENT,

    invoice_id BIGINT NOT NULL,

    description VARCHAR(255) NOT NULL,
    details TEXT NULL,
    quantity DECIMAL(6,2) NOT NULL,
    unit_rate DECIMAL(10,2) NOT NULL,

    CONSTRAINT pk_invoice_line_item PRIMARY KEY (invoice_line_item_id),

    CONSTRAINT fk_invoice_line_item_invoice FOREIGN KEY (invoice_id) REFERENCES invoice(invoice_id) ON DELETE CASCADE,

    CONSTRAINT ck_invoice_line_item_quantity_positive CHECK (quantity > 0),
    CONSTRAINT ck_invoice_line_item_unit_rate_positive CHECK (unit_rate > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE receipt (
    receipt_id BIGINT NOT NULL AUTO_INCREMENT,

    invoice_id BIGINT NOT NULL,

    date_received DATE NOT NULL,
    discount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    amount_paid DECIMAL(10,2) NOT NULL,

    CONSTRAINT pk_receipt PRIMARY KEY (receipt_id),

    CONSTRAINT fk_receipt_invoice FOREIGN KEY (invoice_id) REFERENCES invoice(invoice_id) ON DELETE CASCADE,

    CONSTRAINT uk_receipt_invoice UNIQUE (invoice_id),

    CONSTRAINT ck_receipt_discount_nonnegative CHECK (discount >= 0),
    CONSTRAINT ck_receipt_amount_paid_positive CHECK (amount_paid > 0),
    CONSTRAINT ck_receipt_discount_less_than_amount_paid CHECK (discount <= amount_paid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE project_report_document (
    report_document_id BIGINT NOT NULL AUTO_INCREMENT,

    project_id BIGINT NOT NULL,
    document_type_id BIGINT NOT NULL,
    uploaded_by_employee_id BIGINT NOT NULL,

    title VARCHAR(255) NOT NULL,
    date_created DATETIME NOT NULL,
    storage_location VARCHAR(500) NOT NULL,

    CONSTRAINT pk_project_report_document PRIMARY KEY (report_document_id),

    CONSTRAINT fk_project_report_document_project FOREIGN KEY (project_id) REFERENCES project(project_id),
    CONSTRAINT fk_project_report_document_type FOREIGN KEY (document_type_id) REFERENCES document_type(document_type_id),
    CONSTRAINT fk_project_report_document_employee FOREIGN KEY (uploaded_by_employee_id) REFERENCES employee(employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
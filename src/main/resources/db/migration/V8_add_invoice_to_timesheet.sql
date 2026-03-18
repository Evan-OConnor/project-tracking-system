-- V2__add_invoice_to_timesheet.sql

ALTER TABLE timesheet_entry
    ADD COLUMN invoice_id BIGINT;

ALTER TABLE timesheet_entry
    ADD CONSTRAINT fk_timesheet_invoice
        FOREIGN KEY (invoice_id)
            REFERENCES invoice(invoice_id);

CREATE INDEX idx_timesheet_invoice_id
    ON timesheet_entry(invoice_id);
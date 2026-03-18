ALTER TABLE invoice
    ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT 'GENERATED';

CREATE INDEX idx_invoice_status ON invoice(status);
ALTER TABLE cost_item
    ADD COLUMN invoice_id BIGINT NULL;

ALTER TABLE cost_item
    ADD CONSTRAINT fk_cost_item_invoice
        FOREIGN KEY (invoice_id) REFERENCES invoice(invoice_id);

CREATE INDEX idx_cost_item_invoice_id ON cost_item(invoice_id);
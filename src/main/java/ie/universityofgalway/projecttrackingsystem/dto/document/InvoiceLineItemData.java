package ie.universityofgalway.projecttrackingsystem.dto.document;

import java.math.BigDecimal;

/**
 * DTO representing a single line item in an invoice document.
 * Each instance corresponds to one row in the invoice charges table.
 */
public class InvoiceLineItemData {

    private final String description;
    private final BigDecimal quantity;
    private final BigDecimal unitRate;
    private final BigDecimal lineTotal;

    // Constructor

    public InvoiceLineItemData(
            String description,
            BigDecimal quantity,
            BigDecimal unitRate,
            BigDecimal lineTotal) {

        this.description = description;
        this.quantity = quantity;
        this.unitRate = unitRate;
        this.lineTotal = lineTotal;
    }

    // Getters


    public String getDescription() {
        return description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitRate() {
        return unitRate;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }
}
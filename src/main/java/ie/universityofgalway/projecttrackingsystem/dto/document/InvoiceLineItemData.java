package ie.universityofgalway.projecttrackingsystem.dto.document;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO representing a single line item in an invoice document.
 * Each instance corresponds to one row in the invoice charges table.
 */
public class InvoiceLineItemData {

    private final LocalDate date;
    private final String type;
    private final String description;
    private final BigDecimal quantity;
    private final BigDecimal unitRate;
    private final BigDecimal lineTotal;

    // Constructor

    public InvoiceLineItemData(
            LocalDate date,
            String type,
            String description,
            BigDecimal quantity,
            BigDecimal unitRate,
            BigDecimal lineTotal) {

        this.date = date;
        this.type = type;
        this.description = description;
        this.quantity = quantity;
        this.unitRate = unitRate;
        this.lineTotal = lineTotal;
    }

    // Getters

    public LocalDate getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

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
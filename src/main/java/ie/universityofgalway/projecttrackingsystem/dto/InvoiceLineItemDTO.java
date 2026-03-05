package ie.universityofgalway.projecttrackingsystem.dto;

import java.math.BigDecimal;

/**
 * DTO representing an invoice line item with VAT breakdown.
 */
public class InvoiceLineItemDTO {

    private final String description;
    private final BigDecimal quantity;
    private final BigDecimal unitRate;

    private final BigDecimal netAmount;       // Quantity × Unit Rate
    private final BigDecimal vatRatePercent;  // VAT percentage (e.g. 23.00)
    private final BigDecimal vatAmount;       // VAT amount for this line
    private final BigDecimal grossAmount;     // Net + VAT

    public InvoiceLineItemDTO(String description,
                              BigDecimal quantity,
                              BigDecimal unitRate,
                              BigDecimal netAmount,
                              BigDecimal vatRatePercent,
                              BigDecimal vatAmount,
                              BigDecimal grossAmount) {

        this.description = description;
        this.quantity = quantity;
        this.unitRate = unitRate;
        this.netAmount = netAmount;
        this.vatRatePercent = vatRatePercent;
        this.vatAmount = vatAmount;
        this.grossAmount = grossAmount;
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

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public BigDecimal getVatRatePercent() {
        return vatRatePercent;
    }

    public BigDecimal getVatAmount() {
        return vatAmount;
    }

    public BigDecimal getGrossAmount() {
        return grossAmount;
    }
}
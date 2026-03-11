package ie.universityofgalway.projecttrackingsystem.dto;

import java.math.BigDecimal;

public class InvoiceLineItemDTO {

    private final Long id;

    private final String description;
    private final BigDecimal quantity;
    private final BigDecimal unitRate;

    private final BigDecimal netAmount;
    private final BigDecimal discountPercent;
    private final BigDecimal discountAmount;

    private final BigDecimal vatRatePercent;
    private final BigDecimal vatAmount;
    private final BigDecimal grossAmount;

    public InvoiceLineItemDTO(Long id,
                              String description,
                              BigDecimal quantity,
                              BigDecimal unitRate,
                              BigDecimal netAmount,
                              BigDecimal discountPercent,
                              BigDecimal discountAmount,
                              BigDecimal vatRatePercent,
                              BigDecimal vatAmount,
                              BigDecimal grossAmount) {

        this.id = id;   // ← THIS FIXES THE ERROR

        this.description = description;
        this.quantity = quantity;
        this.unitRate = unitRate;
        this.netAmount = netAmount;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
        this.vatRatePercent = vatRatePercent;
        this.vatAmount = vatAmount;
        this.grossAmount = grossAmount;
    }

    public Long getId() {
        return id;
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

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
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
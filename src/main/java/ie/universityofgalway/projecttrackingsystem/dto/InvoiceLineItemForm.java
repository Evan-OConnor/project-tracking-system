package ie.universityofgalway.projecttrackingsystem.dto;

import java.math.BigDecimal;

public class InvoiceLineItemForm {

    private String description;
    private Long id;
    private String details;
    private BigDecimal quantity;
    private BigDecimal unitRate;
    private BigDecimal total; // optional pre-calculated total

    public InvoiceLineItemForm() {}

    // Getters & Setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getUnitRate() { return unitRate; }
    public void setUnitRate(BigDecimal unitRate) { this.unitRate = unitRate; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
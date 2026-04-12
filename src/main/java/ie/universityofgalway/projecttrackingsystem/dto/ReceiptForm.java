package ie.universityofgalway.projecttrackingsystem.dto;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReceiptForm {

    private Long id;

    @NotNull(message = "Invoice must be selected")
    private Long invoiceId;

    private String invoiceNumber;

    private String receiptNumber;

    @NotNull(message = "Date received is required")
    @PastOrPresent(message = "Date cannot be in the future")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateReceived;

    @NotNull(message = "Discount is required")
    @PositiveOrZero(message = "Discount cannot be negative")
    private BigDecimal discount;

    @NotNull(message = "Amount paid is required")
    @Positive(message = "Amount paid must be greater than zero")
    private BigDecimal amountPaid;

    @NotBlank(message = "Payment method is required")
    @Size(max = 50, message = "Payment method must be less than 50 characters")
    private String paymentMethod;

    // Getters
    public Long getId() {
        return id;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public String getInvoiceNumber(){ return invoiceNumber;}

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public LocalDate getDateReceived() {
        return dateReceived;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    // Setters

    public void setId(Long id) {
        this.id = id;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public void setInvoiceNumber(String invoiceNumber){this.invoiceNumber = invoiceNumber;}

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public void setDateReceived(LocalDate dateReceived) {
        this.dateReceived = dateReceived;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
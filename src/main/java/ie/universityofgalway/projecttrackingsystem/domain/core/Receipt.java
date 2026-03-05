package ie.universityofgalway.projecttrackingsystem.domain.core;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "receipt",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_receipt_invoice",
                columnNames = "invoice_id"
        )
)
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_id")
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(name = "receipt_number", nullable = false, unique = true)
    private String receiptNumber;

    @Column(name = "date_received", nullable = false)
    private LocalDate dateReceived;

    @PositiveOrZero
    @Column(name = "discount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discount;

    @Positive
    @Column(name = "amount_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;

    // =========================
    // Constructors
    // =========================

    protected Receipt() {
    }

    public Receipt(Invoice invoice,
                   String receiptNumber,
                   LocalDate dateReceived,
                   BigDecimal discount,
                   BigDecimal amountPaid,
                   String paymentMethod) {

        this.invoice = invoice;
        this.receiptNumber = receiptNumber;
        this.dateReceived = dateReceived;
        this.discount = discount;
        this.amountPaid = amountPaid;
        this.paymentMethod = paymentMethod;
    }

    // =========================
    // Getters
    // =========================

    public Long getId() {
        return id;
    }

    public Invoice getInvoice() {
        return invoice;
    }

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

    // =========================
    // Setters
    // =========================

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

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
package ie.universityofgalway.projecttrackingsystem.dto;

import java.math.BigDecimal;

public class InvoiceSearchDTO {
    private Long id;
    private String invoiceNumber;
    private String projectTitle;
    private BigDecimal amount;

    // Constructor
    public InvoiceSearchDTO(Long id, String invoiceNumber, String projectTitle, BigDecimal amount) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.projectTitle = projectTitle;
        this.amount = amount;
    }

    // Getters
    public Long getId() {return id;}

    public String getInvoiceNumber() {return invoiceNumber;}

    public String getProjectTitle() {return projectTitle;}

    public BigDecimal getAmount() {return amount;}

    // Setters
    public void setId(Long id) {this.id = id;}

    public void setInvoiceNumber(String invoiceNumber) {this.invoiceNumber = invoiceNumber;}

    public void setProjectTitle(String projectTitle) {this.projectTitle = projectTitle;}

    public void setAmount(BigDecimal amount) {this.amount = amount;}
}

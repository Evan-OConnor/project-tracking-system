package ie.universityofgalway.projecttrackingsystem.dto;

import java.math.BigDecimal;

public class ContactSummaryDTO {
    private Long contactId;
    private String contactName;
    private int projectCount;
    private BigDecimal totalInvoices;
    private BigDecimal totalReceipts;

    // Getters and Setters
    public Long getContactId() { return contactId; }
    public void setContactId(Long contactId) { this.contactId = contactId; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public int getProjectCount() { return projectCount; }
    public void setProjectCount(int projectCount) { this.projectCount = projectCount; }

    public BigDecimal getTotalInvoices() { return totalInvoices; }
    public void setTotalInvoices(BigDecimal totalInvoices) { this.totalInvoices = totalInvoices; }

    public BigDecimal getTotalReceipts() { return totalReceipts; }
    public void setTotalReceipts(BigDecimal totalReceipts) { this.totalReceipts = totalReceipts; }
}

package ie.universityofgalway.projecttrackingsystem.dto;

public class ClientSummaryDto {

    private String clientName;
    private long totalProjects;
    private long totalInvoices;
    private long totalReceipts;

    public ClientSummaryDto(String clientName, long totalProjects, long totalInvoices, long totalReceipts) {
        this.clientName = clientName;
        this.totalProjects = totalProjects;
        this.totalInvoices = totalInvoices;
        this.totalReceipts = totalReceipts;
    }

    public String getClientName() { return clientName; }
    public long getTotalProjects() { return totalProjects; }
    public long getTotalInvoices() { return totalInvoices; }
    public long getTotalReceipts() { return totalReceipts; }
}
package ie.universityofgalway.projecttrackingsystem.dto.report;

import java.math.BigDecimal;

public class RevenueSummary {

    private BigDecimal totalNetInvoiced;
    private BigDecimal totalVat;
    private BigDecimal totalGrossInvoiced;

    private BigDecimal totalReceived;
    private BigDecimal totalOutstanding;

    private BigDecimal totalCosts;

    private BigDecimal netRevenueEstimate;

    public RevenueSummary(
            BigDecimal totalNetInvoiced,
            BigDecimal totalVat,
            BigDecimal totalGrossInvoiced,
            BigDecimal totalReceived,
            BigDecimal totalOutstanding,
            BigDecimal totalCosts,
            BigDecimal netRevenueEstimate
    ) {
        this.totalNetInvoiced = totalNetInvoiced;
        this.totalVat = totalVat;
        this.totalGrossInvoiced = totalGrossInvoiced;
        this.totalReceived = totalReceived;
        this.totalOutstanding = totalOutstanding;
        this.totalCosts = totalCosts;
        this.netRevenueEstimate = netRevenueEstimate;
    }

    public BigDecimal getTotalNetInvoiced() { return totalNetInvoiced; }
    public BigDecimal getTotalVat() { return totalVat; }
    public BigDecimal getTotalGrossInvoiced() { return totalGrossInvoiced; }
    public BigDecimal getTotalReceived() { return totalReceived; }
    public BigDecimal getTotalOutstanding() { return totalOutstanding; }
    public BigDecimal getTotalCosts() { return totalCosts; }
    public BigDecimal getNetRevenueEstimate() { return netRevenueEstimate; }
}

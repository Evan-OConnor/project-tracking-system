package ie.universityofgalway.projecttrackingsystem.domain.lookup;

public enum InvoiceStatus {

    GENERATED,       // Invoice created but not paid
    PARTIALLY_PAID,  // Some payment received
    PAID,            // Fully paid
    VOID             // Cancelled / invalid

}
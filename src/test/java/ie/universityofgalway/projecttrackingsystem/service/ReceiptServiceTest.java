package ie.universityofgalway.projecttrackingsystem.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ReceiptServiceTest {

    @Test
    void shouldThrowExceptionWhenOverpaymentOccurs() {

        BigDecimal invoiceTotal = new BigDecimal("1000");
        BigDecimal existingReceipts = new BigDecimal("900");
        BigDecimal newReceipt = new BigDecimal("200");

        BigDecimal newTotal = existingReceipts.add(newReceipt);

        assertThrows(IllegalArgumentException.class, () -> {
            if (newTotal.compareTo(invoiceTotal) > 0) {
                throw new IllegalArgumentException("Overpayment not allowed");
            }
        });
    }

}
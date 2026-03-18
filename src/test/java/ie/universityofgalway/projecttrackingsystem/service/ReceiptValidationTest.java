package ie.universityofgalway.projecttrackingsystem.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ReceiptValidationTest {

    @Test
    void shouldPreventOverpayment() {

        BigDecimal invoiceTotal = new BigDecimal("1000");
        BigDecimal existingReceipts = new BigDecimal("900");
        BigDecimal newPayment = new BigDecimal("200");

        BigDecimal totalReceipts = existingReceipts.add(newPayment);

        boolean isOverpayment = totalReceipts.compareTo(invoiceTotal) > 0;

        assertTrue(isOverpayment);
    }

}
package ie.universityofgalway.projecttrackingsystem.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OutstandingBalanceTest {

    @Test
    void shouldCalculateOutstandingBalanceCorrectly() {

        BigDecimal invoiceTotal = new BigDecimal("1000");
        BigDecimal receipts = new BigDecimal("300");

        BigDecimal outstanding = invoiceTotal.subtract(receipts);

        assertEquals(new BigDecimal("700"), outstanding);
    }

}
package ie.universityofgalway.projecttrackingsystem.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceFinancialLogicTest {

    @Test
    void shouldCalculateInvoiceLineTotalCorrectly() {

        BigDecimal quantity = new BigDecimal("2");
        BigDecimal unitPrice = new BigDecimal("100");

        BigDecimal lineTotal = quantity.multiply(unitPrice);

        assertEquals(new BigDecimal("200"), lineTotal);
    }

    @Test
    void shouldCalculateVatCorrectly() {

        BigDecimal lineTotal = new BigDecimal("200");
        BigDecimal vatRate = new BigDecimal("0.23");

        BigDecimal vat = lineTotal.multiply(vatRate);

        assertEquals(new BigDecimal("46.00"), vat.setScale(2));
    }

    @Test
    void shouldCalculateInvoiceTotalFromMultipleLines() {

        BigDecimal item1 = new BigDecimal("200");
        BigDecimal item2 = new BigDecimal("100");

        BigDecimal invoiceTotal = item1.add(item2);

        assertEquals(new BigDecimal("300"), invoiceTotal);
    }

}
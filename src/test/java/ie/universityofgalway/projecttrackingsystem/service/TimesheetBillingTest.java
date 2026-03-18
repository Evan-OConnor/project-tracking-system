package ie.universityofgalway.projecttrackingsystem.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TimesheetBillingTest {

    @Test
    void shouldCalculateTimesheetBillingCorrectly() {

        BigDecimal hoursWorked = new BigDecimal("5");
        BigDecimal hourlyRate = new BigDecimal("100");

        BigDecimal total = hoursWorked.multiply(hourlyRate);

        assertEquals(new BigDecimal("500"), total);
    }

}
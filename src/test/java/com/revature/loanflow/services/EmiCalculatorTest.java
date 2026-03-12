package com.revature.loanflow.services;

import com.revature.loanflow.services.EmiCalculator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EmiCalculator Tests")
class EmiCalculatorTest {

    @Test
    @DisplayName("Standard loan: Rs.1L at 12.5% for 36 months = Rs.3338.74")
    void testStandardLoan() {
        double emi = EmiCalculator.calculateEmi(100_000, 12.5, 36);
        assertEquals(3338.74, emi, 0.01);
        //             ^expected  ^actual  ^delta (tolerance)
    }

    @Test
    @DisplayName("Shorter tenure produces higher EMI")
    void testShortTenureHigherEmi() {
        double emi6  = EmiCalculator.calculateEmi(100_000, 12.5, 6);
        double emi36 = EmiCalculator.calculateEmi(100_000, 12.5, 36);
        assertTrue(emi6 > emi36);
    }

    @Test
    @DisplayName("Total payment exceeds principal")
    void testTotalPaymentExceedsPrincipal() {
        double emi   = EmiCalculator.calculateEmi(100_000, 12.5, 36);
        double total = EmiCalculator.calculateTotalPayment(emi, 36);
        assertTrue(total > 100_000);
    }

    // ── Parameterized: run same test with multiple inputs ──
    @ParameterizedTest(name = "Principal={0}, Rate={1}%, Tenure={2}m")
    @CsvSource({
            "100000, 12.5, 36, 3000",
            "200000, 14.0, 48, 5000",
            "50000,  10.0, 24, 2000",
    })
    void testEmiAlwaysAboveMinimum(double p, double r, int t, double min) {
        assertTrue(EmiCalculator.calculateEmi(p, r, t) > min);
    }

    // ── Exception tests ────────────────────────────────
    @Test
    @DisplayName("Zero principal throws IllegalArgumentException")
    void testZeroPrincipal() {
        assertThrows(IllegalArgumentException.class,
                () -> EmiCalculator.calculateEmi(0, 12.5, 36));
    }

    @Test
    @DisplayName("Zero tenure throws IllegalArgumentException")
    void testZeroTenure() {
        assertThrows(IllegalArgumentException.class,
                () -> EmiCalculator.calculateEmi(100_000, 12.5, 0));
    }
}
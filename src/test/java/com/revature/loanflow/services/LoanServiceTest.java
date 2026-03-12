package com.revature.loanflow.services;

import com.revature.loanflow.model.LoanApplication;
import com.revature.loanflow.model.LoanStatus;
import com.revature.loanflow.services.LoanService;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoanService — Business Rules Tests")
class LoanServiceTest {

    private LoanService loanService;

    @BeforeEach
    void setUp() {
        loanService = new LoanService();  // No Spring needed — plain Java
    }

    // Helper: creates an application that passes ALL rules
    private LoanApplication eligible() {
        return new LoanApplication("Ravi Kumar", "9876543210",
                720, 45_000.0, 85_000.0, 36);
    }

    // ── Credit score rule ──────────────────────────────
    @Test
    @DisplayName("Score 650 (exact minimum) -> APPROVED")
    void testExactMinimumScore() {
        LoanApplication app = eligible();
        app.setCreditScore(650);
        assertTrue(loanService.isEligible(app));
    }

    @Test
    @DisplayName("Score 649 (one below minimum) -> REJECTED")
    void testJustBelowMinimumScore() {
        LoanApplication app = eligible();
        app.setCreditScore(649);
        assertFalse(loanService.isEligible(app));
    }

    // ── Income rule ────────────────────────────────────
    @Test
    @DisplayName("Income below Rs.10,000 -> REJECTED")
    void testLowIncome() {
        LoanApplication app = eligible();
        app.setMonthlyIncome(9_999.0);
        assertFalse(loanService.isEligible(app));
    }

    // ── EMI affordability rule ─────────────────────────
    @Test
    @DisplayName("Large loan on small income (EMI > 40%) -> REJECTED")
    void testEmiExceedsFoir() {
        LoanApplication app = eligible();
        app.setMonthlyIncome(15_000.0);
        app.setLoanAmount(300_000.0);  // EMI will be ~67% of income
        assertFalse(loanService.isEligible(app));
    }

    // ── processApplication ─────────────────────────────
    @Test
    @DisplayName("Eligible -> status APPROVED")
    void testApproved() {
        assertEquals(LoanStatus.APPROVED,
                loanService.processApplication(eligible()).getLoanStatus());
    }

    @Test
    @DisplayName("Low score -> status REJECTED")
    void testRejected() {
        LoanApplication app = eligible();
        app.setCreditScore(400);
        assertEquals(LoanStatus.REJECTED,
                loanService.processApplication(app).getLoanStatus());
    }

    // ── Rejection reason ───────────────────────────────
    @Test
    @DisplayName("Eligible -> rejection reason is null")
    void testNoRejectionReasonForEligible() {
        assertNull(loanService.getRejectionReason(eligible()));
    }

    @Test
    @DisplayName("Low score -> reason mentions credit score")
    void testRejectionReasonMentionsScore() {
        LoanApplication app = eligible();
        app.setCreditScore(580);
        String reason = loanService.getRejectionReason(app);
        assertNotNull(reason);
        assertTrue(reason.contains("580"));
    }
}
 
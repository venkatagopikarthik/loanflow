package com.revature.loanflow.services;

import com.revature.loanflow.model.LoanApplication;
import com.revature.loanflow.model.LoanStatus;
import org.springframework.stereotype.Service;

@Service
public class LoanService {
 
    // Business rule constants
    private static final int    MIN_CREDIT_SCORE     = 650;
    private static final double MAX_EMI_INCOME_RATIO = 0.40;  // 40% FOIR
    private static final double MIN_MONTHLY_INCOME   = 10_000.0;
    private static final double MAX_LOAN_ANNUAL_MULT = 3.0;   // max 3x annual income
    private static final double DEFAULT_RATE         = 12.5;  // 12.5% per year
 
    // Main entry point: checks all 4 rules
    public boolean isEligible(LoanApplication app) {
        return hasSufficientCreditScore(app.getCreditScore())
            && hasSufficientIncome(app.getMonthlyIncome())
            && isEmiAffordable(app.getMonthlyIncome(),
                               app.getLoanAmount(),
                               app.getTenureInMonths())
            && isLoanAmountReasonable(app.getMonthlyIncome(),
                                      app.getLoanAmount());
    }
 
    // Sets status and returns the same object
    public LoanApplication processApplication(LoanApplication app) {
        app.setLoanStatus(isEligible(app) ? LoanStatus.APPROVED : LoanStatus.REJECTED);
        return app;
    }
 
    // Returns human-readable reason for rejection, or null if eligible
    public String getRejectionReason(LoanApplication app) {
        if (!hasSufficientCreditScore(app.getCreditScore()))
            return "Credit score " + app.getCreditScore() + " below minimum 650";
        if (!hasSufficientIncome(app.getMonthlyIncome()))
            return "Monthly income below minimum Rs.10,000";
        if (!isLoanAmountReasonable(app.getMonthlyIncome(), app.getLoanAmount()))
            return "Loan exceeds 3x annual income limit";
        if (!isEmiAffordable(app.getMonthlyIncome(), app.getLoanAmount(),
                              app.getTenureInMonths()))
            return "EMI exceeds 40% of monthly income (FOIR limit)";
        return null;
    }
 
    // ── Private rule methods ──────────────────────────
    private boolean hasSufficientCreditScore(int score) {
        return score >= MIN_CREDIT_SCORE;
    }
    private boolean hasSufficientIncome(double income) {
        return income >= MIN_MONTHLY_INCOME;
    }
    private boolean isEmiAffordable(double income, double loan, int tenure) {
        double emi = EmiCalculator.calculateEmi(loan, DEFAULT_RATE, tenure);
        return (emi / income) <= MAX_EMI_INCOME_RATIO;
    }
    private boolean isLoanAmountReasonable(double income, double loan) {
        return loan <= (income * 12 * MAX_LOAN_ANNUAL_MULT);
    }
}
 
package com.revature.loanflow.services;

public class EmiCalculator {
    private EmiCalculator(){
        throw new UnsupportedOperationException("Utility Class");
    }

    /**
     * EMI formula is P X r X (1+r)^n / ((1+r)^n-1),
     * where P -> principal, r -> mothnly rate, n -> tenure in months
     * @param principal
     * @param annualRate
     * @param tenureInMonths
     * @return Math.round(emi * 100.0) / 100.0
     */
    public static double calculateEmi(double principal, double annualRate,
                                     int tenureInMonths){
        if (principal <= 0) throw
                new IllegalArgumentException("Principal must be positive");

        if (annualRate <= 0) throw
                new IllegalArgumentException("Annual rate must be positive");

        if (tenureInMonths <= 0) throw
                new IllegalArgumentException("Tenure must be positive");

        //Convert annual percent to monthly decimal
        // e.g., 12.5% per year = 12.5 / 12/ 100 = 0.010417 per month
        double monthlyRate = annualRate /12.0 /100.0;
        double power = Math.pow(1 + monthlyRate, tenureInMonths);
        double emi = principal * monthlyRate * power / (power - 1);
        return Math.round(emi * tenureInMonths * 100.0) / 100.0;

    }
    public static double calculateTotalPayment(double emi, int tenureMonths) {
        return Math.round(emi * tenureMonths * 100.0) / 100.0;
    }

    public static double calculateTotalInterest(double principal,
                                                double emi,
                                                int tenureMonths) {
        return Math.round((calculateTotalPayment(emi, tenureMonths) - principal) * 100.0) / 100.0;
    }

}

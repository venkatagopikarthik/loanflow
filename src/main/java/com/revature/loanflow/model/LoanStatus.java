package com.revature.loanflow.model;

public enum LoanStatus {
    PENDING,  //application submitted, not yet evaluated
    APPROVED, //All eligibility rules passed
    REJECTED, //one or more rules fails
    CANCELLED //customer withdraws the application
}

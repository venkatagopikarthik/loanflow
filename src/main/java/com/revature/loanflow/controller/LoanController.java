package com.revature.loanflow.controller;

import com.revature.loanflow.model.LoanApplication;
import com.revature.loanflow.model.LoanStatus;
import com.revature.loanflow.services.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;   // Constructor injection
    }

    // POST /api/loans/apply
    @PostMapping("/apply")
    public ResponseEntity<Map<String, Object>> applyForLoan(
            @Valid @RequestBody LoanApplication application) {

        LoanApplication processed = loanService.processApplication(application);
        Map<String, Object> response = new HashMap<>();
        response.put("status", processed.getLoanStatus());
        response.put("application", processed);

        if (processed.getLoanStatus() == LoanStatus.APPROVED) {
            response.put("message", "Loan approved!");
        } else {
            response.put("message", loanService.getRejectionReason(application));
        }
        return ResponseEntity.ok(response);
    }

    // GET /api/loans/health — used by monitoring and CI smoke tests
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> r = new HashMap<>();
        r.put("status", "UP");
        r.put("service", "LoanFlow API");
        return ResponseEntity.ok(r);
    }
}

 
package com.revature.loanflow.controller;

import com.revature.loanflow.model.LoanApplication;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("LoanController REST API Tests")
class LoanControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired  ObjectMapper objectMapper;

    private LoanApplication valid() {
        return new LoanApplication("Priya Sharma", "8765432109",
                720, 45_000.0, 85_000.0, 36);
    }

    @Test
    @DisplayName("GET /health -> 200 UP")
    void testHealth() throws Exception {
        mockMvc.perform(get("/api/loans/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("Eligible customer -> APPROVED in response")
    void testApproved() throws Exception {
        mockMvc.perform(post("/api/loans/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(valid())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("Low credit score -> REJECTED")
    void testRejected() throws Exception {
        LoanApplication app = valid();
        app.setCreditScore(580);
        mockMvc.perform(post("/api/loans/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(app)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    @DisplayName("Missing customer name -> 400 Bad Request")
    void testMissingName() throws Exception {
        LoanApplication app = valid();
        app.setCustomerName(null);
        mockMvc.perform(post("/api/loans/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(app)))
                .andExpect(status().isBadRequest());
    }
}

 
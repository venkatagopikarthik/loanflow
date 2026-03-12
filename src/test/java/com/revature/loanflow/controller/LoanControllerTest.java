package com.revature.loanflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.loanflow.model.LoanApplication;
import com.revature.loanflow.services.LoanService;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("LoanController REST API Tests")
class LoanControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        LoanService loanService = new LoanService();
        LoanController controller = new LoanController(loanService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

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
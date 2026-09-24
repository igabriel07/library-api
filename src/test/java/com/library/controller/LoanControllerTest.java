package com.library.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import org.springframework.http.MediaType;

import com.library.exception.ResourceNotFoundException;
import com.library.dto.LoanRequest;

import java.time.LocalDate;

import com.library.dto.LoanResponse;
import com.library.model.LoanStatus;

import java.util.List;

import com.library.service.LoanService;

@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoanService loanService;

    @Test
    void shouldGetAllLoans() throws Exception {
    
        when(loanService.getAllLoans())
                .thenReturn(List.of());
    
        mockMvc.perform(get("/api/loans"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldGetLoanById() throws Exception {
    
        LoanResponse response = new LoanResponse(
                1L,
                LocalDate.of(2026, 9, 19),
                LocalDate.of(2026, 9, 26),
                null,
                LoanStatus.ACTIVE,
                1L,
                1L
        );
    
        when(loanService.getLoanById(1L))
                .thenReturn(response);
    
        mockMvc.perform(get("/api/loans/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.memberId").value(1))
                .andExpect(jsonPath("$.bookId").value(1));
    }

    @Test
    void shouldCreateLoan() throws Exception {
    
        LoanResponse response = new LoanResponse(
                1L,
                LocalDate.of(2026, 9, 19),
                LocalDate.of(2026, 9, 26),
                null,
                LoanStatus.ACTIVE,
                1L,
                1L
        );
    
        when(loanService.createLoan(
                org.mockito.ArgumentMatchers.any(LoanRequest.class)))
                .thenReturn(response);
    
        mockMvc.perform(post("/api/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "loanDate": "2026-09-19",
                            "dueDate": "2026-09-26",
                            "memberId": 1,
                            "bookId": 1
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.memberId").value(1))
                .andExpect(jsonPath("$.bookId").value(1));
    }

    @Test
    void shouldReturn400WhenCreatingInvalidLoan() throws Exception {

        mockMvc.perform(post("/api/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "loanDate": null,
                            "dueDate": null,
                            "memberId": null,
                            "bookId": null
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenCreatingLoanWithNonExistingMember() throws Exception {
    
        when(loanService.createLoan(
                org.mockito.ArgumentMatchers.any(LoanRequest.class)))
                .thenThrow(new ResourceNotFoundException("Member not found"));
    
        mockMvc.perform(post("/api/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "loanDate": "2026-09-19",
                            "dueDate": "2026-09-26",
                            "memberId": 999,
                            "bookId": 1
                        }
                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Member not found"));
    }

    @Test
    void shouldReturn404WhenCreatingLoanWithNonExistingBook() throws Exception {

        when(loanService.createLoan(
                org.mockito.ArgumentMatchers.any(LoanRequest.class)))
                .thenThrow(new ResourceNotFoundException("Book not found"));

        mockMvc.perform(post("/api/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "loanDate": "2026-09-19",
                            "dueDate": "2026-09-26",
                            "memberId": 1,
                            "bookId": 999
                        }
                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found"));
    }

    @Test
    void shouldReturn400WhenCreatingLoanWithNoAvailableCopies() throws Exception {

        when(loanService.createLoan(
                org.mockito.ArgumentMatchers.any(LoanRequest.class)))
                .thenThrow(new IllegalArgumentException(
                        "No available copies"));

        mockMvc.perform(post("/api/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "loanDate": "2026-09-19",
                            "dueDate": "2026-09-26",
                            "memberId": 1,
                            "bookId": 1
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No available copies"));
    }

    @Test
    void shouldReturnLoan() throws Exception {

        LoanResponse response = new LoanResponse(
                1L,
                LocalDate.of(2026, 9, 19),
                LocalDate.of(2026, 9, 26),
                LocalDate.of(2026, 9, 24),
                LoanStatus.RETURNED,
                1L,
                1L);

        when(loanService.returnLoan(1L))
                .thenReturn(response);

        mockMvc.perform(put("/api/loans/1/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("RETURNED"))
                .andExpect(jsonPath("$.memberId").value(1))
                .andExpect(jsonPath("$.bookId").value(1));
    }

    @Test
    void shouldReturn404WhenReturningLoanThatDoesNotExist() throws Exception {

        when(loanService.returnLoan(999L))
                .thenThrow(new ResourceNotFoundException("Loan not found"));

        mockMvc.perform(put("/api/loans/999/return"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Loan not found"));
    }

    @Test
    void shouldReturn400WhenReturningAlreadyReturnedLoan() throws Exception {

        when(loanService.returnLoan(1L))
                .thenThrow(new IllegalArgumentException(
                        "Loan has already been returned"));

        mockMvc.perform(put("/api/loans/1/return"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Loan has already been returned"));
    }

    @Test
    void shouldReturn404WhenLoanDoesNotExist() throws Exception {

        when(loanService.getLoanById(999L))
                .thenThrow(new ResourceNotFoundException("Loan not found"));

        mockMvc.perform(get("/api/loans/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Loan not found"));
    }

    @Test
    void shouldReturn400WhenDueDateIsBeforeLoanDate() throws Exception {

        when(loanService.createLoan(
                org.mockito.ArgumentMatchers.any(LoanRequest.class)))
                .thenThrow(new IllegalArgumentException(
                        "Due date cannot be before loan date"));

        mockMvc.perform(post("/api/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "loanDate": "2026-09-26",
                            "dueDate": "2026-09-19",
                            "memberId": 1,
                            "bookId": 1
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Due date cannot be before loan date"));
    }
}
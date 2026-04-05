package com.ngo_finance.ngo_finance.controller;

import com.ngo_finance.ngo_finance.dto.DonationRequest;
import com.ngo_finance.ngo_finance.dto.ExpenseRequest;
import com.ngo_finance.ngo_finance.entity.User;
import com.ngo_finance.ngo_finance.service.DonationService;
import com.ngo_finance.ngo_finance.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final DonationService donationService;
    private final ExpenseService expenseService;

    @PostMapping("/donations")
    public ResponseEntity<String> addDonation(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody DonationRequest request
    ) {
        donationService.addDonation(user, request);
        return ResponseEntity.ok("Donation has been securely recorded!");
    }

    @DeleteMapping("/donations/{id}")
    public ResponseEntity<String> deleteDonation(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        donationService.deleteDonation(user, id);
        return ResponseEntity.ok("Donation sequence has been permanently wiped from the analytical ledger.");
    }

    @PostMapping("/expenses")
    public ResponseEntity<String> addExpense(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ExpenseRequest request
    ) {
        expenseService.addExpense(user, request);
        return ResponseEntity.ok("Expense has been successfully validated and recorded!");
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<String> deleteExpense(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        expenseService.deleteExpense(user, id);
        return ResponseEntity.ok("Expense block has been permanently wiped from the internal ledger.");
    }
}

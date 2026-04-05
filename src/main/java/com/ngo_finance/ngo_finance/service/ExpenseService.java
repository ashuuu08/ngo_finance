package com.ngo_finance.ngo_finance.service;

import com.ngo_finance.ngo_finance.dto.ExpenseRequest;
import com.ngo_finance.ngo_finance.entity.Expense;
import com.ngo_finance.ngo_finance.entity.Role;
import com.ngo_finance.ngo_finance.entity.User;
import com.ngo_finance.ngo_finance.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public void addExpense(User user, ExpenseRequest req) {
        // Enforcing robust administrative checks
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.CO_ADMIN) {
            throw new AccessDeniedException("Only Administrators or Co-Admins are permitted to establish financial expense records.");
        }

        Expense expense = Expense.builder()
                .transactionId("EXP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .category(req.getCategory())
                .amount(req.getAmount())
                .description(req.getDescription())
                .updatedBy(user.getEmail())
                .build();
        
        expenseRepository.save(expense);
    }

    public void deleteExpense(User user, Long expenseId) {
        // Ultimate override destruction capability strictly bounded to absolute root Admin
        if (user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Restricted. Only a root level ADMIN holds permission to physically purge transaction matrices.");
        }
        
        expenseRepository.findById(expenseId).orElseThrow(() -> new IllegalArgumentException("The specified expense block targeting destruction was not located."));
        expenseRepository.deleteById(expenseId);
    }
}

package com.ngo_finance.ngo_finance.service;

import com.ngo_finance.ngo_finance.dto.CategoryExpenseDTO;
import com.ngo_finance.ngo_finance.dto.DashboardResponse;
import com.ngo_finance.ngo_finance.dto.DonationDTO;
import com.ngo_finance.ngo_finance.entity.Role;
import com.ngo_finance.ngo_finance.entity.User;
import com.ngo_finance.ngo_finance.repository.DonationRepository;
import com.ngo_finance.ngo_finance.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DonationRepository donationRepository;
    private final ExpenseRepository expenseRepository;

    public DashboardResponse getDashboardData(User user, LocalDateTime startDate, LocalDateTime endDate) {
        
        BigDecimal totalDonations;
        BigDecimal totalExpenses;
        List<Object[]> categoryData;

        // Execute Custom filter if both limits are provided
        if (startDate != null && endDate != null) {
            totalDonations = donationRepository.sumAmountByDateRange(startDate, endDate);
            totalExpenses = expenseRepository.sumAmountByDateRange(startDate, endDate);
            categoryData = expenseRepository.sumAmountByCategoryAndDateRange(startDate, endDate);
        } else {
            totalDonations = donationRepository.sumTotalAmount();
            totalExpenses = expenseRepository.sumTotalAmount();
            categoryData = expenseRepository.sumAmountByCategory();
        }

        BigDecimal remainingAmount = totalDonations.subtract(totalExpenses);

        List<CategoryExpenseDTO> categoryExpenses = new ArrayList<>();
        for (Object[] row : categoryData) {
            String category = (String) row[0];
            BigDecimal amount = (BigDecimal) row[1];
            
            CategoryExpenseDTO.CategoryExpenseDTOBuilder catBuilder = CategoryExpenseDTO.builder()
                    .category(category)
                    .totalExpense(amount);
                    
            // Securely evaluate role based rendering
            if (user.getRole() == Role.ADMIN || user.getRole() == Role.CO_ADMIN) {
                // Placeholder allocated budget parameter for administrators
                BigDecimal allocated = new BigDecimal("10000.00");
                catBuilder.budgetAllocated(allocated);
                catBuilder.remainingBudget(allocated.subtract(amount));
            }
            categoryExpenses.add(catBuilder.build());
        }

        DashboardResponse.DashboardResponseBuilder responseBuilder = DashboardResponse.builder()
                .totalDonation(totalDonations)
                .totalExpense(totalExpenses)
                .remainingAmount(remainingAmount)
                .categoryExpenses(categoryExpenses);

        // Conditional rendering for Admin logs vs User logs
        if (user.getRole() == Role.ADMIN || user.getRole() == Role.CO_ADMIN) {
            List<DonationDTO> recentDonations = donationRepository.findTop10ByOrderByDateAndTimeDesc()
                    .stream()
                    .map(d -> DonationDTO.builder()
                            .id(d.getId())
                            .transactionId(d.getTransactionId())
                            .donorName(d.getUName())
                            .amount(d.getAmount())
                            .date(d.getDateAndTime())
                            .build())
                    .collect(Collectors.toList());
            responseBuilder.recentDonations(recentDonations);
        } else {
            List<DonationDTO> myLogs = donationRepository.findByUserIdOrderByDateAndTimeDesc(user.getId())
                    .stream()
                    .map(d -> DonationDTO.builder()
                            .id(d.getId())
                            .transactionId(d.getTransactionId())
                            .donorName(d.getUName())
                            .amount(d.getAmount())
                            .date(d.getDateAndTime())
                            .build())
                    .collect(Collectors.toList());
            responseBuilder.myDonationLogs(myLogs);
        }

        return responseBuilder.build();
    }
}

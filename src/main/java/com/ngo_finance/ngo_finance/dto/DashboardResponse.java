package com.ngo_finance.ngo_finance.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardResponse {
    private BigDecimal totalDonation;
    private BigDecimal totalExpense;
    private BigDecimal remainingAmount;

    // Fields returned specifically for ADMIN / CO_ADMIN
    private List<DonationDTO> recentDonations;
    
    // Field returned specifically for USER standard role
    private List<DonationDTO> myDonationLogs;

    // Returned for all roles
    private List<CategoryExpenseDTO> categoryExpenses;
}

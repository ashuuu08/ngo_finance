package com.ngo_finance.ngo_finance.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryExpenseDTO {
    private String category;
    private BigDecimal totalExpense;
    
    // Values dynamically hidden based on Role
    private BigDecimal budgetAllocated;
    private BigDecimal remainingBudget;
}

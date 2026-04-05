package com.ngo_finance.ngo_finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseRequest {
    @NotBlank(message = "Category is legally required to identify the expense")
    private String category;

    @NotNull(message = "Amount stringency must be evaluated")
    private BigDecimal amount;

    @NotBlank(message = "Detailed descriptive intent is required")
    private String description;
}

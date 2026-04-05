package com.ngo_finance.ngo_finance.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ExpenseDTO {
    private Long id;
    private String transactionId;
    private String category;
    private BigDecimal amount;
    private String description;
    private LocalDateTime date;
}

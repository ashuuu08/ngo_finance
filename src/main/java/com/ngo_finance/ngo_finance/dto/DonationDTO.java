package com.ngo_finance.ngo_finance.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DonationDTO {
    private Long id;
    private String transactionId;
    private String donorName;
    private BigDecimal amount;
    private LocalDateTime date;
}

package com.ngo_finance.ngo_finance.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DonorDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    
    // Aggregated telemetry fields
    private Long totalDonations;
    private BigDecimal totalAmountDonated;
}

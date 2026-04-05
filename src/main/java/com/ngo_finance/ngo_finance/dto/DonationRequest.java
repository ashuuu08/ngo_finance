package com.ngo_finance.ngo_finance.dto;

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
public class DonationRequest {
    @NotNull(message = "Numeric explicit cash parameter amounts are strictly required")
    private BigDecimal amount;

    // Fallback unlinked string parameter representing manually captured cash from offline drops
    private String offlineDonorName;
    
    // Exact identifier allowing an administrative user to allocate a block to a known valid repository User entity
    private Long targetUserId;
}

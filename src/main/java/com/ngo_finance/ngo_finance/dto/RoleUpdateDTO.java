package com.ngo_finance.ngo_finance.dto;

import com.ngo_finance.ngo_finance.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleUpdateDTO {
    
    @NotNull(message = "Role must be provided")
    private Role role;
}

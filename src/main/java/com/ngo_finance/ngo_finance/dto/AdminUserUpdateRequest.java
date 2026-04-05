package com.ngo_finance.ngo_finance.dto;

import com.ngo_finance.ngo_finance.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserUpdateRequest {
    private String fullName;
    private String phone;
    private String email;
    private String password;
    private Role role;
    private Boolean active;
}

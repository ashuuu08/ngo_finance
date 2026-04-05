package com.ngo_finance.ngo_finance.dto;

import com.ngo_finance.ngo_finance.entity.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private Role role;
    private boolean active;
}

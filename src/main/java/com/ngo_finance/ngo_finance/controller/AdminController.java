package com.ngo_finance.ngo_finance.controller;

import com.ngo_finance.ngo_finance.dto.DonationDTO;
import com.ngo_finance.ngo_finance.dto.DonorDTO;
import com.ngo_finance.ngo_finance.dto.ExpenseDTO;
import com.ngo_finance.ngo_finance.dto.AdminUserRegisterRequest;
import com.ngo_finance.ngo_finance.dto.AdminUserUpdateRequest;
import com.ngo_finance.ngo_finance.entity.User;
import com.ngo_finance.ngo_finance.dto.RoleUpdateDTO;
import com.ngo_finance.ngo_finance.dto.UserDTO;
import com.ngo_finance.ngo_finance.service.AdminService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/donors")
    public ResponseEntity<Page<DonorDTO>> getDonorsList(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(adminService.getAllDonors(user, page, size));
    }

    @GetMapping("/expenses")
    public ResponseEntity<Page<ExpenseDTO>> getAllExpenses(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(adminService.getAllExpenses(user, page, size));
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserDTO>> getUserList(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(adminService.getAllUsers(user, page, size));
    }

    @GetMapping("/donations")
    public ResponseEntity<Page<DonationDTO>> getAllDonations(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(adminService.getAllDonations(user, page, size));
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<String> updateUserRole(
            @AuthenticationPrincipal User user,
            @PathVariable Long userId,
            @Valid @RequestBody RoleUpdateDTO updateDTO
    ) {
        adminService.updateUserRole(user, userId, updateDTO.getRole());
        return ResponseEntity.ok("User role successfully updated to " + updateDTO.getRole().name());
    }

    @PostMapping("/users")
    public ResponseEntity<String> adminRegisterUser(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AdminUserRegisterRequest req
    ) {
        adminService.adminRegisterUser(user, req);
        return ResponseEntity.ok("User registered successfully by Admin/Co-Admin.");
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<String> updateUserDetails(
            @AuthenticationPrincipal User user,
            @PathVariable Long userId,
            @RequestBody AdminUserUpdateRequest req
    ) {
        adminService.updateUserCredentials(user, userId, req);
        return ResponseEntity.ok("User credentials securely updated by Master Admin.");
    }
}

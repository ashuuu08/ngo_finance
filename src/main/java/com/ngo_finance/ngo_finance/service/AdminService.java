package com.ngo_finance.ngo_finance.service;

import com.ngo_finance.ngo_finance.dto.DonationDTO;
import com.ngo_finance.ngo_finance.dto.DonorDTO;
import com.ngo_finance.ngo_finance.dto.ExpenseDTO;
import com.ngo_finance.ngo_finance.entity.Role;
import com.ngo_finance.ngo_finance.entity.User;
import com.ngo_finance.ngo_finance.repository.DonationRepository;
import com.ngo_finance.ngo_finance.repository.ExpenseRepository;
import com.ngo_finance.ngo_finance.repository.UserRepository;
import com.ngo_finance.ngo_finance.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ngo_finance.ngo_finance.dto.AdminUserRegisterRequest;
import com.ngo_finance.ngo_finance.dto.AdminUserUpdateRequest;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final DonationRepository donationRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private void verifyAdminAccess(User user) {
        if (user == null) {
            throw new AccessDeniedException("Access Denied: No authenticated user found in the security context.");
        }
        // Strict boundary: Only system admins or co-admins can access these modules
        if (user.getRole() == null || (user.getRole() != Role.ADMIN && user.getRole() != Role.CO_ADMIN)) {
            String roleName = (user.getRole() != null) ? user.getRole().name() : "NULL";
            throw new AccessDeniedException("Access Denied: Your current role (" + roleName + 
                ") does not have the required administrative permissions to access this secure registry.");
        }
    }

    public Page<DonorDTO> getAllDonors(User user, int page, int size) {
        verifyAdminAccess(user);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> aggregatedData = donationRepository.findDonorsAggregateData(pageable);
        
        return aggregatedData.map(row -> DonorDTO.builder()
                .id((Long) row[0])
                .fullName((String) row[1])
                .email((String) row[2])
                .phone((String) row[3])
                .totalDonations((Long) row[4])
                .totalAmountDonated((BigDecimal) row[5])
                .build());
    }

    public Page<UserDTO> getAllUsers(User user, int page, int size) {
        verifyAdminAccess(user);
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable)
                .map(u -> UserDTO.builder()
                        .id(u.getId())
                        .fullName(u.getFullName())
                        .email(u.getEmail())
                        .phone(u.getPhone())
                        .role(u.getRole())
                        .active(u.isActive())
                        .build());
    }

    public Page<ExpenseDTO> getAllExpenses(User user, int page, int size) {
        verifyAdminAccess(user);
        
        Pageable pageable = PageRequest.of(page, size);
        return expenseRepository.findAllByOrderByTimeAndDateDesc(pageable)
                .map(e -> ExpenseDTO.builder()
                        .id(e.getId())
                        .transactionId(e.getTransactionId())
                        .category(e.getCategory())
                        .amount(e.getAmount())
                        .description(e.getDescription())
                        .date(e.getTimeAndDate())
                        .build());
    }

    public Page<DonationDTO> getAllDonations(User user, int page, int size) {
        verifyAdminAccess(user);
        
        Pageable pageable = PageRequest.of(page, size);
        return donationRepository.findAllByOrderByDateAndTimeDesc(pageable)
                .map(d -> DonationDTO.builder()
                        .id(d.getId())
                        .transactionId(d.getTransactionId())
                        .donorName(d.getUName())
                        .amount(d.getAmount())
                        .date(d.getDateAndTime())
                        .build());
    }

    public void updateUserRole(User adminUser, Long userId, Role newRole) {
        if (adminUser.getRole() != Role.ADMIN) {
             throw new AccessDeniedException("Only Super Admins can modify existing roles.");
        }
        
        if (adminUser.getId().equals(userId)) {
             throw new IllegalArgumentException("Safety Restriction: You cannot modify your own administrative role. This prevents accidental self-demotion from the system.");
        }
        
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Target user not found."));
        
        // Prevent removing the last admin from the system
        if (targetUser.getRole() == Role.ADMIN && newRole != Role.ADMIN) {
            long adminCount = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.ADMIN && u.isActive())
                    .count();
            if (adminCount <= 1) {
                throw new IllegalStateException("Security Restriction: Cannot demote the last active Super Admin. At least one administrator must remain to manage the system.");
            }
        }
                
        targetUser.setRole(newRole);
        targetUser.setUpdatedBy(adminUser.getEmail());
        userRepository.save(targetUser);
    }

    public void adminRegisterUser(User adminUser, AdminUserRegisterRequest req) {
        verifyAdminAccess(adminUser);
        
        // Co-admins cannot create other super admins
        if (adminUser.getRole() == Role.CO_ADMIN && req.getRole() == Role.ADMIN) {
            throw new AccessDeniedException("Permission Denied: Co-Admins are not permitted to create users with Super Admin privileges.");
        }

        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }
        User newUser = User.builder()
                .fullName(req.getFullName())
                .phone(req.getPhone())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole())
                .active(true)
                .updatedBy(adminUser.getEmail())
                .build();
        userRepository.save(newUser);
    }

    public void updateUserCredentials(User adminUser, Long targetUserId, AdminUserUpdateRequest req) {
        if (adminUser.getRole() != Role.ADMIN) {
             throw new AccessDeniedException("Only Super Admins can modify existing credentials and roles.");
        }

        if (adminUser.getId().equals(targetUserId) && req.getRole() != null && req.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("Self-Modification Denied: You cannot change your own Super Admin role.");
        }

        User target = userRepository.findById(targetUserId).orElseThrow(() -> new IllegalArgumentException("Target user not found."));
        
        if (req.getFullName() != null) target.setFullName(req.getFullName());
        if (req.getPhone() != null) target.setPhone(req.getPhone());
        if (req.getEmail() != null) target.setEmail(req.getEmail());
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            target.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        
        if (req.getRole() != null) {
            // Prevent removing the last admin via credentials update
            if (target.getRole() == Role.ADMIN && req.getRole() != Role.ADMIN) {
                long adminCount = userRepository.findAll().stream()
                        .filter(u -> u.getRole() == Role.ADMIN && u.isActive())
                        .count();
                if (adminCount <= 1) {
                    throw new IllegalStateException("Critical Restriction: Cannot demote the last Super Admin via credential update.");
                }
            }
            target.setRole(req.getRole());
        }

        if (req.getActive() != null) {
            // Prevent deactivating the last admin
            if (target.getRole() == Role.ADMIN && !req.getActive()) {
                long adminCount = userRepository.findAll().stream()
                        .filter(u -> u.getRole() == Role.ADMIN && u.isActive())
                        .count();
                if (adminCount <= 1) {
                    throw new IllegalStateException("Critical Restriction: Cannot deactivate the last Super Admin.");
                }
            }
            target.setActive(req.getActive());
        }

        target.setUpdatedBy(adminUser.getEmail());
        userRepository.save(target);
    }
}

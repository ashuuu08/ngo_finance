package com.ngo_finance.ngo_finance.service;

import com.ngo_finance.ngo_finance.dto.DonationRequest;
import com.ngo_finance.ngo_finance.entity.Donation;
import com.ngo_finance.ngo_finance.entity.Role;
import com.ngo_finance.ngo_finance.entity.User;
import com.ngo_finance.ngo_finance.repository.DonationRepository;
import com.ngo_finance.ngo_finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;
    private final UserRepository userRepository;

    public void addDonation(User user, DonationRequest req) {
        Donation.DonationBuilder donationBuilder = Donation.builder()
                .transactionId("DON-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .amount(req.getAmount())
                .updatedBy(user.getEmail());

        if (user.getRole() == Role.USER) {
            // General authenticated consumers are entirely restricted to allocating exclusively locally to themselves
            donationBuilder.uName(user.getFullName());
            donationBuilder.user(user);
        } else {
            // Extended permissive boundaries scaling for Admin capabilities proxying input injections
            if (req.getTargetUserId() != null) {
                User targetProfile = userRepository.findById(req.getTargetUserId())
                        .orElseThrow(() -> new IllegalArgumentException("Administrative mapping failed: Intended target user mapping ID resolved cleanly as completely invalid."));
                donationBuilder.uName(targetProfile.getFullName());
                donationBuilder.user(targetProfile);
            } else if (req.getOfflineDonorName() != null && !req.getOfflineDonorName().trim().isEmpty()) {
                // Saving an unlinked physical object anonymously tracked
                donationBuilder.uName(req.getOfflineDonorName());
                // Notice we ensure internal `.user` linkage remains blank
            } else {
                // If practically no overrides occur, just log standard internal submission defaults
                donationBuilder.uName(user.getFullName());
                donationBuilder.user(user);
            }
        }
        
        donationRepository.save(donationBuilder.build());
    }

    public void deleteDonation(User user, Long donationId) {
        if (user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Restricted. Only a root level ADMIN holds permission to physically purge active donation components.");
        }
        
        donationRepository.findById(donationId).orElseThrow(() -> new IllegalArgumentException("Destruction trajectory failed cleanly. Logged donation target entirely null."));
        donationRepository.deleteById(donationId);
    }
}

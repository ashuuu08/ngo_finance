package com.ngo_finance.ngo_finance.config;

import com.ngo_finance.ngo_finance.entity.Role;
import com.ngo_finance.ngo_finance.entity.User;
import com.ngo_finance.ngo_finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        // 1. Create Super Admin if missing
        createAdminIfNotFound(adminEmail, adminPassword, "Super Admin", Role.ADMIN);
    }

    private void createAdminIfNotFound(String email, String password, String name, Role role) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            User admin = User.builder()
                    .fullName(name)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .active(true)
                    .updatedBy("System Setup")
                    .phone("9999999999")
                    .build();
            userRepository.save(admin);
            System.out.println("[INFO] Dynamic user created: " + email + " with role: " + role);
        } else {
            System.out.println("[INFO] User already exists: " + email);
        }
    }
}

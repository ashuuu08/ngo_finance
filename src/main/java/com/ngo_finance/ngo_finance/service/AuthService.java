package com.ngo_finance.ngo_finance.service;

import com.ngo_finance.ngo_finance.dto.AuthRequest;
import com.ngo_finance.ngo_finance.dto.AuthResponse;
import com.ngo_finance.ngo_finance.dto.RegisterRequest;
import com.ngo_finance.ngo_finance.entity.Role;
import com.ngo_finance.ngo_finance.entity.User;
import com.ngo_finance.ngo_finance.exception.UserAlreadyExistsException;
import com.ngo_finance.ngo_finance.repository.UserRepository;
import com.ngo_finance.ngo_finance.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("A user with this email already exists.");
        }

        var user = User.builder()
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // Bound safely to default general USER
                .active(true)
                .updatedBy(request.getEmail())
                .build();
        
        repository.save(user);
        var jwtToken = jwtUtil.generateToken(user);
        
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
                
        var jwtToken = jwtUtil.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}

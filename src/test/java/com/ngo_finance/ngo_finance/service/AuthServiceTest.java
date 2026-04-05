package com.ngo_finance.ngo_finance.service;

import com.ngo_finance.ngo_finance.dto.RegisterRequest;
import com.ngo_finance.ngo_finance.entity.Role;
import com.ngo_finance.ngo_finance.entity.User;
import com.ngo_finance.ngo_finance.exception.UserAlreadyExistsException;
import com.ngo_finance.ngo_finance.repository.UserRepository;
import com.ngo_finance.ngo_finance.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest request;

    @BeforeEach
    void setUp() {
        request = new RegisterRequest(
                "Jonathan Doe",
                "9876543210",
                "jonathan@test.com",
                "password123"
        );
    }

    @Test
    void register_ShouldCreateUserAndReturnToken_WhenEmailIsUnique() {
        // Arrange
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(jwtUtil.generateToken(any(User.class))).thenReturn("fake-jwt-token-xyz");

        // Act
        var response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("fake-jwt-token-xyz", response.getToken());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertEquals("Jonathan Doe", savedUser.getFullName());
        assertEquals("jonathan@test.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(Role.USER, savedUser.getRole());
        assertTrue(savedUser.isActive());
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        // Act & Assert
        Exception exception = assertThrows(UserAlreadyExistsException.class, () -> {
            authService.register(request);
        });

        assertEquals("A user with this email already exists.", exception.getMessage());
        
        // Ensure that repository.save() is NEVER called if the email exists
        verify(userRepository, never()).save(any(User.class));
    }
}

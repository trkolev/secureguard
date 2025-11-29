package com.project.ins.Integrationtest;

import com.project.ins.notification.client.NotificationClient;
import com.project.ins.notification.client.dto.SmsSendRequest;
import com.project.ins.numbergenerator.NumberGenerator;
import com.project.ins.user.model.User;
import com.project.ins.user.model.UserRole;
import com.project.ins.user.repository.UserRepository;
import com.project.ins.user.service.UserService;
import com.project.ins.wallet.model.Wallet;
import com.project.ins.web.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RegisterITest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private NumberGenerator numberGenerator;

    @MockitoBean
    private NotificationClient notificationClient;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userRepository.flush();

        when(numberGenerator.getResponse()).thenReturn("POL-TEST-12345");
        when(numberGenerator.getClaimNumbers()).thenReturn("CL-TEST-12345");

        when(notificationClient.sendSms(any(SmsSendRequest.class))).thenReturn("OK");
        when(notificationClient.getNotifications(any(UUID.class)))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));
        when(notificationClient.deleteSms(any(UUID.class))).thenReturn("OK");
    }

    @Test
    void registerUser_happyPath() {

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("test123")
                .firstName("FirstName")
                .lastName("LastName")
                .address("testAddress")
                .phoneNumber("0888888888")
                .email("test@test.test")
                .password("password")
                .confirmPassword("password")
                .build();

        userService.createUser(registerRequest);

        UserDetails userDetails = userService.loadUserByUsername(registerRequest.getUsername());
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(registerRequest.getUsername());

        User savedUser = userRepository.findByUsername(registerRequest.getUsername())
                .orElseThrow(() -> new AssertionError("User not found in database"));

        assertEquals(registerRequest.getUsername(), savedUser.getUsername());
        assertEquals(registerRequest.getFirstName(), savedUser.getFirstName());
        assertEquals(registerRequest.getLastName(), savedUser.getLastName());
        assertEquals(registerRequest.getAddress(), savedUser.getAddress());
        assertEquals(registerRequest.getEmail(), savedUser.getEmail());
        assertEquals(registerRequest.getPhoneNumber(), savedUser.getPhoneNumber());

        assertNotEquals(registerRequest.getPassword(), savedUser.getPassword());
        assertTrue(passwordEncoder.matches(registerRequest.getPassword(), savedUser.getPassword()));

        assertTrue(savedUser.isActive());
        assertNotNull(savedUser.getCreatedAt());

        assertNotNull(savedUser.getWallet());
        Wallet wallet = savedUser.getWallet();
        assertEquals(BigDecimal.valueOf(20.00), wallet.getBalance());
        assertEquals(savedUser, wallet.getOwner());

        assertEquals(UserRole.ADMIN, savedUser.getRole());
    }

    @Test
    void registerUser_shouldCreateUserWithUserRoleWhenOtherUsersExist() {
        RegisterRequest adminRequest = RegisterRequest.builder()
                .username("admin123")
                .firstName("Admin")
                .lastName("User")
                .email("admin@test.test")
                .password("password")
                .confirmPassword("password")
                .build();
        userService.createUser(adminRequest);

        RegisterRequest userRequest = RegisterRequest.builder()
                .username("user123")
                .firstName("Regular")
                .lastName("User")
                .email("user@test.test")
                .password("password")
                .confirmPassword("password")
                .build();

        userService.createUser(userRequest);

        User savedUser = userRepository.findByUsername(userRequest.getUsername())
                .orElseThrow(() -> new AssertionError("User not found in database"));

        assertEquals(UserRole.USER, savedUser.getRole());
    }
}

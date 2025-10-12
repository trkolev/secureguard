package com.project.ins.user.service;

import com.project.ins.user.model.User;
import com.project.ins.user.model.UserRole;
import com.project.ins.user.repository.UserRepository;
import com.project.ins.web.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder PasswordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        PasswordEncoder = passwordEncoder;
    }

    public void saveUser(RegisterRequest registerRequest) {

        Optional<User> byUsername = userRepository.findByUsernameAndEmail(registerRequest.getUsername(), registerRequest.getEmail());
        if (byUsername.isPresent()) {
            throw new IllegalStateException("Username or email already exists");
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .address(registerRequest.getAddress())
                .email(registerRequest.getEmail())
                .password(PasswordEncoder.encode(registerRequest.getPassword()))
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
    }
}

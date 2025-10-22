package com.project.ins.user.service;

import com.project.ins.security.UserData;
import com.project.ins.user.model.User;
import com.project.ins.user.model.UserRole;
import com.project.ins.user.repository.UserRepository;
import com.project.ins.web.dto.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));


        return new UserData(user.getId(), user.getUsername(), user.getPassword(), user.getRole(), user.isActive());
    }

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
                .isActive(true)
                .build();

        userRepository.save(user);
    }


    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    public RegisterRequest mapUserToRegisterRequest(User user) {

        return new RegisterRequest(user.getUsername(), user.getFirstName(), user.getLastName(), user.getAddress(), user.getEmail(), "", "");

    }
}

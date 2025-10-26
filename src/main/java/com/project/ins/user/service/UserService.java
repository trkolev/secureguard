package com.project.ins.user.service;

import com.project.ins.security.UserData;
import com.project.ins.user.model.User;
import com.project.ins.user.model.UserRole;
import com.project.ins.user.repository.UserRepository;
import com.project.ins.wallet.model.Wallet;
import com.project.ins.wallet.repository.WalletRepository;
import com.project.ins.wallet.service.WalletService;
import com.project.ins.web.dto.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder PasswordEncoder;
    private final WalletService walletService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, WalletRepository walletRepository, WalletService walletService) {
        this.userRepository = userRepository;
        PasswordEncoder = passwordEncoder;
        this.walletService = walletService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));


        return new UserData(user.getId(), user.getUsername(), user.getPassword(), user.getRole(), user.isActive());
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
                .wallet(walletService.createDefaultWallet())
                .build();

        user.getWallet().setOwner(user);
        userRepository.save(user);
        log.info("User {} successfully created", user.getUsername());
    }


    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    public RegisterRequest mapUserToRegisterRequest(User user) {

        return new RegisterRequest(user.getUsername(), user.getFirstName(), user.getLastName(), user.getAddress(), user.getEmail(), "", "");

    }


    public void save(User user) {
        userRepository.save(user);
    }
}

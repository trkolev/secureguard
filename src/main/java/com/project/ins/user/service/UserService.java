package com.project.ins.user.service;

import com.project.ins.exception.*;
import com.project.ins.security.UserData;
import com.project.ins.user.model.User;
import com.project.ins.user.model.UserRole;
import com.project.ins.user.repository.UserRepository;
import com.project.ins.wallet.service.WalletService;
import com.project.ins.web.dto.PasswordChangeRequest;
import com.project.ins.web.dto.RegisterRequest;
import com.project.ins.web.dto.UpdateUserDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletService walletService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, WalletService walletService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.walletService = walletService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));


        return new UserData(user.getId(), user.getUsername(), user.getPassword(), user.getRole(), user.isActive());
    }


    public void createUser(RegisterRequest registerRequest) {

        Optional<User> byUsername = userRepository.findByUsernameOrEmail(registerRequest.getUsername(), registerRequest.getEmail());
        if (byUsername.isPresent()) {
            throw new UserOrEmailAlreadyExistException("Username or email already exists");
        }

        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new RegisterPasswordDifferException("Passwords do not match", registerRequest);
        }

        UserRole role = UserRole.USER;
        if(findAll().isEmpty()){
            role = UserRole.ADMIN;
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .address(registerRequest.getAddress())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(role)
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

    public UpdateUserDto mapUserToUpdateDto(User user) {

        return new UpdateUserDto(user.getUsername(), user.getFirstName(), user.getLastName(), user.getAddress(), user.getPhoneNumber(), user.getEmail());

    }


    public void save(User user) {
        userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void updateRole(UUID id, String role) {

        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            throw new UserUpdateException("User not found");
        }

        User user = optionalUser.get();
        user.setRole(UserRole.valueOf(role));
        userRepository.save(user);

    }

    public void updateStatus(UUID id, String status) {

        Optional<User> userCheck = userRepository.findById(id);

        if (userCheck.isEmpty()) {
            throw new UserUpdateException("User not found");
        }

        User user = userCheck.get();
        user.setActive(!status.equalsIgnoreCase("disable"));
        userRepository.save(user);

    }


    public void changePassword(User user, @Valid PasswordChangeRequest passwordChange) {

        if(!passwordEncoder.matches(passwordChange.getCurrentPassword(), user.getPassword())) {
            throw new WrongPasswordException();
        }

        if(!passwordChange.getNewPassword().equals(passwordChange.getConfirmPassword())) {
            throw new PasswordDifferException();
        }

        user.setPassword(passwordEncoder.encode(passwordChange.getNewPassword()));
        userRepository.save(user);

    }

    public void updateUserInformation(@Valid UpdateUserDto updateUserDto, User user) {

        user.setFirstName(updateUserDto.getFirstName());
        user.setLastName(updateUserDto.getLastName());
        user.setAddress(updateUserDto.getAddress());
        user.setEmail(updateUserDto.getEmail());
        user.setPhoneNumber(updateUserDto.getPhoneNumber());

        userRepository.save(user);

    }
}

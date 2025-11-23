package com.project.ins.user;

import com.project.ins.exception.PasswordDifferException;
import com.project.ins.exception.RegisterPasswordDifferException;
import com.project.ins.exception.UserOrEmailAlreadyExistException;
import com.project.ins.exception.UserUpdateException;
import com.project.ins.exception.WrongPasswordException;
import com.project.ins.web.dto.PasswordChangeRequest;
import com.project.ins.web.dto.UpdateUserDto;
import com.project.ins.user.model.User;
import com.project.ins.user.model.UserRole;
import com.project.ins.user.repository.UserRepository;
import com.project.ins.user.service.UserService;
import com.project.ins.wallet.model.Wallet;
import com.project.ins.wallet.service.WalletService;
import com.project.ins.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private WalletService walletService;

    @InjectMocks
    private UserService userService;

    @Test
    void loadUserByUsername_shouldReturnUserDetailsWhenUserExists() {

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testUser")
                .password("encodedPassword")
                .role(UserRole.USER)
                .isActive(true)
                .build();

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername("testUser");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testUser");
    }

    @Test
    void loadUserByUsername_shouldThrowErrorWhenUserDoesNotExist() {

        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("nonexistent"));
    }

    @Test
    void createUser_shouldThrowExceptionWhenUsernameOrEmailAlreadyExists() {

        RegisterRequest registerRequest = new RegisterRequest();
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testUser")
                .email("test@test.com")
                .password("encodedPassword")
                .role(UserRole.USER)
                .isActive(true)
                .build();
        when(userRepository.findByUsernameOrEmail(any(), any())).thenReturn(Optional.of(user));

        assertThrows(UserOrEmailAlreadyExistException.class, () -> userService.createUser(registerRequest));
    }

    @Test
    void createUser_shouldThrowExceptionWhenBothPasswordsAreDifferent() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("testUser")
                .password("password")
                .confirmPassword("differentPassword")
                .build();

        when(userRepository.findByUsernameOrEmail(any(), any())).thenReturn(Optional.empty());

        assertThrows(RegisterPasswordDifferException.class, () -> userService.createUser(registerRequest));

    }

    @Test
    void createUser_shouldCrateAdminIhThereAreNoOtherUsers() {

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("adminUser")
                .email("admin@test.com")
                .firstName("Admin")
                .lastName("User")
                .password("password123")
                .confirmPassword("password123")
                .build();

        when(userRepository.findByUsernameOrEmail(any(), any())).thenReturn(Optional.empty());
        when(userRepository.findAll()).thenReturn(List.of());

        when(walletService.createDefaultWallet()).thenReturn(new Wallet());

        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.createUser(registerRequest);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(UserRole.ADMIN, savedUser.getRole());
    }

    @Test
    void findById_shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.findById(UUID.randomUUID()));
    }

    @Test
    void updateRole_shouldThrowExceptionWhenUserDoesNotExist() {

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(UserUpdateException.class,() -> userService.updateRole(UUID.randomUUID(), UserRole.USER.toString()));

    }

    @Test
    void updateRole_shouldSaveToRepository() {

        UUID userId = UUID.randomUUID();
        String newRole = "ADMIN";

        User existingUser = User.builder()
                .id(userId)
                .username("testUser")
                .email("test@test.com")
                .password("encodedPassword")
                .role(UserRole.USER)
                .isActive(true)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        userService.updateRole(userId, newRole);

        verify(userRepository).save(userCaptor.capture());

        User user = userCaptor.getValue();

        assertEquals(UserRole.ADMIN, user.getRole());
        assertEquals(userId, user.getId());

    }

    @Test
    void updateStatus_shouldThrowExceptionWhenUserDoesNotExist() {

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(UserUpdateException.class,() -> userService.updateRole(UUID.randomUUID(), "USER"));

    }

    @Test
    void updateStatus_shouldSaveToRepository() {

        UUID userId = UUID.randomUUID();
        String status = "ACTIVE";

        User user = User.builder()
                .id(userId)
                .username("testUser")
                .email("test@test.com")
                .password("encodedPassword")
                .role(UserRole.USER)
                .isActive(false)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        userService.updateStatus(userId, status);
        verify(userRepository).save(userCaptor.capture());

        User updatedUser = userCaptor.getValue();

        assertTrue(updatedUser.isActive());

    }

    @Test
    void changePassword_shouldUpdatePasswordWhenAllFieldsAreCorrect() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testUser")
                .password("encodedOldPassword")
                .role(UserRole.USER)
                .isActive(true)
                .build();

        PasswordChangeRequest passwordChangeRequest = PasswordChangeRequest.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        userService.changePassword(user, passwordChangeRequest);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("encodedNewPassword", savedUser.getPassword());
    }

    @Test
    void changePassword_shouldThrowExceptionWhenCurrentPasswordIsWrong() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testUser")
                .password("encodedOldPassword")
                .role(UserRole.USER)
                .isActive(true)
                .build();

        PasswordChangeRequest passwordChangeRequest = PasswordChangeRequest.builder()
                .currentPassword("wrongPassword")
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        when(passwordEncoder.matches("wrongPassword", "encodedOldPassword")).thenReturn(false);

        assertThrows(WrongPasswordException.class, () -> userService.changePassword(user, passwordChangeRequest));
    }

    @Test
    void changePassword_shouldThrowExceptionWhenNewPasswordAndConfirmPasswordDoNotMatch() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testUser")
                .password("encodedOldPassword")
                .role(UserRole.USER)
                .isActive(true)
                .build();

        PasswordChangeRequest passwordChangeRequest = PasswordChangeRequest.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword123")
                .confirmPassword("differentPassword")
                .build();

        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);

        assertThrows(PasswordDifferException.class, () -> userService.changePassword(user, passwordChangeRequest));
    }

    @Test
    void updateUserInformation_shouldUpdateAllUserFields() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("oldUsername")
                .firstName("OldFirstName")
                .lastName("OldLastName")
                .address("OldAddress")
                .email("old@email.com")
                .phoneNumber("123456789")
                .password("encodedPassword")
                .role(UserRole.USER)
                .isActive(true)
                .build();

        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .username("newUsername")
                .firstName("NewFirstName")
                .lastName("NewLastName")
                .address("NewAddress")
                .email("new@email.com")
                .phoneNumber("987654321")
                .build();

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        userService.updateUserInformation(updateUserDto, user);

        verify(userRepository).save(userCaptor.capture());
        User updatedUser = userCaptor.getValue();

        assertEquals("NewFirstName", updatedUser.getFirstName());
        assertEquals("NewLastName", updatedUser.getLastName());
        assertEquals("NewAddress", updatedUser.getAddress());
        assertEquals("new@email.com", updatedUser.getEmail());
        assertEquals("987654321", updatedUser.getPhoneNumber());
    }

    @Test
    void updateUserInformation_shouldUpdateUserWhenSomeFieldsAreNull() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testUser")
                .firstName("FirstName")
                .lastName("LastName")
                .address("Address")
                .email("test@email.com")
                .phoneNumber("123456789")
                .password("encodedPassword")
                .role(UserRole.USER)
                .isActive(true)
                .build();

        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .username("testUser")
                .firstName("NewFirstName")
                .lastName("NewLastName")
                .address(null)
                .email("new@email.com")
                .phoneNumber(null)
                .build();

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        userService.updateUserInformation(updateUserDto, user);

        verify(userRepository).save(userCaptor.capture());
        User updatedUser = userCaptor.getValue();

        assertEquals("NewFirstName", updatedUser.getFirstName());
        assertEquals("NewLastName", updatedUser.getLastName());
        assertNull(updatedUser.getAddress());
        assertEquals("new@email.com", updatedUser.getEmail());
        assertNull(updatedUser.getPhoneNumber());
    }

}

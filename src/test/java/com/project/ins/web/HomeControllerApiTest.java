package com.project.ins.web;

import com.project.ins.claim.service.ClaimService;
import com.project.ins.notification.service.NotificationService;
import com.project.ins.policy.service.PolicyService;
import com.project.ins.security.UserData;
import com.project.ins.transaction.service.TransactionService;
import com.project.ins.user.model.User;
import com.project.ins.user.model.UserRole;
import com.project.ins.user.service.UserService;
import com.project.ins.wallet.model.Wallet;
import com.project.ins.web.dto.UpdateUserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest(HomeController.class)
public class HomeControllerApiTest {

    @MockitoBean
    private  UserService userService;
    @MockitoBean
    private  TransactionService transactionService;
    @MockitoBean
    private  PolicyService policyService;
    @MockitoBean
    private  ClaimService claimService;
    @MockitoBean
    private  NotificationService notificationService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getHomeEndpoint_shouldReturn200OkAndHomeView() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDetails authentication = new UserData(userId, "testAdmin", "testPassword", UserRole.ADMIN, true);

        Wallet wallet = Wallet.builder()
                .balance(BigDecimal.valueOf(1000))
                .build();

        User user = User.builder()
                .id(userId)
                .wallet(wallet)
                .build();

        when(userService.findById(userId)).thenReturn(user);
        when(policyService.getAllByOwnerIdLimited(userId)).thenReturn(Collections.emptyList());
        when(policyService.getAllByUserId(userId)).thenReturn(Collections.emptyList());
        when(transactionService.findAllByUserIdLimit(userId)).thenReturn(Collections.emptyList());
        when(claimService.findAllByOwnerIdLimit(userId)).thenReturn(Collections.emptyList());
        when(claimService.findAllByOwnerId(userId)).thenReturn(Collections.emptyList());
        when(policyService.findTotalCoverage(userId)).thenReturn(BigDecimal.ZERO);
        when(policyService.findTotalPremium(userId)).thenReturn(BigDecimal.ZERO);
        when(claimService.findClaimsThisYear(userId)).thenReturn(0);
        when(claimService.upcomingPaymentsLimit(userId)).thenReturn(Collections.emptyList());
        when(notificationService.getNotificationsLimit(userId)).thenReturn(Collections.emptyList());


        MockHttpServletRequestBuilder httpRequest = get("/home")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(view().name("home"))
                .andExpect(status().isOk());

    }

    @Test
    void getProfileEndpoint_shouldReturn200OkAndProfileView() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDetails authentication = new UserData(userId, "testAdmin", "testPassword", UserRole.ADMIN, true);

        Wallet wallet = Wallet.builder()
                .balance(BigDecimal.valueOf(1000))
                .build();

        User user = User.builder()
                .id(userId)
                .username("testUser")
                .firstName("testFirstName")
                .lastName("testLastName")
                .address("testAddress")
                .phoneNumber("088888888")
                .email("test@test.com")
                .wallet(wallet)
                .build();

                UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .username("testUser")
                .firstName("testFirstName")
                .lastName("testLastName")
                .address("testAddress")
                .phoneNumber("088888888")
                .email("test@test.com")
                .build();

        when(userService.findById(userId)).thenReturn(user);
        when(userService.mapUserToUpdateDto(user)).thenReturn(updateUserDto);

        MockHttpServletRequestBuilder httpRequest = get("/profile")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(view().name("profile"))
                .andExpect(status().isOk());

    }

}

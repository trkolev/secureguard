package com.project.ins.web;

import com.project.ins.policy.model.Policy;
import com.project.ins.policy.service.PolicyService;
import com.project.ins.security.UserData;
import com.project.ins.transaction.model.Transaction;
import com.project.ins.transaction.model.TransactionStatus;
import com.project.ins.user.model.User;
import com.project.ins.user.model.UserRole;
import com.project.ins.user.service.UserService;
import com.project.ins.wallet.model.Wallet;
import com.project.ins.wallet.service.WalletService;
import com.project.ins.web.dto.PolicyRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PolicyController.class)
class PolicyControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PolicyService policyService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private WalletService walletService;

    @Test
    void getRequestToPolicy_shouldReturn200OkAndPolicyView() throws Exception {
        UUID userId = UUID.randomUUID();
        org.springframework.security.core.userdetails.UserDetails authentication =
                new UserData(userId, "testUser", "testPassword", UserRole.USER, true);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/policy")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("policy"))
                .andExpect(model().attributeExists("policyRequest"));
    }

    @Test
    void postRequestToCreatePolicy_shouldReturn3xxAndRedirectToPolicyWhenSuccessful() throws Exception {
        UUID userId = UUID.randomUUID();
        org.springframework.security.core.userdetails.UserDetails authentication =
                new UserData(userId, "testUser", "testPassword", UserRole.USER, true);

        Wallet wallet = Wallet.builder()
                .balance(BigDecimal.valueOf(1000))
                .build();

        User user = User.builder()
                .id(userId)
                .wallet(wallet)
                .build();

        Transaction successTransaction = Transaction.builder()
                .status(TransactionStatus.SUCCESS)
                .build();

        when(userService.findById(userId)).thenReturn(user);
        doNothing().when(policyService).createPolicy(any(PolicyRequest.class), any(User.class));
        when(walletService.reduceAmount(any(BigDecimal.class), any(User.class))).thenReturn(successTransaction);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/policy/create")
                .param("policyName", "VEHICLE")
                .param("startDate", LocalDate.now().toString())
                .param("endDate", LocalDate.now().plusYears(1).toString())
                .param("coverageDescription", "Test coverage")
                .param("premiumAmount", "500")
                .param("coverageAmount", "10000")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/policy"));
    }

    @Test
    void postRequestToCreatePolicy_shouldReturn3xxAndRedirectToPolicyWhenFailed() throws Exception {
        UUID userId = UUID.randomUUID();
        org.springframework.security.core.userdetails.UserDetails authentication =
                new UserData(userId, "testUser", "testPassword", UserRole.USER, true);

        Wallet wallet = Wallet.builder()
                .balance(BigDecimal.valueOf(1000))
                .build();

        User user = User.builder()
                .id(userId)
                .wallet(wallet)
                .build();

        Transaction failedTransaction = Transaction.builder()
                .status(TransactionStatus.FAILED)
                .build();

        when(userService.findById(userId)).thenReturn(user);
        doNothing().when(policyService).createPolicy(any(PolicyRequest.class), any(User.class));
        when(walletService.reduceAmount(any(BigDecimal.class), any(User.class))).thenReturn(failedTransaction);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/policy/create")
                .param("policyName", "VEHICLE")
                .param("startDate", LocalDate.now().toString())
                .param("endDate", LocalDate.now().plusYears(1).toString())
                .param("coverageDescription", "Test coverage")
                .param("premiumAmount", "500")
                .param("coverageAmount", "10000")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/policy"));
    }

    @Test
    void getRequestToPolicyView_shouldReturn200OkAndPolicyView() throws Exception {
        UUID userId = UUID.randomUUID();
        org.springframework.security.core.userdetails.UserDetails authentication =
                new UserData(userId, "testUser", "testPassword", UserRole.USER, true);

        when(policyService.getAllByUserId(userId)).thenReturn(Collections.emptyList());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/policy-view")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("policy-view"))
                .andExpect(model().attributeExists("policies"));
    }

    @Test
    void patchRequestToCancelPolicy_shouldReturn3xxAndRedirectToPolicyView() throws Exception {
        UUID policyId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        org.springframework.security.core.userdetails.UserDetails authentication =
                new UserData(userId, "testUser", "testPassword", UserRole.USER, true);

        doNothing().when(policyService).cancelPolicy(policyId);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/policy/{id}/cancel", policyId)
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/policy-view"));
    }
}

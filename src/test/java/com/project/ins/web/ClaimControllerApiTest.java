package com.project.ins.web;

import com.project.ins.claim.model.Claim;
import com.project.ins.claim.model.ClaimStatus;
import com.project.ins.claim.model.ClaimType;
import com.project.ins.claim.service.ClaimService;
import com.project.ins.policy.model.Policy;
import com.project.ins.policy.model.PolicyName;
import com.project.ins.policy.service.PolicyService;
import com.project.ins.security.UserData;
import com.project.ins.user.model.User;
import com.project.ins.user.model.UserRole;
import com.project.ins.user.service.UserService;
import com.project.ins.wallet.model.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ClaimController.class)
public class ClaimControllerApiTest {

    @MockitoBean
    private  PolicyService policyService;
    @MockitoBean
    private ClaimService claimService;
    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRequestToCreateClaim_shouldReturn200OkAndClaimModel() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDetails authentication = new UserData(userId, "testAdmin", "testPassword", UserRole.ADMIN, true);

        Policy policy = Policy.builder()
                .id(UUID.randomUUID())
                .policyName(PolicyName.PERSON)
                .coverageDescription("test")
                .build();

        when(policyService.getAllByUserId(userId)).thenReturn(List.of(policy));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/claims")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("claim"))
                .andExpect(model().attributeExists("claimRequest"))
                .andExpect(model().attributeExists("policies"));
    }

    @Test
    void postRequestToCreateClaim_shouldInvokeClaimServiceAndRedirectToClaims() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID policyId = UUID.randomUUID();
        UserDetails authentication = new UserData(userId, "testUser", "testPassword", UserRole.USER, true);

        Policy policy = Policy.builder()
                .id(policyId)
                .policyName(PolicyName.PERSON)
                .coverageDescription("Test coverage")
                .build();

        Wallet wallet = Wallet.builder()
                .balance(BigDecimal.valueOf(1000))
                .build();

        User user = User.builder()
                .id(userId)
                .wallet(wallet)
                .build();

        Claim claim = Claim.builder()
                .id(UUID.randomUUID())
                .claimType(ClaimType.LIFE)
                .status(ClaimStatus.REGISTERED)
                .build();

        when(userService.findById(userId)).thenReturn(user);
        when(claimService.create(any(), any(User.class))).thenReturn(claim);

        LocalDateTime incidentDate = LocalDateTime.now();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/claims")
                .param("incidentDate", incidentDate.toString())
                .param("description", "Test claim description")
                .param("clientPolicy.id", policyId.toString())
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/claims"));

        verify(claimService).create(any(), any(User.class));
    }

    @Test
    void postRequestToCreateClaim_shouldReturnClaimViewWhenValidationFails() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDetails authentication = new UserData(userId, "testUser", "testPassword", UserRole.USER, true);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/claims")
                .param("incidentDate", LocalDateTime.now().toString())
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("claim"));

        verify(claimService, never()).create(any(), any());
    }

}

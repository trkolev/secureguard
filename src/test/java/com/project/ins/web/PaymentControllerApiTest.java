package com.project.ins.web;

import com.project.ins.claim.model.Claim;
import com.project.ins.claim.service.ClaimService;
import com.project.ins.security.UserData;
import com.project.ins.user.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClaimService claimService;

    @Test
    void getRequestToPaymentView_shouldReturn200OkAndPaymentView() throws Exception {
        UUID userId = UUID.randomUUID();
        org.springframework.security.core.userdetails.UserDetails authentication =
                new UserData(userId, "testUser", "testPassword", UserRole.USER, true);

        when(claimService.upcomingPayments(userId)).thenReturn(Collections.emptyList());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/payment-view")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("payment-view"))
                .andExpect(model().attributeExists("payments"));
    }
}

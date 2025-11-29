package com.project.ins.web;

import com.project.ins.security.UserData;
import com.project.ins.user.model.User;
import com.project.ins.user.model.UserRole;
import com.project.ins.user.service.UserService;
import com.project.ins.wallet.model.Wallet;
import com.project.ins.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
class WalletControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private WalletService walletService;

    @Test
    void patchRequestToAddBalance_shouldReturn3xxAndRedirectToHome() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        org.springframework.security.core.userdetails.UserDetails authentication =
                new UserData(userId, "testUser", "testPassword", UserRole.USER, true);

        Wallet wallet = Wallet.builder()
                .id(walletId)
                .balance(BigDecimal.valueOf(1000))
                .build();

        User user = User.builder()
                .id(userId)
                .wallet(wallet)
                .build();

        when(userService.findById(userId)).thenReturn(user);
        doNothing().when(walletService).topUp(user);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/wallets/{id}/balance", walletId)
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }
}

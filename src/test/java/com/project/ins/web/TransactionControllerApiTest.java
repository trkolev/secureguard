package com.project.ins.web;

import com.project.ins.security.UserData;
import com.project.ins.transaction.model.Transaction;
import com.project.ins.transaction.service.TransactionService;
import com.project.ins.user.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @Test
    void getRequestToTransactions_shouldReturn200OkAndTransactionView() throws Exception {
        UUID userId = UUID.randomUUID();
        org.springframework.security.core.userdetails.UserDetails authentication =
                new UserData(userId, "testUser", "testPassword", UserRole.USER, true);

        when(transactionService.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        MockHttpServletRequestBuilder request = get("/transactions")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("transaction-view"))
                .andExpect(model().attributeExists("transactions"));
    }
}

package com.project.ins.web;

import com.project.ins.claim.model.Claim;
import com.project.ins.claim.service.ClaimService;
import com.project.ins.security.UserData;
import com.project.ins.user.model.UserRole;
import com.project.ins.web.dto.ClaimLiquidationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClaimService claimService;

    @Test
    void getRequestToEmployee_shouldReturn200OkAndEmployeeView() throws Exception {
        UUID userId = UUID.randomUUID();
        org.springframework.security.core.userdetails.UserDetails authentication =
                new UserData(userId, "testEmployee", "testPassword", UserRole.EMPLOYEE, true);
        when(claimService.findAll()).thenReturn(Collections.emptyList());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/employee")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("employee"))
                .andExpect(model().attributeExists("claims"))
                .andExpect(model().attributeExists("liquidRequest"));
    }

    @Test
    void patchRequestToApproveClaim_shouldReturn3xxAndRedirectToEmployee() throws Exception {
        UUID claimId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        org.springframework.security.core.userdetails.UserDetails authentication =
                new UserData(userId, "testEmployee", "testPassword", UserRole.EMPLOYEE, true);

        ClaimLiquidationRequest request = new ClaimLiquidationRequest();
        doNothing().when(claimService).approveClaim(claimId, request);

        MockHttpServletRequestBuilder httpRequest = MockMvcRequestBuilders.patch("/employee/claims/{id}/approve", claimId)
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employee"));
    }

    @Test
    void patchRequestToDeclineClaim_shouldReturn3xxAndRedirectToEmployee() throws Exception {
        UUID claimId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        org.springframework.security.core.userdetails.UserDetails authentication =
                new UserData(userId, "testEmployee", "testPassword", UserRole.EMPLOYEE, true);

        ClaimLiquidationRequest request = new ClaimLiquidationRequest();
        doNothing().when(claimService).declineClaim(claimId, request);

        MockHttpServletRequestBuilder httpRequest = MockMvcRequestBuilders.patch("/employee/claims/{id}/decline", claimId)
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employee"));
    }
}

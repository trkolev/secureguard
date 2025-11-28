package com.project.ins.web;

import com.project.ins.security.UserData;
import com.project.ins.user.model.User;
import com.project.ins.user.model.UserRole;
import com.project.ins.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
public class AdminControllerApiTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAdminPanel_shouldReturn200OkIfUserIsAdmin() throws Exception {

        UUID userId = UUID.randomUUID();
        UserDetails authentication = new UserData(userId, "adminUser", "adminPassword", UserRole.ADMIN, true);

        User user1 = User.builder()
                .id(UUID.randomUUID())
                .username("testUser1")
                .firstName("testFirstName1")
                .lastName("testLastName1")
                .email("test1@test.com")
                .role(UserRole.EMPLOYEE)
                .isActive(true)
                .build();

        User user2 = User.builder()
                .id(UUID.randomUUID())
                .username("testUser2")
                .firstName("testFirstName2")
                .lastName("testLastName2")
                .email("test2@test.com")
                .role(UserRole.USER)
                .isActive(true)
                .build();

        User user3 = User.builder()
                .id(UUID.randomUUID())
                .username("testUser3")
                .firstName("testFirstName3")
                .lastName("testLastName3")
                .email("test3@test.com")
                .role(UserRole.ADMIN)
                .isActive(true)
                .build();

        when(userService.findAll()).thenReturn(List.of(user1, user2, user3));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/admin")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(view().name("admin"))
                .andExpect(status().isOk());
    }

    @Test
    void patchRequestToUpdateRole_shouldReturn3xxAndRedirectToAdmin() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDetails authentication = new UserData(userId, "adminUser", "adminPassword", UserRole.ADMIN, true);

        doNothing().when(userService).updateRole(userId, "USER");

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/admin/users/{id}/role", userId)
                .param("role", "USER")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    void patchRequestToDisableShouldReturn() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDetails authentication = new UserData(userId, "adminUser", "adminPassword", UserRole.ADMIN, true);

        doNothing().when(userService).updateStatus(userId, "enabled");

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/admin/users/{id}/disable", userId)
                .param("disable", "true")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

    }
}

package com.project.ins.web;

import com.project.ins.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IndexController.class)
public class IndexControllerApiTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRequestToIndexController_shouldReturn200OkAndIndexView() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("index"));

    }

    @Test
    void getRequestToLoginShouldReturn200OkAndReturnLoginView() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/login");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void getRequestToLogin_shouldAddErrorMessageWhenErrorParameterIsPresent() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/login")
                .param("error", "true");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("errorMessage", "Incorrect username or password"));
    }

    @Test
    void getRequestToRegisterShouldReturn200OkAndReturnRegisterView() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/register");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void postRequestToRegister_shouldInvokeUserServiceAndRedirectToLogin() throws Exception {

        doNothing().when(userService).createUser(any());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/register")
                .param("username", "testUser")
                .param("email", "test@test.com")
                .param("firstName", "Test")
                .param("lastName", "User")
                .param("password", "password123")
                .param("confirmPassword", "password123")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService).createUser(any());

    }

    @Test
    void postRequestToRegister_shouldReturnRegisterViewWhenValidationFails() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/register")
                .param("username", "")
                .param("email", "invalid-email")
                .param("firstName", "Te")
                .param("lastName", "Us")
                .param("password", "123")
                .param("confirmPassword", "123")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        verify(userService, never()).createUser(any());
    }

    @Test
    void postRequestToRegister_shouldReturnRegisterViewWhenRequiredFieldsAreMissing() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/register")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        verify(userService, never()).createUser(any());
    }

    @Test
    void getRequestToAbout_shouldReturn200OkAndAboutView() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/about");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("about"));
    }

    @Test
    void getRequestToTerms_shouldReturn200OkAndTermsView() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/terms");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("terms"));
    }

}

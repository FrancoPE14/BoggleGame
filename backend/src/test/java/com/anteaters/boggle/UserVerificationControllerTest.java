package com.anteaters.boggle;

import com.anteaters.boggle.controller.UserVerificationController;
import com.anteaters.boggle.service.UserRegulationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserVerificationController.class)
@AutoConfigureMockMvc
public class UserVerificationControllerTest {

    @Autowired
    private MockMvc mock;

    @MockitoBean
    private UserRegulationService service;

    /**
     * when passed valid argument, the endpoint should return the proper username and status
     */
    @Test
    void registrationValidArgument() throws Exception {
        String username = "username", pwd = "password";

        when(service.createNewAccount(username, pwd)).thenReturn(true);

        mock.perform(post("/api/register")
                        .param("username", username)
                        .param("password", pwd))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.status").value(true));
    }

    /**
     * when passed invalid argument, the endpoint should return false and the proper username
     */
    @Test
    void registrationInvalidArgument() throws Exception {
        String username = "", pwd = "password";

        when(service.createNewAccount(username, pwd)).thenReturn(false);

        mock.perform(post("/api/register")
                        .param("username", username)
                        .param("password", pwd))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.status").value(false));
    }

    /**
     * when passed valid argument, the endpoint should return the proper username and status
     */
    @Test
    void loginValidArgument() throws Exception {
        String username = "username", pwd = "password";

        when(service.login(username, pwd)).thenReturn(true);

        mock.perform(post("/api/login")
                        .param("username", username)
                        .param("password", pwd))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.status").value(true));
    }

    /**
     * when passed invalid argument, the endpoint should return false and the proper username
     */
    @Test
    void loginInvalidArgument() throws Exception {
        String username = "", pwd = "password";

        when(service.login(username, pwd)).thenReturn(false);

        mock.perform(post("/api/login")
                        .param("username", username)
                        .param("password", pwd))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.status").value(false));
    }
}
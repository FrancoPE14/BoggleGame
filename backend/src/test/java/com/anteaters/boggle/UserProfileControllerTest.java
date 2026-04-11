package com.anteaters.boggle;

import com.anteaters.boggle.controller.UserProfileController;
import com.anteaters.boggle.entity.User;
import com.anteaters.boggle.repository.UserRepository;
import com.anteaters.boggle.service.UserRegulationService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MVC slice tests for {@link UserProfileController}.
 *
 * Why this test style:
 * - {@code @WebMvcTest} loads only the web layer (controller + MVC configuration).
 * - This prevents unrelated infrastructure (ex: database) from breaking the tests.
 *
 * What we validate:
 * 1) GET /api/user/profile returns 200 with correct user data when logged in.
 * 2) GET /api/user/profile returns 401 when user is not logged in.
 * 3) POST /api/user/profile/picture returns 200 when logged in with valid image data.
 * 4) POST /api/user/profile/picture returns 401 when user is not logged in.
 * 5) POST /api/user/profile/picture returns 400 when image data is missing or blank.
 */
@WebMvcTest(UserProfileController.class)
@AutoConfigureMockMvc
public class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRegulationService userService;

    @MockitoBean
    private UserRepository userRepository;

    /**
     * GET /api/user/profile should return 200 and correct user data when user is logged in.
     */
    @Test
    void getProfile_loggedIn_returns200WithUserData() throws Exception {
        User user = new User("Rae", "hashedpwd");

        when(userService.isLoggedIn("Rae")).thenReturn(true);
        when(userService.getUser("Rae")).thenReturn(user);

        mockMvc.perform(get("/api/user/profile").param("username", "Rae"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Rae"))
                .andExpect(jsonPath("$.matchesWon").value(0))
                .andExpect(jsonPath("$.highScore").value(0))
                .andExpect(jsonPath("$.profilePicture").value(""));
    }

    /**
     * GET /api/user/profile should return 401 when user is not logged in.
     */
    @Test
    void getProfile_notLoggedIn_returns401() throws Exception {
        when(userService.isLoggedIn("Rae")).thenReturn(false);

        mockMvc.perform(get("/api/user/profile").param("username", "Rae"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Not logged in"));
    }

    /**
     * POST /api/user/profile/picture should return 200 when logged in with valid image data.
     */
    @Test
    void uploadPicture_loggedIn_returns200() throws Exception {
        User user = new User("Rae", "hashedpwd");

        when(userService.isLoggedIn("Rae")).thenReturn(true);
        when(userRepository.findById("Rae")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/user/profile/picture")
                        .param("username", "Rae")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"imageData\":\"data:image/png;base64,abc123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));
    }

    /**
     * POST /api/user/profile/picture should return 401 when user is not logged in.
     */
    @Test
    void uploadPicture_notLoggedIn_returns401() throws Exception {
        when(userService.isLoggedIn("Rae")).thenReturn(false);

        mockMvc.perform(post("/api/user/profile/picture")
                        .param("username", "Rae")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"imageData\":\"data:image/png;base64,abc123\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Not authenticated"));
    }

    /**
     * POST /api/user/profile/picture should return 400 when image data is blank.
     */
    @Test
    void uploadPicture_blankImageData_returns400() throws Exception {
        when(userService.isLoggedIn("Rae")).thenReturn(true);

        mockMvc.perform(post("/api/user/profile/picture")
                        .param("username", "Rae")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"imageData\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("No image data provided"));
    }
}
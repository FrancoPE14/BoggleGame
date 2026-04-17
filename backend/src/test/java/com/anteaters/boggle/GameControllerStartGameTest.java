package com.anteaters.boggle;

import com.anteaters.boggle.controller.GameController;
import com.anteaters.boggle.service.BoggleBoard;
import com.anteaters.boggle.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-level tests for the start-game endpoint.
 *
 * These tests verify the HTTP contract exposed by GameController for successful
 * host starts and rejected non-host start attempts.
 */
@WebMvcTest(GameController.class)
public class GameControllerStartGameTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService service;

    /**
     * Verifies that the controller returns a successful response payload
     * when the service allows the host to start the game.
     */
    @Test
    void startGame_returnsSuccessForHost() throws Exception {
        when(service.startGame(0, "alice")).thenReturn(new BoggleBoard());

        mockMvc.perform(post("/api/start")
                        .param("sessionId", "0")
                        .param("username", "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.sessionId").value(0))
                .andExpect(jsonPath("$.username").value("alice"));
    }

    /**
     * Verifies that the controller returns a failure response payload
     * when the service rejects a non-host start attempt.
     */
    @Test
    void startGame_returnsFailureForNonHost() throws Exception {
        when(service.startGame(0, "bob"))
                .thenThrow(new IllegalStateException("Only the host can start the game"));

        mockMvc.perform(post("/api/start")
                        .param("sessionId", "0")
                        .param("username", "bob"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.sessionId").value(0))
                .andExpect(jsonPath("$.username").value("bob"))
                .andExpect(jsonPath("$.err").value("Only the host can start the game"));
    }
}
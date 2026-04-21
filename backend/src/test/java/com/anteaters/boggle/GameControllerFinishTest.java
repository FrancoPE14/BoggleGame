package com.anteaters.boggle;

import com.anteaters.boggle.controller.GameController;
import com.anteaters.boggle.model.FinishAckResponse;
import com.anteaters.boggle.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-level tests for the finish acknowledgement endpoint.
 *
 * These tests verify the HTTP contract exposed by GameController for finish
 * acknowledgement after the round-ended state has already been reached.
 */
@WebMvcTest(GameController.class)
public class GameControllerFinishTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService service;

    /**
     * Verifies that the finish endpoint returns the service response payload.
     */
    @Test
    void finishRound_returnsFinishAckResponse() throws Exception {
        when(service.acknowledgePlayerFinished(0, "alice"))
                .thenReturn(new FinishAckResponse(0, "alice", true, true, false));

        mockMvc.perform(post("/api/finish")
                        .param("sessionId", "0")
                        .param("username", "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(0))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.roundEnded").value(true))
                .andExpect(jsonPath("$.playerFinished").value(true))
                .andExpect(jsonPath("$.allPlayersFinished").value(false));
    }
}
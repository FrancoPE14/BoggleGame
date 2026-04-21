package com.anteaters.boggle;

import com.anteaters.boggle.controller.GameController;
import com.anteaters.boggle.model.SessionSummary;
import com.anteaters.boggle.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
public class GameControllerLobbyTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService service;

    @Test
    void getSessions_returnsSessionSummaries() throws Exception {
        when(service.getSessionSummaries()).thenReturn(List.of(
                new SessionSummary(0, false, 1, 3, "alice"),
                new SessionSummary(1, true, 3, 3, "bob")
        ));

        mockMvc.perform(get("/api/sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sessionId").value(0))
                .andExpect(jsonPath("$[0].started").value(false))
                .andExpect(jsonPath("$[0].playerCount").value(1))
                .andExpect(jsonPath("$[0].maxPlayers").value(3))
                .andExpect(jsonPath("$[0].hostUsername").value("alice"))
                .andExpect(jsonPath("$[1].sessionId").value(1))
                .andExpect(jsonPath("$[1].started").value(true))
                .andExpect(jsonPath("$[1].playerCount").value(3))
                .andExpect(jsonPath("$[1].maxPlayers").value(3))
                .andExpect(jsonPath("$[1].hostUsername").value("bob"));
    }

    @Test
    void joinSession_returnsUpdatedSummary() throws Exception {
        when(service.joinSession(0, "alice"))
                .thenReturn(new SessionSummary(0, false, 1, 3, "alice"));

        mockMvc.perform(post("/api/join")
                        .param("sessionId", "0")
                        .param("username", "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(0))
                .andExpect(jsonPath("$.started").value(false))
                .andExpect(jsonPath("$.playerCount").value(1))
                .andExpect(jsonPath("$.maxPlayers").value(3))
                .andExpect(jsonPath("$.hostUsername").value("alice"));
    }
}
package com.anteaters.boggle;

import com.anteaters.boggle.controller.GameStreamController;
import com.anteaters.boggle.service.GameEventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameStreamController.class)
class GameStreamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameEventService gameEventService;

    @Test
    void streamEndpointShouldReturnEventStream() throws Exception {
        when(gameEventService.subscribe("1")).thenReturn(new SseEmitter(0L));

        mockMvc.perform(get("/api/game/stream")
                        .param("sessionId", "1"))
                .andExpect(status().isOk());

        verify(gameEventService).subscribe("1");
    }

    @Test
    void testEventEndpointShouldReturnSent() throws Exception {
        mockMvc.perform(post("/api/game/test-event")
                        .param("sessionId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("sent"));

        verify(gameEventService).broadcastToSession(
                org.mockito.ArgumentMatchers.eq("1"),
                org.mockito.ArgumentMatchers.eq("test"),
                org.mockito.ArgumentMatchers.any()
        );
    }
}
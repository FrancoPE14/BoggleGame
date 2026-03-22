package com.anteaters.boggle;

import com.anteaters.boggle.controller.LeaderboardController;
import com.anteaters.boggle.service.LeaderboardService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MVC slice tests for {@link LeaderboardController}.
 *
 * Why this test style:
 * - {@code @WebMvcTest} loads only the web layer (controller + MVC configuration).
 * - This prevents unrelated infrastructure (ex: database) from breaking the tests.
 *
 * What we validate:
 * 1) Endpoint returns HTTP 200 with a JSON array.
 * 2) Response entries contain user_name and highest_score fields.
 * 3) Results are returned in descending score order.
 * 4) Empty leaderboard returns HTTP 200 with an empty array.
 */
@WebMvcTest(LeaderboardController.class)
@AutoConfigureMockMvc
public class LeaderboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LeaderboardService leaderboardService;

    /**
     * Endpoint should return HTTP 200 with a JSON array.
     */
    @Test
    void getLeaderboard_returns200WithJsonArray() throws Exception {
        when(leaderboardService.getLeaderboard()).thenReturn(List.of(
                Map.of("user_name", "Alice", "highest_score", 500)
        ));

        mockMvc.perform(get("/api/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    /**
     * Each entry should contain user_name and highest_score fields.
     */
    @Test
    void getLeaderboard_entryHasRequiredFields() throws Exception {
        when(leaderboardService.getLeaderboard()).thenReturn(List.of(
                Map.of("user_name", "Alice", "highest_score", 500)
        ));

        mockMvc.perform(get("/api/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user_name").value("Alice"))
                .andExpect(jsonPath("$[0].highest_score").value(500));
    }

    /**
     * Results should be in descending score order.
     */
    @Test
    void getLeaderboard_scoresAreInDescendingOrder() throws Exception {
        when(leaderboardService.getLeaderboard()).thenReturn(List.of(
                Map.of("user_name", "Alice", "highest_score", 500),
                Map.of("user_name", "Bob",   "highest_score", 300),
                Map.of("user_name", "Carol", "highest_score", 100)
        ));

        mockMvc.perform(get("/api/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].highest_score").value(500))
                .andExpect(jsonPath("$[1].highest_score").value(300))
                .andExpect(jsonPath("$[2].highest_score").value(100));
    }

    /**
     * Empty leaderboard should return HTTP 200 with an empty array.
     */
    @Test
    void getLeaderboard_emptyResult_returns200WithEmptyArray() throws Exception {
        when(leaderboardService.getLeaderboard()).thenReturn(List.of());

        mockMvc.perform(get("/api/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
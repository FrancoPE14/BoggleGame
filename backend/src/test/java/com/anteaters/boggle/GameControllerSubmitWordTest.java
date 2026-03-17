package com.anteaters.boggle;

import com.anteaters.boggle.controller.GameController;
import com.anteaters.boggle.model.WordSubmissionResult;
import com.anteaters.boggle.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
public class GameControllerSubmitWordTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService service;

    /**
     * Verifies that POST /api/submit-word returns the expected score state
     * for a valid accepted word.
     */
    @Test
    void submitWord_returnsScoringResponse() throws Exception {
        WordSubmissionResult result = new WordSubmissionResult(
                "apple",
                "APPLE",
                true,
                false,
                true,
                210,
                210,
                List.of("APPLE")
        );

        when(service.submitWord("user", "apple")).thenReturn(result);

        mockMvc.perform(post("/api/submit-word")
                        .param("username", "user")
                        .param("word", "apple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalWord").value("apple"))
                .andExpect(jsonPath("$.normalizedWord").value("APPLE"))
                .andExpect(jsonPath("$.accepted").value(true))
                .andExpect(jsonPath("$.duplicate").value(false))
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.pointsAwarded").value(210))
                .andExpect(jsonPath("$.currentScore").value(210))
                .andExpect(jsonPath("$.acceptedWords[0]").value("APPLE"));
    }

    /**
     * Verifies duplicate-word response shape through the API.
     */
    @Test
    void submitWord_duplicateWord_returnsDuplicateState() throws Exception {
        WordSubmissionResult result = new WordSubmissionResult(
                "apple",
                "APPLE",
                false,
                true,
                true,
                0,
                210,
                List.of("APPLE")
        );

        when(service.submitWord("user", "apple")).thenReturn(result);

        mockMvc.perform(post("/api/submit-word")
                        .param("username", "user")
                        .param("word", "apple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accepted").value(false))
                .andExpect(jsonPath("$.duplicate").value(true))
                .andExpect(jsonPath("$.pointsAwarded").value(0))
                .andExpect(jsonPath("$.currentScore").value(210));
    }
}
package com.anteaters.boggle;

import com.anteaters.boggle.controller.CustomDictionaryController;
import com.anteaters.boggle.dictionary.CustomDictionaryStorable;
import com.anteaters.boggle.dictionary.DictionaryTrie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MVC slice tests for {@link CustomDictionaryController}.
 *
 * Why this test style:
 * - {@code @WebMvcTest} loads only the web layer (controller + MVC configuration).
 * - This prevents unrelated infrastructure (ex: database) from breaking the tests.
 *
 * What we validate (edge cases):
 * 1) User tries to add a word that is already in the base dictionary.
 * 2) User tries to add a word that is already in the custom dictionary.
 * 3) User tries to add a blank/whitespace word.
 */
@WebMvcTest(CustomDictionaryController.class)
@AutoConfigureMockMvc
public class CustomDictionaryControllerTest {

    /**
     * MockMvc performs HTTP-like calls against the controller
     * without starting a real server.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * In Spring Boot 4, {@code @MockBean} was replaced by {@code @MockitoBean}.
     * We use it to override beans that the controller depends on.
     */
    @MockitoBean
    private CustomDictionaryStorable store;

    /**
     * Mocked base dictionary used by the controller to detect "already a real word".
     */
    @MockitoBean
    private DictionaryTrie baseDictionary;

    /**
     * Edge case #3:
     * Blank or whitespace input should be rejected with HTTP 400.
     */
    @Test
    void addWord_blankInput_returns400() throws Exception {
        mockMvc.perform(post("/api/custom-words").param("word", "   "))
                .andExpect(status().isBadRequest());
    }

    /**
     * Edge case #1:
     * If the word already exists in the base dictionary, adding it as a custom word is redundant.
     * Expect:
     * - added=false
     * - reason=already_in_base_dictionary
     */
    @Test
    void addWord_baseDictionaryWord_isRejectedAsRedundant() throws Exception {
        String baseWord = "APPLE";

        when(baseDictionary.isValidWord(eq(baseWord))).thenReturn(true);
        when(store.listWords(anyString())).thenReturn(Set.of());

        mockMvc.perform(post("/api/custom-words").param("word", baseWord))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.normalized").value(baseWord))
                .andExpect(jsonPath("$.added").value(false))
                .andExpect(jsonPath("$.reason").value("already_in_base_dictionary"));

        mockMvc.perform(get("/api/custom-words"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.words", not(hasItem(baseWord))));
    }

    /**
     * Edge case #2:
     * Adding the same custom word twice should succeed once, then report a duplicate.
     */
    @Test
    void addWord_duplicateCustomWord_reportsDuplicate() throws Exception {
        String customWord = "ZZZABCZZZABC";

        when(baseDictionary.isValidWord(eq(customWord))).thenReturn(false);
        when(store.addWord(anyString(), eq(customWord))).thenReturn(true).thenReturn(false);

        mockMvc.perform(post("/api/custom-words").param("word", customWord))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.normalized").value(customWord))
                .andExpect(jsonPath("$.added").value(true))
                .andExpect(jsonPath("$.reason").value("added"));

        mockMvc.perform(post("/api/custom-words").param("word", customWord))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.normalized").value(customWord))
                .andExpect(jsonPath("$.added").value(false))
                .andExpect(jsonPath("$.reason").value("already_in_custom_dictionary"));

        when(store.removeWord(anyString(), eq(customWord))).thenReturn(true);

        mockMvc.perform(delete("/api/custom-words").param("word", customWord))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.removed").value(true));
    }
}
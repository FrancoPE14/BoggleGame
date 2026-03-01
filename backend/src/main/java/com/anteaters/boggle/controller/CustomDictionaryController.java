package com.anteaters.boggle.controller;

import com.anteaters.boggle.dictionary.CustomDictionaryStorable;
import com.anteaters.boggle.dictionary.DictionaryTrie;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

/**
 * REST API endpoints for managing custom dictionary words.
 *
 * Custom words are stored in an in-memory store for now (server-lifetime only),
 * but the design supports scoping by roomId later.
 */
@RestController
public class CustomDictionaryController {

    private static final String GLOBAL_SCOPE = "GLOBAL";

    private final CustomDictionaryStorable store;
    private final DictionaryTrie baseDictionary;

    public CustomDictionaryController(CustomDictionaryStorable store, DictionaryTrie baseDictionary) {
        this.store = store;
        this.baseDictionary = baseDictionary;
    }

    /**
     * Adds a custom word to the GLOBAL scope.
     *
     * Edge cases handled:
     * - Blank input => 400 (IllegalArgumentException)
     * - Already exists in base dictionary => added=false, reason=already_in_base_dictionary
     * - Duplicate add to custom dictionary => added=false, reason=already_in_custom_dictionary
     */
    @PostMapping("/api/custom-words")
    public Map<String, Object> add(@RequestParam String word) {
        String normalized = normalize(word);

        // If it already exists in the base dictionary, adding it is redundant.
        if (baseDictionary.isValidWord(normalized)) {
            return Map.of(
                    "word", word,
                    "normalized", normalized,
                    "added", false,
                    "reason", "already_in_base_dictionary"
            );
        }

        boolean added = store.addWord(GLOBAL_SCOPE, normalized);

        return Map.of(
                "word", word,
                "normalized", normalized,
                "added", added,
                "reason", added ? "added" : "already_in_custom_dictionary"
        );
    }

    /**
     * Removes a custom word from the GLOBAL scope.
     */
    @DeleteMapping("/api/custom-words")
    public Map<String, Object> remove(@RequestParam String word) {
        String normalized = normalize(word);
        boolean removed = store.removeWord(GLOBAL_SCOPE, normalized);

        return Map.of(
                "word", word,
                "normalized", normalized,
                "removed", removed
        );
    }

    /**
     * Lists all custom words in the GLOBAL scope.
     */
    @GetMapping("/api/custom-words")
    public Map<String, Object> list() {
        Set<String> words = store.listWords(GLOBAL_SCOPE);

        return Map.of(
                "scope", GLOBAL_SCOPE,
                "words", words
        );
    }

    /**
     * Normalizes raw user input.
     * - trims whitespace
     * - converts to uppercase
     * - validates length and characters
     */
    private String normalize(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("Word must not be null.");
        }

        String normalized = raw.trim().toUpperCase();

        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Word must not be blank.");
        }

        if (normalized.length() < 3) {
            throw new IllegalArgumentException("Word must be at least 3 characters long.");
        }

        if (!normalized.matches("[A-Z]+")) {
            throw new IllegalArgumentException("Word must contain only letters A-Z.");
        }

        return normalized;
    }
}
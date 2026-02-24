package com.anteaters.boggle.controller;

import com.anteaters.boggle.dictionary.DictionaryTrie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST API endpoint for dictionary-based word verification.
 */
@RestController
public class WordVerificationController {

    private final DictionaryTrie dictionary;

    public WordVerificationController(DictionaryTrie dictionary) {
        this.dictionary = dictionary;
    }

    /**
     * Verifies whether a given string is a valid dictionary word.
     *
     * Example:
     * GET /api/verify?word=apple
     *
     * @param word raw user input
     * @return JSON containing the input and validation result
     */
    @GetMapping("/api/verify")
    public Map<String, Object> verify(@RequestParam String word) {
        boolean valid = dictionary.isValidWord(word);

        return Map.of(
                "word", word,
                "valid", valid
        );
    }
}
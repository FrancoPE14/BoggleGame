package com.anteaters.boggle.controller;

import com.anteaters.boggle.service.WordVerificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST API endpoint for word verification.
 *
 * Delegates verification logic to WordVerificationService.
 */
@RestController
public class WordVerificationController {

    private final WordVerificationService verificationService;

    public WordVerificationController(WordVerificationService verificationService) {
        this.verificationService = verificationService;
    }

    /**
     * Verifies whether a given string is a valid word.
     *
     * Example:
     * GET /api/verify?word=apple
     *
     * @param word raw user input
     * @return JSON containing the input and validation result
     */
    @GetMapping("/api/verify")
    public Map<String, Object> verify(@RequestParam String word) {
        boolean valid = verificationService.isValidWord(word);

        return Map.of(
                "word", word,
                "valid", valid
        );
    }
}
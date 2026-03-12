package com.anteaters.boggle.service;

import com.anteaters.boggle.model.WordSubmissionResult;
import org.springframework.stereotype.Service;


/**
 * Handles the full lifecycle of a word submission.
 *
 * Responsibilities:
 * - Normalize user input
 * - Prevent duplicate submissions
 * - Validate words using WordVerificationService
 * - Calculate score using ScoreCalculator
 * - Track accepted words and score using ScoreTracker
 *
 * This class acts as the single entry point for processing
 * word submissions during gameplay.
 */
@Service
public class WordSubmissionService {

    private final WordVerificationService verificationService;
    private final ScoreCalculator scoreCalculator;

    public WordSubmissionService(
            WordVerificationService verificationService,
            ScoreCalculator scoreCalculator
    ) {
        this.verificationService = verificationService;
        this.scoreCalculator = scoreCalculator;
    }

    /**
     * Processes a submitted word.
     *
     * @param rawWord raw user input
     * @param tracker score tracker for the current player
     * @return WordSubmissionResult describing the outcome
     */
    public WordSubmissionResult submitWord(String rawWord, ScoreTracker tracker) {

        String normalized = normalize(rawWord);

        if (tracker.hasWord(normalized)) {
            return new WordSubmissionResult(
                    rawWord,
                    normalized,
                    false,
                    true,
                    true,
                    0,
                    tracker.getScore(),
                    tracker.getAcceptedWords()
            );
        }

        boolean valid = verificationService.isValidWord(normalized);

        if (!valid) {
            return new WordSubmissionResult(
                    rawWord,
                    normalized,
                    false,
                    false,
                    false,
                    0,
                    tracker.getScore(),
                    tracker.getAcceptedWords()
            );
        }

        int points = scoreCalculator.calculateScore(normalized);

        tracker.recordWord(normalized, points);

        return new WordSubmissionResult(
                rawWord,
                normalized,
                true,
                false,
                true,
                points,
                tracker.getScore(),
                tracker.getAcceptedWords()
        );
    }

    /**
     * Normalizes raw user input.
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
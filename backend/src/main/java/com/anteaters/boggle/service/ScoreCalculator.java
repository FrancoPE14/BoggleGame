package com.anteaters.boggle.service;

import org.springframework.stereotype.Component;

/**
 * Calculates the score awarded for a valid word.
 *
 * Scoring is based on word length and starts at a base score of 100
 * for 3-letter words. Longer words receive progressively more points.
 *
 * Current scoring formula:
 *
 * Let n = word length - 3
 *
 * score = 100 + 50n + 10n(n - 1) / 2
 *
 * This produces:
 * - 3 letters -> 100
 * - 4 letters -> 150
 * - 5 letters -> 210
 * - 6 letters -> 280
 * - 7 letters -> 360
 * - 8 letters -> 450
 *
 * Design notes:
 * - 3-letter words define the base score.
 * - 4-letter words are scored separately from 3-letter words.
 * - Score continues to increase for all longer words.
 * - There is no "7+ letters" flat cap; every additional letter affects the score.
 *
 * This class assumes that the word has already been validated
 * by the dictionary verification system.
 */
@Component
public class ScoreCalculator {

    private static final int BASE_SCORE = 100;
    private static final int STEP_SCORE = 50;
    private static final int GROWTH_FACTOR = 10;

    /**
     * Calculates the score for a valid word.
     *
     * @param word normalized valid word
     * @return points awarded for the word
     * @throws IllegalArgumentException if the word is shorter than 3 characters
     */
    public int calculateScore(String word) {
        int length = word.length();

        if (length < 3) {
            throw new IllegalArgumentException("Word must be at least 3 characters long.");
        }

        int extraLetters = length - 3;

        return BASE_SCORE
                + STEP_SCORE * extraLetters
                + GROWTH_FACTOR * extraLetters * (extraLetters - 1) / 2;
    }
}
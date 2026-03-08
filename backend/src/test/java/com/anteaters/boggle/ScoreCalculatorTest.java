package com.anteaters.boggle;

import com.anteaters.boggle.service.ScoreCalculator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for ScoreCalculator.
 *
 * These tests verify that the scoring formula returns the expected score
 * for different word lengths.
 */
public class ScoreCalculatorTest {

    private final ScoreCalculator calculator = new ScoreCalculator();

    /**
     * 3-letter words should receive the base score of 100.
     */
    @Test
    void testThreeLetterWordsScoreOneHundred() {
        assertEquals(100, calculator.calculateScore("CAT"));
        assertEquals(100, calculator.calculateScore("DOG"));
    }

    /**
     * 4-letter words should score higher than 3-letter words.
     */
    @Test
    void testFourLetterWordsScoreOneHundredFifty() {
        assertEquals(150, calculator.calculateScore("TREE"));
        assertEquals(150, calculator.calculateScore("FISH"));
    }

    /**
     * 5-letter words should follow the scoring formula correctly.
     */
    @Test
    void testFiveLetterWordsScoreTwoHundredTen() {
        assertEquals(210, calculator.calculateScore("APPLE"));
    }

    /**
     * 6-letter words should follow the scoring formula correctly.
     */
    @Test
    void testSixLetterWordsScoreTwoHundredEighty() {
        assertEquals(280, calculator.calculateScore("BANANA"));
    }

    /**
     * 7-letter words should continue to increase in score.
     */
    @Test
    void testSevenLetterWordsScoreThreeHundredSixty() {
        assertEquals(360, calculator.calculateScore("ORANGES"));
    }

    /**
     * 8-letter words should continue to increase in score.
     */
    @Test
    void testEightLetterWordsScoreFourHundredFifty() {
        assertEquals(450, calculator.calculateScore("ELEPHANT"));
    }

    /**
     * Words shorter than 3 characters should be rejected.
     */
    @Test
    void testWordsShorterThanThreeCharactersThrowException() {
        assertThrows(IllegalArgumentException.class, () -> calculator.calculateScore("A"));
        assertThrows(IllegalArgumentException.class, () -> calculator.calculateScore("AB"));
    }
}
package com.anteaters.boggle;

import com.anteaters.boggle.service.ScoreTracker;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ScoreTracker.
 *
 * These tests verify that score is accumulated correctly,
 * accepted words are stored in submission order,
 * duplicate words are rejected, and reset restores initial state.
 */
public class ScoreTrackerTest {

    private final ScoreTracker tracker = new ScoreTracker();

    /**
     * A newly created tracker should start with score 0
     * and no accepted words.
     */
    @Test
    void testInitialState() {
        assertEquals(0, tracker.getScore());
        assertEquals(List.of(), tracker.getAcceptedWords());
    }

    /**
     * Recording a new word should increase score
     * and store the word in the accepted-word list.
     */
    @Test
    void testRecordWordAddsScoreAndStoresWord() {
        boolean recorded = tracker.recordWord("CAT", 100);

        assertTrue(recorded);
        assertEquals(100, tracker.getScore());
        assertEquals(List.of("CAT"), tracker.getAcceptedWords());
    }

    /**
     * Accepted words should remain in the order they were submitted.
     */
    @Test
    void testAcceptedWordsPreserveSubmissionOrder() {
        tracker.recordWord("CAT", 100);
        tracker.recordWord("TREE", 150);
        tracker.recordWord("APPLE", 210);

        assertEquals(List.of("CAT", "TREE", "APPLE"), tracker.getAcceptedWords());
    }

    /**
     * Recording multiple unique words should accumulate score correctly.
     */
    @Test
    void testScoreAccumulatesAcrossMultipleWords() {
        tracker.recordWord("CAT", 100);
        tracker.recordWord("TREE", 150);
        tracker.recordWord("APPLE", 210);

        assertEquals(460, tracker.getScore());
    }

    /**
     * Duplicate words should not be recorded twice
     * and should not change the score.
     */
    @Test
    void testDuplicateWordIsRejected() {
        boolean first = tracker.recordWord("CAT", 100);
        boolean second = tracker.recordWord("CAT", 100);

        assertTrue(first);
        assertFalse(second);
        assertEquals(100, tracker.getScore());
        assertEquals(List.of("CAT"), tracker.getAcceptedWords());
    }

    /**
     * hasWord should return true only for previously recorded words.
     */
    @Test
    void testHasWord() {
        tracker.recordWord("CAT", 100);

        assertTrue(tracker.hasWord("CAT"));
        assertFalse(tracker.hasWord("DOG"));
    }

    /**
     * reset should restore the tracker to its initial empty state.
     */
    @Test
    void testResetClearsScoreAndAcceptedWords() {
        tracker.recordWord("CAT", 100);
        tracker.recordWord("APPLE", 210);

        tracker.reset();

        assertEquals(0, tracker.getScore());
        assertEquals(List.of(), tracker.getAcceptedWords());
        assertFalse(tracker.hasWord("CAT"));
        assertFalse(tracker.hasWord("APPLE"));
    }
}
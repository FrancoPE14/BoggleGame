package com.anteaters.boggle;

import com.anteaters.boggle.service.*;
import com.anteaters.boggle.model.WordSubmissionResult;

import com.anteaters.boggle.dictionary.DictionaryTrie;
import com.anteaters.boggle.dictionary.InMemoryCustomDictionaryStore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WordSubmissionServiceTest {

    private WordSubmissionService service;
    private ScoreTracker tracker;

    @BeforeEach
    void setUp() {

        DictionaryTrie trie = new DictionaryTrie();
        trie.loadDictionary();

        InMemoryCustomDictionaryStore store = new InMemoryCustomDictionaryStore();

        WordVerificationService verification =
                new WordVerificationService(trie, store);

        ScoreCalculator calculator = new ScoreCalculator();

        service = new WordSubmissionService(
                verification,
                calculator
        );

        tracker = new ScoreTracker();
    }

    /**
     * Valid word should be accepted and scored.
     */
    @Test
    void validWordAccepted() {

        WordSubmissionResult result =
                service.submitWord("CAT", tracker);

        assertTrue(result.isAccepted());
        assertEquals(100, result.getPointsAwarded());
        assertEquals(100, tracker.getScore());
    }

    /**
     * Duplicate words should not increase score.
     */
    @Test
    void duplicateWordRejected() {

        service.submitWord("CAT", tracker);
        WordSubmissionResult result =
                service.submitWord("CAT", tracker);

        assertFalse(result.isAccepted());
        assertTrue(result.isDuplicate());
        assertEquals(100, tracker.getScore());
    }

    /**
     * Invalid word should be rejected.
     */
    @Test
    void invalidWordRejected() {

        WordSubmissionResult result =
                service.submitWord("ZZZZZZZZZZ", tracker);

        assertFalse(result.isAccepted());
        assertFalse(result.isValid());
        assertEquals(0, tracker.getScore());
    }

    /**
     * Raw input should be normalized.
     */
    @Test
    void normalizationWorks() {

        WordSubmissionResult result =
                service.submitWord("  cat  ", tracker);

        assertTrue(result.isAccepted());
        assertEquals("CAT", result.getNormalizedWord());
    }

    /**
     * Invalid raw inputs should throw exceptions.
     */
    @Test
    void invalidInputThrowsException() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.submitWord(null, tracker)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.submitWord("  ", tracker)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.submitWord("AB", tracker)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.submitWord("A1B", tracker)
        );
    }
}
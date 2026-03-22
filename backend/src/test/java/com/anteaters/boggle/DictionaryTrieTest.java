package com.anteaters.boggle;

import com.anteaters.boggle.dictionary.DictionaryTrie;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
class DictionaryTrieTest {

    //@Autowired
    private DictionaryTrie dictionary;

    @BeforeEach
    void setup(){
        dictionary = new DictionaryTrie();
        dictionary.loadDictionary();
    }

    @Test
    void isValidWord_rejectsNullAndTooShort() {
        assertFalse(dictionary.isValidWord(null));
        assertFalse(dictionary.isValidWord(""));
        assertFalse(dictionary.isValidWord("  "));
        assertFalse(dictionary.isValidWord("a"));
        assertFalse(dictionary.isValidWord("ap"));
    }

    @Test
    void isValidWord_rejectsNonAlphabetic() {
        assertFalse(dictionary.isValidWord("app-le"));
        assertFalse(dictionary.isValidWord("app1e"));
        assertFalse(dictionary.isValidWord("apple!"));
        assertFalse(dictionary.isValidWord("ap ple"));
        assertFalse(dictionary.isValidWord("한글"));
    }

    @Test
    void isValidWord_acceptsWhitespaceAndCase() {
        // You already verified this via curl, so this should be stable.
        assertTrue(dictionary.isValidWord("apple"));
        assertTrue(dictionary.isValidWord("APPLE"));
        assertTrue(dictionary.isValidWord("  apple  "));
    }

    @Test
    void isValidWord_rejectsARealAlphabeticNonWord() {
        // Deterministically generate an alphabetic string that is NOT in the dictionary.
        // This avoids relying on guessing whether a particular string exists.
        String nonWord = generateAlphabeticStringNotInDictionary(dictionary, 24);
        assertFalse(dictionary.isValidWord(nonWord));
    }

    @Test
    void contains_exactMatchOnly() {
        assertTrue(dictionary.contains("APPLE"));
        assertFalse(dictionary.contains("APPP")); // prefix, not a complete word (usually)
    }

    @Test
    void isPrefix_basicChecks() {
        assertTrue(dictionary.isPrefix("APP"));   // prefix of APPLE
        assertTrue(dictionary.isPrefix("APPLE")); // full word is also a prefix path
        assertFalse(dictionary.isPrefix("A1"));   // invalid characters => should fail
    }

    /**
     * Generates an uppercase A-Z string of a given length that does not exist in the dictionary.
     * Uses a fixed seed for determinism. Tries up to maxAttempts and fails if not found
     * (extremely unlikely with length >= ~20).
     */
    private static String generateAlphabeticStringNotInDictionary(DictionaryTrie dictionary, int length) {
        Random rnd = new Random(0); // fixed seed => deterministic
        int maxAttempts = 2000;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            String candidate = randomUppercase(rnd, length);

            // Use contains() directly since isValidWord() adds rules we don't want to mix in here.
            if (!dictionary.contains(candidate)) {
                return candidate;
            }
        }

        fail("Could not generate a non-word after " + maxAttempts + " attempts. " +
                "Try increasing length or maxAttempts.");
        return null; // unreachable
    }

    private static String randomUppercase(Random rnd, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char ch = (char) ('A' + rnd.nextInt(26));
            sb.append(ch);
        }
        return sb.toString();
    }
}
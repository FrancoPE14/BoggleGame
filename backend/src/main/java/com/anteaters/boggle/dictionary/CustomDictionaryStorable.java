package com.anteaters.boggle.dictionary;

import java.util.Set;

/**
 * Contract for storing and retrieving custom dictionary words.
 *
 * This interface is keyed by a scope identifier so the same contract can be reused
 * later for room-based dictionaries (e.g., using roomId as the scopeKey).
 *
 * For now, the application uses a single GLOBAL scope.
 */
public interface CustomDictionaryStorable {

    /**
     * Adds a normalized word to the given scope.
     *
     * @param scopeKey logical scope identifier (e.g., GLOBAL or a roomId)
     * @param word normalized word (trimmed + uppercased)
     * @return true if the word was added, false if it already existed
     */
    boolean addWord(String scopeKey, String word);

    /**
     * Removes a normalized word from the given scope.
     *
     * @param scopeKey logical scope identifier
     * @param word normalized word
     * @return true if removed, false if it was not present
     */
    boolean removeWord(String scopeKey, String word);

    /**
     * Checks whether the given scope contains a normalized word.
     *
     * @param scopeKey logical scope identifier
     * @param word normalized word
     * @return true if present
     */
    boolean contains(String scopeKey, String word);

    /**
     * Lists all custom words in the given scope.
     *
     * @param scopeKey logical scope identifier
     * @return unmodifiable set of words
     */
    Set<String> listWords(String scopeKey);
}
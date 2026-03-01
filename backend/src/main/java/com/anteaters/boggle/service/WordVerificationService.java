package com.anteaters.boggle.service;

import com.anteaters.boggle.dictionary.CustomDictionaryStorable;
import com.anteaters.boggle.dictionary.DictionaryTrie;
import org.springframework.stereotype.Service;

/**
 * Service layer responsible for word validation logic.
 *
 * A word is considered valid if:
 * 1. It satisfies basic formatting rules (length, A-Z only), AND
 * 2. It exists in either:
 *      - the default dictionary (Trie), OR
 *      - the custom dictionary store
 *
 * This service abstracts verification logic away from the controller.
 * Future room-based support can be added by passing roomId as scopeKey.
 */
@Service
public class WordVerificationService {

    public static final String GLOBAL_SCOPE = "GLOBAL";

    private final DictionaryTrie dictionaryTrie;
    private final CustomDictionaryStorable customStore;

    public WordVerificationService(DictionaryTrie dictionaryTrie,
                                   CustomDictionaryStorable customStore) {
        this.dictionaryTrie = dictionaryTrie;
        this.customStore = customStore;
    }

    /**
     * Verifies a word using the global scope.
     *
     * @param rawInput raw user input
     * @return true if valid
     */
    public boolean isValidWord(String rawInput) {
        return isValidWord(rawInput, GLOBAL_SCOPE);
    }

    /**
     * Verifies a word under a given scope.
     *
     * @param rawInput raw user input
     * @param scopeKey scope identifier (roomId in future)
     * @return true if valid
     */
    public boolean isValidWord(String rawInput, String scopeKey) {
        if (rawInput == null) return false;

        String word = normalize(rawInput);

        if (word.length() < 3) return false;

        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (ch < 'A' || ch > 'Z') return false;
        }

        return dictionaryTrie.contains(word)
                || customStore.contains(scopeKey, word);
    }

    /**
     * Normalizes input to match dictionary format.
     */
    private String normalize(String input) {
        return input.trim().toUpperCase();
    }
}
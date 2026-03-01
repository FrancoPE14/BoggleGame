package com.anteaters.boggle.dictionary;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of CustomDictionaryStore.
 *
 * This implementation stores custom words in a thread-safe map:
 * - key:   scopeKey (GLOBAL for now; roomId later)
 * - value: Set of normalized words
 *
 * Data persists only while the server is running.
 */
@Component
public class InMemoryCustomDictionaryStore implements CustomDictionaryStorable {

    private final ConcurrentHashMap<String, Set<String>> store = new ConcurrentHashMap<>();

    private Set<String> getOrCreateSet(String scopeKey) {
        return store.computeIfAbsent(scopeKey, k -> ConcurrentHashMap.newKeySet());
    }

    @Override
    public boolean addWord(String scopeKey, String word) {
        return getOrCreateSet(scopeKey).add(word);
    }

    @Override
    public boolean removeWord(String scopeKey, String word) {
        return getOrCreateSet(scopeKey).remove(word);
    }

    @Override
    public boolean contains(String scopeKey, String word) {
        return getOrCreateSet(scopeKey).contains(word);
    }

    @Override
    public Set<String> listWords(String scopeKey) {
        return Collections.unmodifiableSet(getOrCreateSet(scopeKey));
    }
}
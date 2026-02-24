package com.anteaters.boggle.dictionary;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Trie-based dictionary for fast word and prefix lookup.
 * Loads dictionary.txt at application startup.
 */
@Component
public class DictionaryTrie {

    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEndOfWord;
    }

    private final TrieNode root = new TrieNode();

    @PostConstruct
    public void loadDictionary() {
        System.out.println("[DictionaryTrie] Loading dictionary.txt...");
        long start = System.nanoTime();
        Runtime rt = Runtime.getRuntime();
        long memBefore = rt.totalMemory() - rt.freeMemory();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(
                                getClass().getClassLoader().getResourceAsStream("dictionary.txt")),
                        StandardCharsets.UTF_8))) {

            String line;
            int count = 0;

            while ((line = br.readLine()) != null) {
                String word = normalize(line);

                // Filter out entries that don't meet baseline dictionary constraints.
                // (This is for dictionary ingestion only, not user input validation.)
                if (isEligibleDictionaryEntry(word)) {
                    insert(word);
                    count++;
                }
            }

            System.out.println("[DictionaryTrie] Loaded " + count + " words.");

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to load dictionary.txt. The server cannot start without a dictionary.",
                    e
            );
        }

        long memAfter = rt.totalMemory() - rt.freeMemory();
        long ms = (System.nanoTime() - start) / 1_000_000;

        System.out.println("[DictionaryTrie] Load time: " + ms + " ms");
        System.out.println("[DictionaryTrie] Memory delta: " + ((memAfter - memBefore) / (1024 * 1024)) + " MB");
    }

    /**
     * Verifies whether the given input is a valid dictionary word.
     *
     * Rules:
     * - Must be at least 3 letters
     * - Must contain only A-Z characters
     * - Must exist in the loaded dictionary (Trie)
     *
     * Note: This validates "dictionary membership" only. It does not verify
     * whether the word can be formed on a specific Boggle board.
     *
     * @param input raw user input
     * @return true if valid; false otherwise
     */
    public boolean isValidWord(String input) {
        if (input == null) return false;

        String word = normalize(input);
        if (word.length() < 3) return false;

        // Validate characters without regex to avoid overhead and ambiguity.
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (ch < 'A' || ch > 'Z') return false;
        }

        return contains(word);
    }

    /**
     * Checks if the exact word exists in the dictionary.
     * Expects A-Z uppercase input.
     */
    public boolean contains(String word) {
        if (word == null) return false;

        TrieNode node = traverse(word);
        return node != null && node.isEndOfWord;
    }

    /**
     * Checks whether the given prefix exists in the dictionary.
     * Used for DFS pruning in the board solver.
     * Expects A-Z uppercase input.
     */
    public boolean isPrefix(String prefix) {
        if (prefix == null) return false;
        return traverse(prefix) != null;
    }

    private void insert(String word) {
        TrieNode current = root;

        for (char ch : word.toCharArray()) {
            int index = ch - 'A';
            if (current.children[index] == null) {
                current.children[index] = new TrieNode();
            }
            current = current.children[index];
        }

        current.isEndOfWord = true;
    }

    private TrieNode traverse(String str) {
        TrieNode current = root;

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch < 'A' || ch > 'Z') return null;

            int index = ch - 'A';
            current = current.children[index];
            if (current == null) return null;
        }

        return current;
    }

    private String normalize(String input) {
        return input.trim().toUpperCase();
    }

    /**
     * Eligibility rules for dictionary ingestion (dictionary.txt -> Trie).
     * Keeps the loaded dataset clean and predictable.
     */
    private boolean isEligibleDictionaryEntry(String word) {
        if (word.length() < 3) return false;

        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (ch < 'A' || ch > 'Z') return false;
        }

        return true;
    }
}
package com.anteaters.boggle.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tracks score-related gameplay state independently of the full game session.
 *
 * Responsibilities:
 * - maintain the player's current score
 * - store accepted valid words in submission order
 * - prevent duplicate words from being scored more than once
 *
 * Design notes:
 * - acceptedWords uses a List so the UI can later display words in the
 *   exact order they were submitted.
 * - seenWords uses a Set for efficient duplicate detection.
 *
 * This class does not calculate scores itself. Instead, it accepts
 * a point value from an external scoring component and records it.
 * This keeps score calculation and score state management separated.
 */
public class ScoreTracker {

    /**
     * Running total score for all accepted words.
     */
    private int score;

    /**
     * Accepted valid words in the order they were submitted.
     */
    private final List<String> acceptedWords;

    /**
     * Used to quickly check whether a word has already been scored.
     */
    private final Set<String> seenWords;

    public ScoreTracker() {
        this.score = 0;
        this.acceptedWords = new ArrayList<>();
        this.seenWords = new HashSet<>();
    }

    /**
     * Returns the current total score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns the accepted words in submission order.
     *
     * The returned list is immutable so callers cannot modify
     * internal tracker state.
     */
    public List<String> getAcceptedWords() {
        return List.copyOf(acceptedWords);
    }

    /**
     * Checks whether a word has already been accepted and scored.
     *
     * @param word normalized valid word
     * @return true if the word has already been recorded
     */
    public boolean hasWord(String word) {
        return seenWords.contains(word);
    }

    /**
     * Records a new accepted valid word and its awarded points.
     *
     * Behavior:
     * - If the word has already been recorded, no score is added.
     * - If the word is new, it is added to the ordered accepted-word list,
     *   marked as seen, and its points are added to the running total.
     *
     * @param word normalized valid word
     * @param points points already calculated for this word
     * @return true if the word was newly recorded, false if it was a duplicate
     */
    public boolean recordWord(String word, int points) {
        if (seenWords.contains(word)) {
            return false;
        }

        seenWords.add(word);
        acceptedWords.add(word);
        score += points;

        return true;
    }

    /**
     * Resets the tracker to its initial empty state.
     */
    public void reset() {
        score = 0;
        acceptedWords.clear();
        seenWords.clear();
    }
}
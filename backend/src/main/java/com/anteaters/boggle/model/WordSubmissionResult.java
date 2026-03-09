package com.anteaters.boggle.model;

import java.util.List;

/**
 * Represents the result of processing a submitted word.
 *
 * This class is intentionally independent of controller and HTTP code.
 * It stores enough information for future game-session or API layers
 * to decide what to display to the player.
 */
public class WordSubmissionResult {

    private final String originalWord;
    private final String normalizedWord;
    private final boolean accepted;
    private final boolean duplicate;
    private final boolean valid;
    private final int pointsAwarded;
    private final int currentScore;
    private final List<String> acceptedWords;

    public WordSubmissionResult(
            String originalWord,
            String normalizedWord,
            boolean accepted,
            boolean duplicate,
            boolean valid,
            int pointsAwarded,
            int currentScore,
            List<String> acceptedWords
    ) {
        this.originalWord = originalWord;
        this.normalizedWord = normalizedWord;
        this.accepted = accepted;
        this.duplicate = duplicate;
        this.valid = valid;
        this.pointsAwarded = pointsAwarded;
        this.currentScore = currentScore;
        this.acceptedWords = List.copyOf(acceptedWords);
    }

    public String getOriginalWord() {
        return originalWord;
    }

    public String getNormalizedWord() {
        return normalizedWord;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public boolean isValid() {
        return valid;
    }

    public int getPointsAwarded() {
        return pointsAwarded;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public List<String> getAcceptedWords() {
        return acceptedWords;
    }
}
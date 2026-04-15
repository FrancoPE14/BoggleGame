"use client";

import { useState, useCallback } from "react";

/**
 * Represents a submitted word with its validation and scoring result.
 * Each field maps to the backend's WordSubmissionResult response shape.
 * Once the backend submission endpoint is fully connected, pointsAwarded
 * will contain the actual score calculated by ScoreCalculator on the server.
 */
export type SubmittedWord = {
    /** The normalized (uppercase, trimmed) word that was submitted. */
    word: string;
    /** Whether the word is a valid dictionary word. */
    valid: boolean;
    /** Points awarded for this specific word. 0 if invalid or duplicate. */
    pointsAwarded: number;
};

/**
 * Custom hook that manages word submission, verification, and scoring state.
 *
 * Maintains the list of submitted words, a loading flag for in-flight requests,
 * and the player's running total score. Currently uses the GET /api/verify
 * endpoint for dictionary checks, but is structured so switching to the full
 * POST /api/submit-word endpoint requires minimal changes.
 *
 * @returns An object containing:
 *   - submittedWords: array of all submitted words with validation and score data
 *   - verifyWord: async function to submit and verify a word
 *   - loading: whether a verification request is in flight
 *   - currentScore: the player's running total score
 *   - resetWords: callback to clear all submitted words and reset score
 */
export default function useWordVerification(): {
    submittedWords: SubmittedWord[];
    verifyWord: (word: string) => Promise<boolean>;
    loading: boolean;
    currentScore: number;
    resetWords: () => void;
} {
    const [submittedWords, setSubmittedWords] = useState<SubmittedWord[]>([]);
    const [loading, setLoading] = useState(false);
    const [currentScore, setCurrentScore] = useState(0);

    /**
     * Submits a word for verification against the backend dictionary.
     * Normalizes the input, checks for duplicates locally, then sends the word
     * to the backend for dictionary validation.
     *
     * Words shorter than 3 letters are rejected immediately without a network call.
     * Duplicate submissions (same normalized word) are also rejected locally.
     *
     * @param word - The raw word string entered by the player.
     * @returns A promise that resolves to true if the word is valid, false otherwise.
     */
    const verifyWord = useCallback(
        async (word: string): Promise<boolean> => {
            const normalized = word.trim().toUpperCase();

            // Reject words shorter than the minimum length
            if (normalized.length < 3) {
                return false;
            }

            // Reject duplicate submissions without hitting the backend
            if (submittedWords.some((w) => w.word === normalized)) {
                return false;
            }

            setLoading(true);

            try {
                // TODO: Switch to POST /api/submit-word?username=<user>&word=<word>
                // once the game session lifecycle (start, addPlayer) is wired up
                // in the frontend. The backend endpoint already exists in
                // GameController.java and returns WordSubmissionResult with fields:
                // originalWord, normalizedWord, accepted, duplicate, valid,
                // pointsAwarded, currentScore, acceptedWords.
                const res = await fetch(
                    `/api/verify?word=${encodeURIComponent(normalized)}`,
                );
                const data: { word: string; valid: boolean } = await res.json();

                setSubmittedWords((prev) => [
                    {
                        word: normalized,
                        valid: data.valid,
                        pointsAwarded: 0, // Backend scoring not yet connected
                    },
                    ...prev,
                ]);

                // TODO: Replace with data.currentScore from the backend response
                // once the submission endpoint is connected.
                // currentScore stays at 0 until backend endpoint is available.
                setCurrentScore(0);

                setLoading(false);
                return data.valid;
            } catch {
                setLoading(false);
                return false;
            }
        },
        [submittedWords],
    );

    /**
     * Clears the submitted word list and resets the score to zero.
     * Called on game end and play again to reset state for the next game.
     */
    const resetWords = useCallback(() => {
        setSubmittedWords([]);
        setCurrentScore(0);
    }, []);

    return { submittedWords, verifyWord, loading, currentScore, resetWords };
}

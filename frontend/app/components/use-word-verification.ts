"use client";

import { useState, useCallback } from "react";

/**
 * Calculates the score for a word based on its length.
 * Uses the team scoring formula: score = 100 + 50*(n-3) + 10*(n-3)*(n-4)/2
 * where n is the number of letters in the word.
 */
function calculateScore(wordLength: number): number {
    if (wordLength < 3) return 0;
    const n = wordLength;
    return 100 + 50 * (n - 3) + 10 * (n - 3) * (n - 4) / 2;
}

/**
 * Represents a submitted word with its validation and scoring result.
 * Score is calculated locally using the team scoring formula.
 */
export type SubmittedWord = {
    word: string;
    valid: boolean;
    pointsAwarded: number;
};

/**
 * Custom hook that manages word submission, verification, and scoring state.
 * Uses GET /api/verify for dictionary checks; scoring is computed client-side.
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

    // Derived from submittedWords — no separate state needed
    const currentScore = submittedWords.reduce(
        (total, w) => (w.valid ? total + w.pointsAwarded : total),
        0,
    );

    /**
     * Submits a word for dictionary verification against the backend.
     * Score is calculated locally on valid words.
     */
    const verifyWord = useCallback(
        async (word: string): Promise<boolean> => {
            const normalized = word.trim().toUpperCase();

            if (normalized.length < 3) return false;

            if (submittedWords.some((w) => w.word === normalized)) return false;

            setLoading(true);

            try {
                const res = await fetch(
                    `http://localhost:8080/api/verify?word=${encodeURIComponent(normalized)}`,
                );
                const data: { word: string; valid: boolean } = await res.json();

                const pointsAwarded = data.valid ? calculateScore(normalized.length) : 0;

                setSubmittedWords((prev) => [
                    { word: normalized, valid: data.valid, pointsAwarded },
                    ...prev,
                ]);

                setLoading(false);
                return data.valid;
            } catch {
                setLoading(false);
                return false;
            }
        },
        [submittedWords],
    );

    /** Clears submitted words and resets score on game end or play again. */
    const resetWords = useCallback(() => {
        setSubmittedWords([]);
    }, []);

    return { submittedWords, verifyWord, loading, currentScore, resetWords };
}
"use client";

import { useState, useCallback } from "react";

const API_BASE = "http://localhost:8080";
const DEFAULT_SESSION_ID = 0;

/** Matches the backend WordSubmissionResult JSON (e.g. accepted, normalizedWord). */
type WordSubmissionResponse = {
    originalWord: string;
    normalizedWord: string;
    accepted: boolean;
    duplicate: boolean;
    valid: boolean;
    pointsAwarded: number;
    currentScore: number;
    acceptedWords: string[];
};

export type SubmittedWord = {
    word: string;
    valid: boolean;
    pointsAwarded: number;
};

type UseWordVerificationOptions = {
    /** Game session id; must match POST /api/start and the server session map. */
    sessionId?: number;
    /** If set, used instead of sessionStorage username (e.g. for tests). */
    username?: string | null;
};

/**
 * Manages word submission against POST /api/submit-word and keeps per-word
 * points and the running total from the backend response.
 */
export default function useWordVerification(
    options: UseWordVerificationOptions = {},
): {
    submittedWords: SubmittedWord[];
    verifyWord: (word: string) => Promise<boolean>;
    loading: boolean;
    currentScore: number;
    resetWords: () => void;
} {
    const sessionId = options.sessionId ?? DEFAULT_SESSION_ID;
    const usernameOverride = options.username ?? null;

    const [submittedWords, setSubmittedWords] = useState<SubmittedWord[]>([]);
    const [loading, setLoading] = useState(false);
    const [currentScore, setCurrentScore] = useState(0);

    const verifyWord = useCallback(
        async (word: string): Promise<boolean> => {
            const normalized = word.trim().toUpperCase();

            if (normalized.length < 3) {
                return false;
            }

            if (submittedWords.some((w) => w.word === normalized)) {
                return false;
            }

            const username =
                (usernameOverride && usernameOverride.trim()) ||
                (typeof window !== "undefined"
                    ? window.sessionStorage.getItem("username")?.trim() ?? ""
                    : "");

            if (!username) {
                return false;
            }

            setLoading(true);

            try {
                const params = new URLSearchParams({
                    sessionId: String(sessionId),
                    username,
                    word: word.trim(),
                });

                const res = await fetch(
                    `${API_BASE}/api/submit-word?${params.toString()}`,
                    { method: "POST" },
                );

                if (!res.ok) {
                    setLoading(false);
                    return false;
                }

                const data: WordSubmissionResponse = await res.json();

                setSubmittedWords((prev) => [
                    {
                        word: data.normalizedWord,
                        valid: data.valid,
                        pointsAwarded: data.pointsAwarded,
                    },
                    ...prev,
                ]);

                setCurrentScore(data.currentScore);
                setLoading(false);
                return data.accepted;
            } catch {
                setLoading(false);
                return false;
            }
        },
        [submittedWords, sessionId, usernameOverride],
    );

    const resetWords = useCallback(() => {
        setSubmittedWords([]);
        setCurrentScore(0);
    }, []);

    return { submittedWords, verifyWord, loading, currentScore, resetWords };
}

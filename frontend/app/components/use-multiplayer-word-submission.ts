"use client";

import { useCallback, useState } from "react";

/**
 * Represents a submitted word entry used by the board and score display
 * components in multiplayer mode.
 */
export type SubmittedWord = {
  word: string;
  valid: boolean;
  pointsAwarded: number;
};

/**
 * Represents the backend response returned by POST /api/submit-word.
 * This payload is produced by the server-side word submission pipeline and
 * includes the authoritative multiplayer score after the submission.
 */
export type WordSubmissionResponse = {
  originalWord: string;
  normalizedWord: string;
  accepted: boolean;
  duplicate: boolean;
  valid: boolean;
  pointsAwarded: number;
  currentScore: number;
  acceptedWords: string[];
};

/**
 * Provides multiplayer-specific word submission behavior.
 *
 * Unlike the single-player verification hook, this hook sends each word to
 * /api/submit-word so the backend can perform validation, duplicate checks,
 * score updates, and session-level state tracking.
 *
 * The function exposed to the board is still named verifyWord because the
 * existing board component expects that interface. In multiplayer mode,
 * however, that function performs a real submission rather than a local or
 * verification-only check.
 *
 * @param sessionId multiplayer session id
 * @param username current player username
 * @returns multiplayer submission state and handlers
 */
export default function useMultiplayerWordSubmission(
  sessionId: number,
  username: string,
): {
  submittedWords: SubmittedWord[];
  verifyWord: (word: string) => Promise<boolean>;
  loading: boolean;
  currentScore: number;
  resetWords: () => void;
} {
  const [submittedWords, setSubmittedWords] = useState<SubmittedWord[]>([]);
  const [currentScore, setCurrentScore] = useState(0);
  const [loading, setLoading] = useState(false);

  /**
   * Submits a word through the multiplayer backend pipeline.
   *
   * This method intentionally calls /api/submit-word instead of /api/verify
   * so that the backend remains the source of truth for score updates.
   *
   * @param word raw word selected from the board
   * @returns true if the backend accepted the word, otherwise false
   */
  const verifyWord = useCallback(
    async (word: string): Promise<boolean> => {
      const normalized = word.trim().toUpperCase();

      if (normalized.length < 3) {
        return false;
      }

      setLoading(true);

      try {
        const res = await fetch(
          `http://128.105.37.147:8080/api/submit-word?sessionId=${sessionId}&username=${encodeURIComponent(username)}&word=${encodeURIComponent(normalized)}`,
          { method: "POST" },
        );

        if (!res.ok) {
          const text = await res.text();
          throw new Error(`submit-word failed: ${res.status} ${text}`);
        }

        const data: WordSubmissionResponse = await res.json();

        setCurrentScore(data.currentScore);

        setSubmittedWords((prev) => [
          {
            word: data.normalizedWord,
            valid: data.accepted,
            pointsAwarded: data.pointsAwarded,
          },
          ...prev,
        ]);

        return data.accepted;
      } catch (error) {
        console.error("Failed to submit multiplayer word", error);
        return false;
      } finally {
        setLoading(false);
      }
    },
    [sessionId, username],
  );

  /**
   * Clears the local round state when a new multiplayer round starts.
   */
  const resetWords = useCallback(() => {
    setSubmittedWords([]);
    setCurrentScore(0);
  }, []);

  return {
    submittedWords,
    verifyWord,
    loading,
    currentScore,
    resetWords,
  };
}
"use client";

import { useState, useCallback } from "react";

export type SubmittedWord = {
    word: string;
    valid: boolean;
};

export default function useWordVerification() {
    const [submittedWords, setSubmittedWords] = useState<SubmittedWord[]>([]);
    const [loading, setLoading] = useState(false);

    const verifyWord = useCallback(async (word: string): Promise<boolean> => {
        const normalized = word.trim().toUpperCase();

        if (normalized.length < 3) {
            return false;
        }

        if (submittedWords.some((w) => w.word === normalized)) {
            return false;
        }

        setLoading(true);

        try {
            const res = await fetch(
                `http://localhost:8080/api/verify?word=${encodeURIComponent(normalized)}`
            );
            const data: { word: string; valid: boolean } = await res.json();

            setSubmittedWords((prev) => [
                { word: normalized, valid: data.valid },
                ...prev,
            ]);

            setLoading(false);
            return data.valid;
        } catch {
            setLoading(false);
            return false;
        }
    }, [submittedWords]);

    return { submittedWords, verifyWord, loading };
}

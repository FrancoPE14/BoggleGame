"use client";

import React, { useState } from "react";

type SubmittedWord = {
    word: string;
    valid: boolean;
};

export default function WordInput() {
    const [input, setInput] = useState("");
    const [submittedWords, setSubmittedWords] = useState<SubmittedWord[]>([]);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleSubmit = async () => {
        const trimmed = input.trim().toUpperCase();

        if (trimmed === "") {
            setError("Please enter a word.");
            return;
        }

        if (trimmed.length < 3) {
            setError("Words must be at least 3 letters.");
            return;
        }

        if (submittedWords.some((w) => w.word === trimmed)) {
            setError("You already submitted that word.");
            return;
        }

        setError("");
        setLoading(true);

        try {
            const res = await fetch(
                `http://localhost:8080/api/verify?word=${encodeURIComponent(trimmed)}`
            );
            const data = await res.json();

            setSubmittedWords((prev) => [
                { word: trimmed, valid: data.valid },
                ...prev,
            ]);
        } catch {
            setError("Could not reach the server. Is the backend running?");
        } finally {
            setLoading(false);
            setInput("");
        }
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === "Enter") {
            handleSubmit();
        }
    };

    const validCount = submittedWords.filter((w) => w.valid).length;

    return (
        <div className="flex flex-col items-center gap-3 w-full max-w-md mx-auto mt-4">
            <div className="flex gap-2 w-full">
                <input
                    type="text"
                    value={input}
                    onChange={(e) => setInput(e.target.value)}
                    onKeyDown={handleKeyDown}
                    placeholder="Enter a word..."
                    disabled={loading}
                    className="flex-1 px-3 py-2 border border-gray-300 rounded-lg
                               text-lg focus:outline-none focus:ring-2
                               focus:ring-amber-400 uppercase"
                />
                <button
                    onClick={handleSubmit}
                    disabled={loading}
                    className="px-5 py-2 bg-amber-400 hover:bg-amber-500
                               text-black font-bold rounded-lg transition
                               disabled:opacity-50"
                >
                    {loading ? "..." : "Submit"}
                </button>
            </div>

            {error && (
                <p className="text-red-500 text-sm font-medium">{error}</p>
            )}

            {submittedWords.length > 0 && (
                <div className="w-full mt-2">
                    <p className="text-sm text-gray-500 mb-2">
                        Words: {submittedWords.length} | Valid: {validCount}
                    </p>
                    <ul className="flex flex-wrap gap-2">
                        {submittedWords.map((w, i) => (
                            <li
                                key={i}
                                className={`px-3 py-1 rounded-full text-sm font-semibold
                                    ${
                                        w.valid
                                            ? "bg-green-100 text-green-700 border border-green-300"
                                            : "bg-red-100 text-red-700 border border-red-300"
                                    }`}
                            >
                                {w.word} {w.valid ? "✓" : "✗"}
                            </li>
                        ))}
                    </ul>
                </div>
            )}
        </div>
    );
}

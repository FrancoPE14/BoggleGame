"use client";

import { SubmittedWord } from "./use-word-verification";

type WordInputProps = {
    submittedWords: SubmittedWord[];
};

export default function WordInput({ submittedWords }: WordInputProps) {
    const validCount = submittedWords.filter((w) => w.valid).length;

    return (
        <div className="flex flex-col items-center gap-3 w-full max-w-md mx-auto mt-4">
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
                                    ${w.valid
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
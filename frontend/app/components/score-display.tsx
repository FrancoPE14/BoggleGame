"use client";

import { SubmittedWord } from "./use-word-verification";

/**
 * Props for the ScoreDisplay component.
 */
type ScoreDisplayProps = {
    /** The list of all submitted words with their validation and scoring status. */
    submittedWords: SubmittedWord[];
    /** The player's current total score from the backend. */
    currentScore: number;
};

/**
 * Displays the player's running score and per-word point breakdown.
 * All score values come from the backend response rather than local calculation.
 * This ensures multiplayer scoring is consistent across all players.
 *
 * @param props - Component props containing submitted words and current score.
 * @returns The rendered score display component.
 */
export default function ScoreDisplay({ submittedWords, currentScore }: ScoreDisplayProps): React.JSX.Element {
    return (
        <div className="flex flex-col items-center gap-2 w-full max-w-md mx-auto mt-4">
            <div className="flex flex-col items-center p-4 rounded-xl bg-amber-50 border border-amber-200 w-full">
                <span className="text-sm font-medium text-amber-700">
                    Total Score
                </span>
                <span className="text-4xl font-bold text-amber-600">
                    {currentScore}
                </span>
                {currentScore === 0 && submittedWords.length === 0 && (
                    <span className="text-xs text-amber-400 mt-1">
                        Submit words to earn points
                    </span>
                )}
            </div>

            {submittedWords.length > 0 && (
                <div className="w-full mt-2">
                    {submittedWords.map((w: SubmittedWord, i: number) => (
                        <div
                            key={i}
                            className="flex justify-between items-center px-3 py-1"
                        >
                            <span
                                className={`text-sm font-medium ${
                                    w.valid ? "text-green-700" : "text-red-400"
                                }`}
                            >
                                {w.word}
                            </span>
                            <span
                                className={`text-sm font-semibold ${
                                    w.valid ? "text-amber-600" : "text-red-400"
                                }`}
                            >
                                {w.valid
                                    ? `+${w.pointsAwarded}`
                                    : "0"}
                            </span>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

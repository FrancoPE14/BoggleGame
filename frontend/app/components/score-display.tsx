"use client";

import { SubmittedWord } from "./use-word-verification";

/**
 * Props for the ScoreDisplay component.
 */
type ScoreDisplayProps = {
    /** The list of all submitted words with their validation status. */
    submittedWords: SubmittedWord[];
    /**
     * Optional authoritative total (e.g. multiplayer server score).
     * When omitted, total is computed from submitted words only.
     */
    currentScore?: number;
};

/**
 * Calculates the score for a word based on its length.
 * Uses the team scoring formula: score = 100 + 50*(n-3) + 10*(n-3)*(n-4)/2
 * where n is the number of letters in the word.
 *
 * @param wordLength - The number of letters in the word.
 * @returns The calculated score, or 0 if the word is shorter than 3 letters.
 */
function calculateScore(wordLength: number): number {
    if (wordLength < 3) return 0;
    const n = wordLength;
    return 100 + 50 * (n - 3) + 10 * (n - 3) * (n - 4) / 2;
}

/**
 * Calculates the total score from all valid submitted words.
 *
 * @param submittedWords - The array of submitted words with validation status.
 * @returns The sum of scores for all valid words.
 */
function getTotalScore(submittedWords: SubmittedWord[]): number {
    return submittedWords.reduce((total: number, w: SubmittedWord): number => {
        if (w.valid) {
            return total + calculateScore(w.word.length);
        }
        return total;
    }, 0);
}

/**
 * Displays the player's running score and per-word point breakdown.
 * Receives the submitted words list from the parent and calculates
 * all scores on each render using the team scoring formula.
 *
 * @param props - The component props containing the submitted words array.
 * @returns The rendered score display component.
 */
export default function ScoreDisplay({
    submittedWords,
    currentScore: authoritativeTotal,
}: ScoreDisplayProps): React.JSX.Element {
    const computedTotal: number = getTotalScore(submittedWords);
    const totalScore: number =
        authoritativeTotal !== undefined ? authoritativeTotal : computedTotal;

    /** Per-row points: prefer server-supplied pointsAwarded when present. */
    function rowPoints(w: SubmittedWord): number {
        if (!w.valid) return 0;
        if (typeof w.pointsAwarded === "number") return w.pointsAwarded;
        return calculateScore(w.word.length);
    }

    return (
        <div className="flex flex-col items-center gap-2 w-full max-w-md mx-auto mt-4">
            <div className="flex flex-col items-center p-4 rounded-xl bg-amber-50 border border-amber-200 w-full">
                <span className="text-sm font-medium text-amber-700">
                    Total Score
                </span>
                <span className="text-4xl font-bold text-amber-600">
                    {totalScore}
                </span>
                {totalScore === 0 && submittedWords.length === 0 && (
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
                                {w.valid ? `+${rowPoints(w)}` : "0"}
                            </span>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}
import type { FinalScore } from "./use-game-stream";
import Link from "next/link";

interface GameOverProps {
    onPlayAgain: () => void;
    finalScore: number;
    finalScores?: FinalScore[];
    isMultiplayer?: boolean;
}

export default function GameOver({
    onPlayAgain,
    finalScore,
    finalScores,
    isMultiplayer,
}: GameOverProps) {
    if (!isMultiplayer) {
        return (
            <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
                <div className="w-90 rounded-xl border border-zinc-200 bg-white p-8 shadow-lg dark:border-zinc-700 dark:bg-zinc-900">
                    <h2 className="mb-2 text-xl font-semibold text-zinc-900 dark:text-zinc-100">
                        Time&apos;s up!
                        {finalScores && finalScores.length > 0 && (
                            <> {finalScores[0].username} wins!</>
                        )}
                    </h2>
                    <h5 className="mb-2 text-xl font-semibold text-zinc-900 dark:text-zinc-100">
                        Ranking:
                    </h5>
                    <ol>
                        {finalScores && finalScores.map((entry, index) => (
                            <li key={entry.username}>
                                {index + 1}. {entry.username} - {entry.score}
                            </li>
                        ))}
                    </ol>
                    <p className="mb-6 text-sm text-zinc-500 dark:text-zinc-400">
                        Return to the main menu whenever you&apos;re ready.
                    </p>
                    <button
                        onClick={onPlayAgain}
                        className="w-full rounded-md border border-zinc-300 bg-zinc-100 px-4 py-2 text-sm font-medium text-zinc-900 hover:bg-zinc-200 dark:border-zinc-700 dark:bg-zinc-800 dark:text-zinc-100 dark:hover:bg-zinc-700"
                    >
                        Return to Main Menu
                    </button>
                </div>
            </div>
        );
    } else {
        return (
            <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
                <div className="w-90 rounded-xl border border-zinc-200 bg-white p-8 shadow-lg dark:border-zinc-700 dark:bg-zinc-900">
                    <h2 className="mb-2 text-xl font-semibold text-zinc-900 dark:text-zinc-100">
                        Time&apos;s up!
                    </h2>
                    <p className="text-3xl font-bold text-amber-500 mb-1">{finalScore}</p>
                    <p className="mb-6 text-sm text-zinc-500 dark:text-zinc-400">
                        Start a new game whenever you&apos;re ready.
                    </p>
                    <button
                        onClick={onPlayAgain}
                        className="w-full rounded-md border border-zinc-300 bg-zinc-100 px-4 py-2 text-sm font-medium text-zinc-900 hover:bg-zinc-200 dark:border-zinc-700 dark:bg-zinc-800 dark:text-zinc-100 dark:hover:bg-zinc-700"
                    >
                        Play Again
                    </button>
                    <button
                        onClick={() => window.location.href = "/setting"}
                        className="w-full mt-2 rounded-md px-4 py-2 text-sm font-medium text-zinc-500 hover:text-zinc-700 dark:text-zinc-400 dark:hover:text-zinc-200"
                    >
                        Change Settings
                    </button>
                </div>
            </div>
        );
    }
}
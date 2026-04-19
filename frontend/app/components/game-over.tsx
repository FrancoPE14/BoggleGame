"use client";

interface GameOverProps {
    onPlayAgain: () => void;
    finalScore: number;
}

export default function GameOver({ onPlayAgain, finalScore }: GameOverProps) {
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
            </div>
        </div>
    );
}
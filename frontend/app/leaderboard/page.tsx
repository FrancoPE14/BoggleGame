"use client";

import Link from "next/link";
import { useEffect, useState } from "react";

interface LeaderboardEntry {
    user_name: string;
    highest_score: number;
}

/**
 * Displays the leaderboard screen showing player names and their highest scores.
 * Accessible from the main menu or end-of-game screen.
 */
export default function Page() {
    const [players, setPlayers] = useState<LeaderboardEntry[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        fetch("/api/leaderboard")
            .then((res) => {
                if (!res.ok) throw new Error("Failed to load leaderboard.");
                return res.json();
            })
            .then((data: LeaderboardEntry[]) => {
                setPlayers(data);
                setLoading(false);
            })
            .catch(() => {
                setError("Could not load leaderboard. Please try again later.");
                setLoading(false);
            });
    }, []);

    return (
        <div className="min-h-screen bg-zinc-100 text-zinc-900 dark:bg-zinc-950 dark:text-zinc-100 flex items-center justify-center px-4">
            <div
                className="w-full max-w-md rounded-xl border border-zinc-300 bg-white p-8 shadow-md dark:border-zinc-700 dark:bg-zinc-900">

                <h1 className="text-3xl font-bold mb-6 text-center text-black">Leaderboard</h1>

                {loading && (
                    <p className="text-center text-zinc-500 dark:text-zinc-400">
                        Loading scores...
                    </p>
                )}

                {error && (
                    <p className="text-center text-red-500">{error}</p>
                )}

                {!loading && !error && players.length === 0 && (
                    <p className="text-center text-zinc-500 dark:text-zinc-400 py-8">
                        No scores yet. Be the first to play!
                    </p>
                )}

                {!loading && !error && players.length > 0 && (
                    <table className="w-full text-left text-sm mb-8">
                        <thead>
                        <tr className="border-b border-zinc-200 dark:border-zinc-700">
                            <th className="pb-2 pr-4 font-semibold text-zinc-500 dark:text-zinc-400">Rank</th>
                            <th className="pb-2 pr-4 font-semibold text-zinc-500 dark:text-zinc-400">Player</th>
                            <th className="pb-2 text-right font-semibold text-zinc-500 dark:text-zinc-400">Highest
                                Score
                            </th>
                        </tr>
                        </thead>
                        <tbody>
                        {players.map((player, index) => (
                            <tr key={player.user_name} className="border-b border-zinc-100 dark:border-zinc-800">
                                <td className="py-3 pr-4">{index + 1}</td>
                                <td className="py-3 pr-4 font-medium">{player.user_name}</td>
                                <td className="py-3 text-right font-bold">{player.highest_score.toLocaleString()}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}

                <div className="flex justify-center mt-6">
                    <Link
                        href="/"
                        className="inline-block rounded-md border border-zinc-300 bg-zinc-100 px-6 py-2 text-sm font-medium hover:bg-zinc-200 dark:border-zinc-700 dark:bg-zinc-800 dark:hover:bg-zinc-700"
                    >
                        Back to Menu
                    </Link>
                </div>

            </div>
        </div>
    );
}
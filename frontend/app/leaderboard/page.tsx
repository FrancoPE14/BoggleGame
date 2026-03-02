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

    // Test (uncomment this and comment out the useEffect() up there)
    // useEffect(() => {
    //     setPlayers([
    //         { user_name: "Apple", highest_score: 4820 },
    //         { user_name: "Banana", highest_score: 4100 },
    //         { user_name: "Lemon", highest_score: 3750 },
    //     ]);
    //     setLoading(false);
    // }, []);

    return (
        // improve UI layout design using ChatGPT
        <div className="container py-5">
            <div
                className="shadow-lg mx-auto"
                style={{
                    maxWidth: "600px",
                    backgroundColor: "#ffffff",
                    borderRadius: "16px",
                    padding: "2rem",
                }}
            >
                <h1 className="text-center mb-4" style={{ color: "#5a4a00" }}>
                    Leaderboard
                </h1>

                {loading && (
                    <p className="text-center text-muted">Loading scores...</p>
                )}

                {error && (
                    <p className="text-center text-danger">{error}</p>
                )}

                {!loading && !error && players.length === 0 && (
                    <p className="text-center text-muted">
                        No scores yet. Be the first to play!
                    </p>
                )}

                {!loading && !error && players.length > 0 && (
                    <table className="table">
                        <thead>
                        <tr style={{ borderBottom: "2px solid #f3e7b3" }}>
                            <th>Rank</th>
                            <th>Player</th>
                            <th className="text-end">Highest Score</th>
                        </tr>
                        </thead>
                        <tbody>
                        {players.map((player, index) => (
                            <tr key={player.user_name}>
                                <td>{index + 1}</td>
                                <td>{player.user_name}</td>
                                <td className="text-end fw-bold">
                                    {player.highest_score.toLocaleString()}
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}

                <div className="text-center mt-4">
                    <Link
                        href="/"
                        style={{
                            backgroundColor: "#fce38a",
                            padding: "0.6rem 1.5rem",
                            borderRadius: "8px",
                            color: "#5a4a00",
                            fontWeight: 600,
                            textDecoration: "none",
                            display: "inline-block",
                        }}
                    >
                        Back to Menu
                    </Link>
                </div>
            </div>
        </div>
    );
}
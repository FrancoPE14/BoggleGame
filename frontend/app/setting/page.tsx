"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";

// TODO:  update these when backend is done
const DURATION_OPTIONS = [
    { label: "1 minute", value: 60 },
    { label: "2 minutes", value: 120 },
    { label: "3 minutes", value: 180 },
    { label: "5 minutes", value: 300 },
];

const BOARD_SIZE_OPTIONS = [
    { label: "4×4", value: 4 },
    { label: "5×5", value: 5 },
];

export interface GameSettings {
    durationSeconds: number;
    boardSize: number;
}

export default function SettingsPage() {
    const router = useRouter();

    const [durationSeconds, setDurationSeconds] = useState<number>(180);
    const [boardSize, setBoardSize] = useState<number>(4);
    const [error, setError] = useState<string | null>(null);

    const handleStartGame = async () => {
        // duration cannot be zero or negative
        if (durationSeconds <= 0) {
            setError("Please select a valid game duration.");
            return;
        }
        setError(null);

        const settings: GameSettings = { durationSeconds, boardSize };

        // TODO: replace with real API call when backend is ready
        // Example:
        // const res = await fetch("/api/game/create", {
        //   method: "POST",
        //   headers: { "Content-Type": "application/json" },
        //   body: JSON.stringify(settings),
        // });
        // const { gameId } = await res.json();
        // router.push(`/game?id=${gameId}`);

        // TODO: pass settings via query params until backend is ready
        router.push(
            `/game?duration=${durationSeconds}&boardSize=${boardSize}`
        );
    };

    const selectedStyle = {
        backgroundColor: "#fce38a",
        borderColor: "#f3c900",
        color: "#5a4a00",
        fontWeight: 600,
    };

    const unselectedStyle = {
        backgroundColor: "#ffffff",
        borderColor: "#dee2e6",
        color: "#5a4a00",
    };

    return (
        <div className="container py-5">
            <div
                className="shadow-lg mx-auto"
                style={{
                    maxWidth: "540px",
                    backgroundColor: "#ffffff",
                    borderRadius: "16px",
                    padding: "2rem",
                }}
            >
                <h1 className="text-center mb-1" style={{ color: "#5a4a00" }}>
                    Game Settings
                </h1>
                <p className="text-center text-muted mb-4">
                    Customize your game before you start.
                </p>

                {/* Game Duration */}
                <div className="mb-4">
                    <label className="form-label fw-semibold" style={{ color: "#5a4a00" }}>
                        Game Duration
                    </label>
                    <div className="pb-2" style={{ borderBottom: "2px solid #f3e7b3" }} />
                    <div className="d-flex flex-wrap gap-2 mt-3">
                        {DURATION_OPTIONS.map((opt) => (
                            <button
                                key={opt.value}
                                type="button"
                                className="btn"
                                style={durationSeconds === opt.value ? selectedStyle : unselectedStyle}
                                onClick={() => setDurationSeconds(opt.value)}
                            >
                                {opt.label}
                            </button>
                        ))}
                    </div>
                    <div className="form-text mt-2">
                        Selected: <strong style={{ color: "#5a4a00" }}>{durationSeconds} seconds</strong>
                    </div>
                </div>

                {/* Board Size */}
                <div className="mb-4">
                    <label className="form-label fw-semibold" style={{ color: "#5a4a00" }}>
                        Board Size
                    </label>
                    <div className="pb-2" style={{ borderBottom: "2px solid #f3e7b3" }} />
                    <div className="d-flex flex-wrap gap-2 mt-3">
                        {BOARD_SIZE_OPTIONS.map((opt) => (
                            <button
                                key={opt.value}
                                type="button"
                                className="btn"
                                style={boardSize === opt.value ? selectedStyle : unselectedStyle}
                                onClick={() => setBoardSize(opt.value)}
                            >
                                {opt.label}
                            </button>
                        ))}
                    </div>
                    <div className="form-text mt-2">
                        Selected: <strong style={{ color: "#5a4a00" }}>{boardSize}×{boardSize} grid</strong>
                    </div>
                </div>

                {error && <div className="alert alert-danger py-2">{error}</div>}

                <button
                    type="button"
                    className="btn w-100 mt-2"
                    style={{
                        backgroundColor: "#fce38a",
                        borderColor: "#f3c900",
                        color: "#5a4a00",
                        fontWeight: 600,
                        borderRadius: "8px",
                        padding: "0.6rem 1.5rem",
                    }}
                    onClick={handleStartGame}
                >
                    Start Game
                </button>

                <div className="text-center mt-3">
                    <Link href="/" style={{ color: "#5a4a00", textDecoration: "none", fontWeight: 500 }}>
                        ← Back to Menu
                    </Link>
                </div>
            </div>
        </div>
    );
}
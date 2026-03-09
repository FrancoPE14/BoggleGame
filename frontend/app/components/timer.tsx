"use client";

import { useState, useEffect, useRef, useCallback } from "react";
import GameOver from "./game-over";

const GAME_DURATION_SECONDS = 3 * 60; // 3 minutes

interface TimerProps {
    onGameStart?: () => void;
    onGameEnd?: () => void;
}

export default function Timer({ onGameStart, onGameEnd }: TimerProps) {
    const [secondsLeft, setSecondsLeft] = useState(GAME_DURATION_SECONDS);
    const [status, setStatus] = useState<"idle" | "running" | "ended">("idle");
    const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

    const clearTimer = () => {
        if (intervalRef.current !== null) {
            clearInterval(intervalRef.current);
            intervalRef.current = null;
        }
    };

    const handleGameEnd = useCallback(() => {
        clearTimer();
        setStatus("ended");
        onGameEnd?.();
    }, [onGameEnd]);

    useEffect(() => {
        if (status !== "running") return;

        intervalRef.current = setInterval(() => {
            setSecondsLeft((prev) => {
                if (prev <= 1) {
                    handleGameEnd();
                    return 0;
                }
                return prev - 1;
            });
        }, 1000);

        return clearTimer;
    }, [status, handleGameEnd]);

    const handleStart = () => {
        clearTimer();
        setSecondsLeft(GAME_DURATION_SECONDS);
        setStatus("running");
        onGameStart?.();
    };

    const handleEnd = () => {
        handleGameEnd();
    };

    const minutes = String(Math.floor(secondsLeft / 60)).padStart(2, "0");
    const seconds = String(secondsLeft % 60).padStart(2, "0");
    const isLowTime = secondsLeft <= 30 && status === "running";

    return (
        <>
            <div className="flex items-center gap-4 mt-6">
                <span
                    className="font-mono text-2xl font-semibold tabular-nums w-20 text-center text-black dark:text-white">
                    {minutes}:{seconds}
                </span>

                {status === "running" ? (
                    <button
                        onClick={handleEnd}
                        className="rounded-md border border-zinc-300 bg-zinc-100 px-4 py-2 text-sm font-medium text-zinc-900 hover:bg-zinc-200 dark:border-zinc-700 dark:bg-zinc-800 dark:text-zinc-100 dark:hover:bg-zinc-700"
                    >
                        End
                    </button>
                ) : (
                    <button
                        onClick={handleStart}
                        disabled={status === "ended"}
                        className="rounded-md border border-zinc-300 bg-zinc-100 px-4 py-2 text-sm font-medium text-zinc-900 hover:bg-zinc-200 disabled:opacity-40 disabled:cursor-not-allowed dark:border-zinc-700 dark:bg-zinc-800 dark:text-zinc-100 dark:hover:bg-zinc-700"
                    >
                        Start
                    </button>
                )}
            </div>

            {status === "ended" && <GameOver onPlayAgain={handleStart}/>}
        </>
    );
}
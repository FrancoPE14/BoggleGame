"use client";

import { useState, useEffect, useRef, useCallback } from "react";
import GameOver from "./game-over";

const GAME_DURATION_SECONDS = 3 * 60; // 3 minutes

interface TimerProps {
    onGameStart?: () => void;
    onGameEnd?: () => void;
    finalScore?: number;
}

export default function Timer({ onGameStart, onGameEnd, finalScore = 0 }: TimerProps) {
    const [secondsLeft, setSecondsLeft] = useState(GAME_DURATION_SECONDS);
    const [status, setStatus] = useState<"idle" | "running" | "ended">("running");
    const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

    /**
     * Notify parent that game has started once on initial mount.
     * Intentionally omit callback deps so this does not re-run when the parent passes a new function reference.
     */
    useEffect(() => {
        onGameStart?.();
        // eslint-disable-next-line react-hooks/exhaustive-deps -- fire once on mount only
    }, []);

    /**
     * Clears the active countdown interval if one exists.
     */
    const clearTimer = () => {
        if (intervalRef.current !== null) {
            clearInterval(intervalRef.current);
            intervalRef.current = null;
        }
    };

    /**
     * Stops the timer, updates status to ended, and notifies parent.
     */
    const handleGameEnd = useCallback(() => {
        clearTimer();
        setStatus("ended");
        setTimeout(() => onGameEnd?.(), 0);
        // defers the parent update until after Timer finishes its own render cycle
    }, [onGameEnd]);

    /**
     * Starts a 1-second interval countdown when status is "running".
     */
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

    /**
     * Resets and restarts the timer from the full duration.
     */
    const handleStart = () => {
        clearTimer();
        setSecondsLeft(GAME_DURATION_SECONDS);
        setStatus("running");
        onGameStart?.();
    };

    /**
     * Manually ends the game before time runs out.
     */
    const handleEnd = () => {
        handleGameEnd();
    };

    const minutes = String(Math.floor(secondsLeft / 60)).padStart(2, "0");
    const seconds = String(secondsLeft % 60).padStart(2, "0");

    return (
        <>
            <div className="flex items-center gap-4 mt-6">
                <span className="font-mono text-2xl font-semibold tabular-nums w-20 text-center text-black dark:text-white">
                    {minutes}:{seconds}
                </span>

                {status === "running" && (
                    <button
                        onClick={handleEnd}
                        className="rounded-md border border-zinc-300 bg-zinc-100 px-4 py-2 text-sm font-medium text-zinc-900 hover:bg-zinc-200 dark:border-zinc-700 dark:bg-zinc-800 dark:text-zinc-100 dark:hover:bg-zinc-700"
                    >
                        End
                    </button>
                )}
            </div>

            {status === "ended" && (
                <GameOver onPlayAgain={handleStart} finalScore={finalScore} />
            )}
        </>
    );
}
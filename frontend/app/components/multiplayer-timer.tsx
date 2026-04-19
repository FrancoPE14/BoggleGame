"use client";

import { useEffect, useMemo, useRef, useState } from "react";

const GAME_DURATION_SECONDS = 3 * 60;

interface MultiplayerTimerProps {
  gameStarted: boolean;
  roundEnded: boolean;
  startSignal: number;
}

export default function MultiplayerTimer({
  gameStarted,
  roundEnded,
  startSignal,
}: MultiplayerTimerProps) {
  const [secondsLeft, setSecondsLeft] = useState(GAME_DURATION_SECONDS);
  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const clearTimer = () => {
    if (intervalRef.current !== null) {
      clearInterval(intervalRef.current);
      intervalRef.current = null;
    }
  };

  useEffect(() => {
    clearTimer();
    setSecondsLeft(GAME_DURATION_SECONDS);

    if (!gameStarted || roundEnded) {
      return;
    }

    intervalRef.current = setInterval(() => {
      setSecondsLeft((prev) => {
        if (prev <= 1) {
          clearTimer();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return clearTimer;
  }, [gameStarted, roundEnded, startSignal]);

  useEffect(() => {
    if (roundEnded) {
      clearTimer();
      setSecondsLeft(0);
    }
  }, [roundEnded]);

  const { minutes, seconds } = useMemo(() => {
    return {
      minutes: String(Math.floor(secondsLeft / 60)).padStart(2, "0"),
      seconds: String(secondsLeft % 60).padStart(2, "0"),
    };
  }, [secondsLeft]);

  return (
    <div className="mt-6 flex items-center gap-4">
      <span className="w-20 text-center font-mono text-2xl font-semibold tabular-nums text-black dark:text-white">
        {minutes}:{seconds}
      </span>
    </div>
  );
}
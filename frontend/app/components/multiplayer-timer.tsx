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
  const [tick, setTick] = useState(0);
  const startTimeRef = useRef<number | null>(null);

  useEffect(() => {
    if (!gameStarted || roundEnded) {
      return;
    }

    startTimeRef.current = Date.now();
    setTick(Date.now());

    const intervalId = setInterval(() => {
      setTick(Date.now());
    }, 1000);

    return () => {
      clearInterval(intervalId);
    };
  }, [gameStarted, roundEnded, startSignal]);

  const secondsLeft = useMemo(() => {
    if (!startTimeRef.current) {
      return GAME_DURATION_SECONDS;
    }

    if (roundEnded) {
      return 0;
    }

    const elapsedSeconds = Math.floor((tick - startTimeRef.current) / 1000);
    return Math.max(0, GAME_DURATION_SECONDS - elapsedSeconds);
  }, [tick, roundEnded]);

  const minutes = String(Math.floor(secondsLeft / 60)).padStart(2, "0");
  const seconds = String(secondsLeft % 60).padStart(2, "0");

  return (
    <div className="mt-6 flex items-center gap-4">
      <span className="w-20 text-center font-mono text-2xl font-semibold tabular-nums text-black dark:text-white">
        {minutes}:{seconds}
      </span>
    </div>
  );
}
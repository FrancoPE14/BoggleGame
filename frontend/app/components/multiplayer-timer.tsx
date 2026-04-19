"use client";

import { useEffect, useMemo, useState } from "react";

const GAME_DURATION_SECONDS = 3 * 60;

interface MultiplayerTimerProps {
  roundEnded: boolean;
  startSignal: number;
}

export default function MultiplayerTimer({
  roundEnded,
  startSignal,
}: MultiplayerTimerProps) {
  const [startedAt] = useState(() => Date.now());
  const [tick, setTick] = useState(() => Date.now());

  useEffect(() => {
    const intervalId = setInterval(() => {
      setTick(Date.now());
    }, 1000);

    return () => {
      clearInterval(intervalId);
    };
  }, [startSignal]);

  const secondsLeft = useMemo(() => {
    if (roundEnded) {
      return 0;
    }

    const elapsedSeconds = Math.floor((tick - startedAt) / 1000);
    return Math.max(0, GAME_DURATION_SECONDS - elapsedSeconds);
  }, [roundEnded, tick, startedAt]);

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
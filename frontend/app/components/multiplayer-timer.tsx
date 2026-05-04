"use client";

import { useEffect, useMemo, useRef, useState } from "react";

const GAME_DURATION_SECONDS = 3 * 60;

interface MultiplayerTimerProps {
  roundEnded: boolean;
  startSignal: number;
  /** Fired once when the countdown reaches zero (unless round already ended externally). */
  onRoundEnded?: () => void;
}

export default function MultiplayerTimer({
  roundEnded,
  startSignal,
  onRoundEnded,
}: MultiplayerTimerProps) {
  const baseRef = useRef(Date.now());
  const firedZeroRef = useRef(false);
  const [tick, setTick] = useState(() => Date.now());

  useEffect(() => {
    baseRef.current = Date.now();
    firedZeroRef.current = false;
    setTick(Date.now());
  }, [startSignal]);

  useEffect(() => {
    const intervalId = setInterval(() => {
      setTick(Date.now());
    }, 1000);

    return () => clearInterval(intervalId);
  }, [startSignal]);

  const secondsLeft = useMemo(() => {
    if (roundEnded) {
      return 0;
    }
    const elapsedSeconds = Math.floor((tick - baseRef.current) / 1000);
    return Math.max(0, GAME_DURATION_SECONDS - elapsedSeconds);
  }, [roundEnded, tick]);

  useEffect(() => {
    if (roundEnded || secondsLeft > 0 || firedZeroRef.current || !onRoundEnded) {
      return;
    }
    firedZeroRef.current = true;
    onRoundEnded();
  }, [roundEnded, secondsLeft, onRoundEnded]);

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

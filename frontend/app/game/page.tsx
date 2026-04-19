"use client";

import Image from "next/image";
import { useSearchParams } from "next/navigation";
import { useState, useCallback, useRef } from "react";
import BoggleBoard, { BoggleBoardHandle } from "../components/boggle-board";
import WordInput from "../components/word-input";
import ScoreDisplay from "../components/score-display";
import Timer from "../components/timer";
import useWordVerification from "../components/use-word-verification";

export default function Home() {
  const searchParams = useSearchParams();
  const duration = Number(searchParams.get("duration")) || 180;
  const boardSize = Number(searchParams.get("boardSize")) || 5;

  const defaultBoard = [
    ["T", "E", "S", "T", "S"],
    ["W", "O", "R", "D", "S"],
    ["G", "A", "M", "E", "S"],
    ["P", "L", "A", "Y", "S"],
    ["B", "O", "G", "G", "L"],
  ];

  const [finalScore, setFinalScore] = useState(0);
  const [gameActive, setGameActive] = useState(false);
  const [board, setBoard] = useState<string[][] | null>(null);
  const { submittedWords, verifyWord, resetWords, currentScore } =
    useWordVerification();
  const boggleBoardRef = useRef<BoggleBoardHandle>(null);

  function generateBoard() {
    console.log("generating board with size:", boardSize);
    fetch(`/api/generate?size=${boardSize}`)
        .then((res) => res.json())
        .then((b) => {
          setBoard(b);
          console.log(b);
        })
        .catch((error) => console.error("Error: ", error));
  }

  /**
   * Called when the timer hits 0 or the player clicks End.
   * Hides the word input and clears the submitted word list.
   */
  const handleGameEnd = useCallback(() => {
    setGameActive(false);
    console.log("handleGameEnd fired, currentScore:", currentScore);
    setFinalScore(currentScore); // capture before resetWords clears it

    const username = window.sessionStorage.getItem("username");
    if (username) {
      fetch(
          `/api/user/score?username=${encodeURIComponent(username)}&score=${currentScore}`,
          { method: "PUT" },
      ).catch((err) => console.error("Score submit failed:", err));
    }

    resetWords();
  }, [resetWords, currentScore]);

  /**
   * Called on initial mount and when the player clicks Play Again.
   * Shows the word input and resets the submitted word list for a fresh game.
   */
  const handleGameStart = useCallback(() => {
    generateBoard();
    setGameActive(true);
    resetWords();
  }, [resetWords, generateBoard]);

  const handleMouseDown = useCallback((e: React.MouseEvent) => {
    boggleBoardRef.current?.handleMouseDown(e);
  }, []);

  const handleMouseUp = useCallback((e: React.MouseEvent) => {
    boggleBoardRef.current?.handleMouseUp(e);
  }, []);

  return (
    <div
      className="flex min-h-screen items-center justify-center bg-amber-100 font-sans dark:bg-amber-100"
      onMouseDown={handleMouseDown}
      onMouseUp={handleMouseUp}
    >
      <main className="flex min-h-screen w-full max-w-3xl mx-auto flex-col items-center justify-center py-32 px-16 bg-amber-100 dark:bg-amber-100">
        <Timer
            onGameStart={handleGameStart}
            onGameEnd={handleGameEnd}
            finalScore={finalScore}
            durationSeconds={duration}
        />

        {board && (
            <BoggleBoard
                ref={boggleBoardRef}
                submittedWords={submittedWords}
                verifyWord={verifyWord}
                gameActive={gameActive}
                board={board}
            />
        )}

        {gameActive && (
            <>
              <WordInput submittedWords={submittedWords} />
              <ScoreDisplay submittedWords={submittedWords} />
            </>
        )}
      </main>
    </div>
  );
}

"use client";

import Image from "next/image";
import { useState, useCallback } from "react";
import BoggleBoard from "../components/boggle-board";
import WordInput from "../components/word-input";
import Timer from "../components/timer";
import useWordVerification from "../components/use-word-verification";
import { request } from "http";

export default function Home() {
  const defaultBoard = [
    ["T", "E", "S", "T", "S"], // 1
    ["W", "O", "R", "D", "S"], // 2
    ["G", "A", "M", "E", "S"], // 3
    ["P", "L", "A", "Y", "S"], // 4
    ["B", "O", "G", "G", "L"], // 5
  ];

  const [gameActive, setGameActive] = useState(false);
  const [board, setBoard] = useState(defaultBoard);
  const { submittedWords, verifyWord, loading, resetWords } =
    useWordVerification();

  /**
   * Called when the timer hits 0 or the player clicks End.
   * Hides the word input and clears the submitted word list.
   */
  const handleGameEnd = useCallback(() => {
    setGameActive(false);
    resetWords();
  }, [resetWords]);

  /**
   * Called on initial mount and when the player clicks Play Again.
   * Shows the word input and resets the submitted word list for a fresh game.
   */
  const handleGameStart = useCallback(() => {
    generateBoard();
    setGameActive(true);
    resetWords();
  }, [resetWords]);

  const onGameStart = () => {
    generateBoard().then(() => {
      setGameActive(true);
    });
  };

  const onGameEnd = () => {
    setGameActive(false);
  };

  async function generateBoard() {
    fetch("http://localhost:8080/api/generate")
      .then((res) => res.json())
      .then((b) => {
        setBoard(b);
        console.log(b);
      })
      .catch((error) => console.error("Error: ", error));
  }
  console.log(board);
  return (
    <div className="flex min-h-screen items-center justify-center bg-zinc-50 font-sans dark:bg-black">
      <main className="flex min-h-screen w-full max-w-3xl mx-auto flex-col items-center justify-center py-32 px-16 bg-white dark:bg-black">
        <Image
          src="/LOGO.png"
          width={100}
          height={100}
          alt="Logo Image"
        ></Image>

        <Timer onGameStart={handleGameStart} onGameEnd={handleGameEnd} />

        <BoggleBoard
          submittedWords={submittedWords}
          verifyWord={verifyWord}
          gameActive={gameActive}
          board={board}
        />

        {gameActive && <WordInput submittedWords={submittedWords} />}
      </main>
    </div>
  );
}

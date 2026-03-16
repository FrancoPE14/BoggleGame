"use client";

import Image from "next/image";
import { useState } from "react";
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
  const { submittedWords, verifyWord, loading } = useWordVerification();
  const [board, setBoard] = useState(defaultBoard);

  const onGameStart = () => {
    generateBoard().then(() => {
      setGameActive(true);
    });
  };

  const onGameEnd = () => {
    setGameActive(false);
  };

  async function generateBoard() {
    try {
      const res = await fetch("http://localhost:8080/api/generate");
      const b = await res.json();
      setBoard(b);
      console.log(b);
    } catch {
      return;
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-zinc-50 font-sans dark:bg-black">
      <main className="flex min-h-screen w-full max-w-3xl mx-auto flex-col items-center justify-center py-32 px-16 bg-white dark:bg-black">
        <Image
          src="/LOGO.png"
          width={100}
          height={100}
          alt="Logo Image"
        ></Image>

        <Timer onGameStart={onGameStart} onGameEnd={onGameEnd} />

        <BoggleBoard
          submittedWords={submittedWords}
          verifyWord={verifyWord}
          gameActive={gameActive}
          board={board}
        />

        <WordInput submittedWords={submittedWords} />
      </main>
    </div>
  );
}

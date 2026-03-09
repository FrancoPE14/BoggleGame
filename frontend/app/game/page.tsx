"use client";

import Image from "next/image";
import { useState } from "react";
import BoggleBoard from "../components/boggle-board";
import WordInput from "../components/word-input";
import Timer from "../components/timer";
import useWordVerification from "../components/use-word-verification";

export default function Home() {
  const [gameActive, setGameActive] = useState(false);
  const { submittedWords, verifyWord, loading } = useWordVerification();

  return (
    <div className="flex min-h-screen items-center justify-center bg-zinc-50 font-sans dark:bg-black">
      <main className="flex min-h-screen w-full max-w-3xl mx-auto flex-col items-center justify-center py-32 px-16 bg-white dark:bg-black">
        <Image
          src="/LOGO.png"
          width={100}
          height={100}
          alt="Logo Image"
        ></Image>

        <Timer
          onGameStart={() => setGameActive(true)}
          onGameEnd={() => setGameActive(false)}
        />

        <BoggleBoard submittedWords={submittedWords} verifyWord={verifyWord} />

        <WordInput
          submittedWords={submittedWords}
          verifyWord={verifyWord}
          loading={loading}
        />
      </main>
    </div>
  );
}

"use client";

import { useState } from "react";
import { useSearchParams } from "next/navigation";
import useGameStream from "@/app/components/use-game-stream";
import useMultiplayerWordSubmission from "@/app/components/use-multiplayer-word-submission";
import MultiplayerScoreDisplay from "@/app/components/multiplayer-score-display";

export default function MultiplayerGamePage() {
  const params = useSearchParams();
  const sessionId = Number(params.get("sessionId"));
  const username = params.get("username") || "user";

  const [board, setBoard] = useState<string[][] | null>(null);
  const [roundEnded, setRoundEnded] = useState(false);

  const {
    input,
    setInput,
    submitWord,
    score,
    words,
  } = useMultiplayerWordSubmission(sessionId, username);

  useGameStream({
    sessionId,
    onGameStarted: (data) => {
      setBoard(data.board.board);
    },
    onGameState: () => {},
    onRoundEnded: async () => {
      setRoundEnded(true);

      // finish ack
      await fetch(
        `http://localhost:8080/api/finish?sessionId=${sessionId}&username=${username}`,
        { method: "POST" }
      );
    },
  });

  if (!board) {
    return <div>Waiting for game to start...</div>;
  }

  return (
    <div className="flex flex-col items-center gap-4">
      <MultiplayerScoreDisplay score={score} />

      <div className="grid grid-cols-4 gap-2">
        {board.map((row, i) =>
          row.map((cell, j) => (
            <div
              key={`${i}-${j}`}
              className="w-12 h-12 border flex items-center justify-center text-lg"
            >
              {cell}
            </div>
          ))
        )}
      </div>

      {!roundEnded && (
        <div className="flex gap-2">
          <input
            value={input}
            onChange={(e) => setInput(e.target.value)}
            className="border p-2"
          />
          <button onClick={submitWord} className="border px-4">
            Submit
          </button>
        </div>
      )}

      <div>
        {words.map((w, i) => (
          <div key={i}>{w}</div>
        ))}
      </div>

      {roundEnded && <div>Round Ended</div>}
    </div>
  );
}
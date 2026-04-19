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
  const [result, setResult] = useState<any>(null);

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
      // backend: { board: { board: [][] } }
      setBoard(data.board.board);
    },

    onGameState: () => {
      // 필요하면 나중에 확장
    },

    onRoundEnded: async () => {
      setRoundEnded(true);

      // 🔥 finish ack
      await fetch(
        `http://localhost:8080/api/finish?sessionId=${sessionId}&username=${username}`,
        { method: "POST" }
      );
    },

    onGameResults: (data) => {
      setResult(data);
    },
  });

  if (!board) {
    return <div>Waiting for game to start...</div>;
  }

  return (
    <div className="flex flex-col items-center gap-4">

      {/* SCORE */}
      <MultiplayerScoreDisplay score={score} />

      {/* BOARD */}
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

      {/* INPUT */}
      {!roundEnded && (
        <div className="flex gap-2">
          <input
            value={input}
            onChange={(e) => setInput(e.target.value)}
            className="border p-2"
          />
          <button
            onClick={submitWord}
            className="border px-4"
          >
            Submit
          </button>
        </div>
      )}

      {/* WORD LIST */}
      <div>
        {words.map((w, i) => (
          <div key={i}>{w}</div>
        ))}
      </div>

      {/* ROUND END */}
      {roundEnded && !result && (
        <div className="text-lg">Waiting for results...</div>
      )}

      {/* RESULT */}
      {result && (
        <div className="flex flex-col items-center gap-2 mt-4 border p-4">
          <h2 className="text-xl font-bold">
            Winner: {result.winner}
          </h2>

          <div>
            {Object.entries(result.scores).map(([user, score]) => (
              <div key={user}>
                {user}: {score}
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
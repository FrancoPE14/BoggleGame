"use client";

import { useCallback, useState } from "react";
import { useSearchParams } from "next/navigation";
import useGameStream, {
  GameResultsEvent,
  GameStartedEvent,
  GameStateEvent,
} from "@/app/components/use-game-stream";
import useMultiplayerWordSubmission from "@/app/components/use-multiplayer-word-submission";
import MultiplayerScoreDisplay from "@/app/components/multiplayer-score-display";

export default function MultiplayerGamePage() {
  const params = useSearchParams();

  const sessionId = Number(params.get("sessionId"));
  const username = params.get("username") || "user";

  const [board, setBoard] = useState<string[][] | null>(null);
  const [roundEnded, setRoundEnded] = useState(false);
  const [result, setResult] = useState<GameResultsEvent | null>(null);

  const {
    input,
    setInput,
    submitWord,
    score,
    words,
  } = useMultiplayerWordSubmission(sessionId, username);

  const handleGameStarted = useCallback((data: GameStartedEvent) => {
    setBoard(data.board);
    setRoundEnded(false);
    setResult(null);
  }, []);

  const handleGameState = useCallback((_data: GameStateEvent) => {
    // useMultiplayerWordSubmission already updates local UI
    // from /api/submit-word responses, so nothing is needed here for now.
  }, []);

  const handleRoundEnded = useCallback(async () => {
    setRoundEnded(true);

    try {
      await fetch(
        `http://localhost:8080/api/finish?sessionId=${sessionId}&username=${username}`,
        { method: "POST" }
      );
    } catch (error) {
      console.error("Failed to acknowledge round finish:", error);
    }
  }, [sessionId, username]);

  const handleGameResults = useCallback((data: GameResultsEvent) => {
    setResult(data);
  }, []);

  useGameStream({
    sessionId,
    onGameStarted: handleGameStarted,
    onGameState: handleGameState,
    onRoundEnded: handleRoundEnded,
    onGameResults: handleGameResults,
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
        {words.map((word, index) => (
          <div key={index}>{word}</div>
        ))}
      </div>

      {roundEnded && !result && (
        <div className="text-lg">Waiting for results...</div>
      )}

      {result && (
        <div className="mt-4 flex flex-col items-center gap-2 border p-4">
          <h2 className="text-xl font-bold">Winner: {result.winner}</h2>

          <div>
            {Object.entries(result.scores).map(([user, playerScore]) => (
              <div key={user}>
                {user}: {playerScore}
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
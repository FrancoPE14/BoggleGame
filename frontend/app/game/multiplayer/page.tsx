"use client";

import { useCallback, useRef, useState } from "react";
import { useSearchParams } from "next/navigation";
import BoggleBoard, { BoggleBoardHandle } from "../../components/boggle-board";
import WordInput from "../../components/word-input";
import ScoreDisplay from "../../components/score-display";
import useGameStream, {
  GameStartedEvent,
  GameResultsEvent,
} from "../../components/use-game-stream";

export default function MultiplayerPage() {
  const params = useSearchParams();
  const sessionId = Number(params.get("sessionId"));
  const username = params.get("username") || "user";

  const defaultBoard = [
    ["T", "E", "S", "T", "S"],
    ["W", "O", "R", "D", "S"],
    ["G", "A", "M", "E", "S"],
    ["P", "L", "A", "Y", "S"],
    ["B", "O", "G", "G", "L"],
  ];

  const [gameActive, setGameActive] = useState(false);
  const [roundEnded, setRoundEnded] = useState(false);
  const [board, setBoard] = useState<string[][]>(defaultBoard);
  const [submittedWords, setSubmittedWords] = useState<string[]>([]);
  const [currentScore, setCurrentScore] = useState(0);
  const [result, setResult] = useState<GameResultsEvent | null>(null);

  const boggleBoardRef = useRef<BoggleBoardHandle>(null);

  const resetRoundState = useCallback(() => {
    setSubmittedWords([]);
    setCurrentScore(0);
    setResult(null);
    setRoundEnded(false);
  }, []);

  const verifyWord = useCallback(
    async (word: string) => {
      if (!gameActive || roundEnded) {
        return;
      }

      try {
        const response = await fetch(
          `http://localhost:8080/api/submit-word?sessionId=${sessionId}&username=${username}&word=${encodeURIComponent(word)}`,
          { method: "POST" }
        );

        if (!response.ok) {
          throw new Error("Failed to submit word");
        }

        const data = await response.json();

        setSubmittedWords(data.acceptedWords ?? []);
        setCurrentScore(data.currentScore ?? 0);

        return data;
      } catch (error) {
        console.error("Failed to submit multiplayer word", error);
      }
    },
    [gameActive, roundEnded, sessionId, username]
  );

  const handleGameStarted = useCallback(
    (data: GameStartedEvent) => {
      setBoard(data.board);
      resetRoundState();
      setGameActive(true);
    },
    [resetRoundState]
  );

  const handleRoundEnded = useCallback(async () => {
    setRoundEnded(true);
    setGameActive(false);

    try {
      await fetch(
        `http://localhost:8080/api/finish?sessionId=${sessionId}&username=${username}`,
        { method: "POST" }
      );
    } catch (error) {
      console.error("Failed to acknowledge round finish", error);
    }
  }, [sessionId, username]);

  const handleGameResults = useCallback((data: GameResultsEvent) => {
    setResult(data);
  }, []);

  useGameStream({
    sessionId,
    onGameStarted: handleGameStarted,
    onRoundEnded: handleRoundEnded,
    onGameResults: handleGameResults,
  });

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
      <main className="mx-auto flex min-h-screen w-full max-w-3xl flex-col items-center justify-center bg-amber-100 px-16 py-32 dark:bg-amber-100">
        {!gameActive && !roundEnded && !result && (
          <div className="mb-6 text-xl font-semibold">
            Waiting for game to start...
          </div>
        )}

        {roundEnded && !result && (
          <div className="mb-6 text-xl font-semibold">
            Waiting for results...
          </div>
        )}

        {result && (
          <div className="mb-6 flex flex-col items-center gap-2">
            <div className="text-2xl font-bold">
              Winner: {result.winner}
            </div>
            <div className="flex flex-col items-center text-lg">
              {Object.entries(result.scores).map(([player, score]) => (
                <div key={player}>
                  {player}: {score}
                </div>
              ))}
            </div>
          </div>
        )}

        <BoggleBoard
          ref={boggleBoardRef}
          submittedWords={submittedWords}
          verifyWord={verifyWord}
          gameActive={gameActive}
          board={board}
        />

        {gameActive && (
          <>
            <WordInput submittedWords={submittedWords} />
            <ScoreDisplay
              submittedWords={submittedWords}
              currentScore={currentScore}
            />
          </>
        )}
      </main>
    </div>
  );
}
"use client";

import { useCallback, useRef, useState } from "react";
import { useSearchParams } from "next/navigation";
import BoggleBoard, { BoggleBoardHandle } from "../../components/boggle-board";
import WordInput from "../../components/word-input";
import ScoreDisplay from "../../components/score-display";
import MultiplayerTimer from "../../components/multiplayer-timer";
import useWordVerification from "../../components/use-word-verification";
import useGameStream, {
  GameStartedEvent,
  GameResultsEvent,
} from "../../components/use-game-stream";

export default function MultiplayerPage() {
  const params = useSearchParams();
  const sessionId = Number(params.get("sessionId"));
  const username = params.get("username") || "user";

  const [gameStarted, setGameStarted] = useState(false);
  const [roundEnded, setRoundEnded] = useState(false);
  const [board, setBoard] = useState<string[][] | null>(null);
  const [result, setResult] = useState<GameResultsEvent | null>(null);
  const [startSignal, setStartSignal] = useState(0);

  const { submittedWords, verifyWord, resetWords, currentScore } =
    useWordVerification({ sessionId, username });

  const boggleBoardRef = useRef<BoggleBoardHandle>(null);

  const handleGameStarted = useCallback(
    (data: GameStartedEvent) => {
      setBoard(data.board);
      setGameStarted(true);
      setRoundEnded(false);
      setResult(null);
      setStartSignal((prev) => prev + 1);
      resetWords();
    },
    [resetWords]
  );

  const handleRoundEnded = useCallback(async () => {
    setRoundEnded(true);
    setGameStarted(false);

    try {
      await fetch(
        `http://163.192.206.210:8080/api/finish?sessionId=${sessionId}&username=${username}`,
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
    //onRoundEnded: handleRoundEnded,
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
        {gameStarted && (
          <MultiplayerTimer
            gameStarted={gameStarted}
            roundEnded={roundEnded}
            startSignal={startSignal}
            onRoundEnded={handleRoundEnded}
          />
        )}

        {!board && (
          <div className="mt-6 text-xl font-semibold">
            Waiting for game to start...
          </div>
        )}

        {board && (
          <BoggleBoard
            ref={boggleBoardRef}
            submittedWords={submittedWords}
            verifyWord={verifyWord}
            gameActive={gameStarted}
            board={board}
          />
        )}

        {board && gameStarted && (
          <>
            <WordInput submittedWords={submittedWords} />
            <ScoreDisplay
              submittedWords={submittedWords}
              currentScore={currentScore}
            />
          </>
        )}

        {roundEnded && !result && (
          <div className="mt-6 text-xl font-semibold">
            Waiting for results...
          </div>
        )}

        {result && (
          <div className="mt-6 flex flex-col items-center gap-2">
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
      </main>
    </div>
  );
}
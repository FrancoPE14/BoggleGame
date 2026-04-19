"use client";

import { useCallback, useRef, useState } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import BoggleBoard, { BoggleBoardHandle } from "../../components/boggle-board";
import WordInput from "../../components/word-input";
import ScoreDisplay from "../../components/score-display";
import MultiplayerTimer from "../../components/multiplayer-timer";
import useWordVerification from "../../components/use-word-verification";
import useGameStream, {
  GameStartedEvent,
  GameResultsEvent,
  GameStateEvent,
  GameEndedEvent,
} from "../../components/use-game-stream";
import type { FinalScore } from "../../components/use-game-stream";
import GameOver from "../../components/game-over";

export default function MultiplayerPage() {
  const params = useSearchParams();
  const sessionId = Number(params.get("sessionId"));
  const username = params.get("username") || "user";
  const router = useRouter();

  const [gameStarted, setGameStarted] = useState(false);
  const [roundEnded, setRoundEnded] = useState(false);
  const [board, setBoard] = useState<string[][] | null>(null);
  const [result, setResult] = useState<GameResultsEvent | null>(null);
  const [startSignal, setStartSignal] = useState(0);
  const [finalScores, setFinalScores] = useState<FinalScore[] | null>(null);
  const [liveScore, setLiveScore] = useState(0);
  const [sessionEnded, setSessionEnded] = useState(false);

  const { submittedWords, verifyWord, resetWords, currentScore } =
    useWordVerification({ sessionId, username });

  const boggleBoardRef = useRef<BoggleBoardHandle>(null);

  const handleGameStarted = useCallback(
    (data: GameStartedEvent) => {
      setBoard(data.board);
      setGameStarted(true);
      setRoundEnded(false);
      setResult(null);
      setSessionEnded(false);
      setLiveScore(0);
      setStartSignal((prev) => prev + 1);
      resetWords();
    },
    [resetWords]
  );

  const handleRoundEnded = useCallback(async () => {
    setRoundEnded(true);
    setGameStarted(false);

    try {
      const res = await fetch(
        `http://163.192.206.210:8080/api/finish?sessionId=${sessionId}&username=${encodeURIComponent(username)}`,
        { method: "POST" }
      );

      if (!res.ok) {
        const text = await res.text();
        throw new Error(`Finish failed: ${res.status} ${text}`);
      }
    } catch (error) {
      console.error("Failed to acknowledge round finish", error);
    }
  }, [sessionId, username]);

  const handleGameState = useCallback(
    (data: GameStateEvent) => {
      if (data.username === username) {
        setLiveScore(data.currentScore);
      }
    },
    [username]
  );

  const handleGameResults = useCallback((data: GameResultsEvent) => {
    setResult(data);
  }, []);

  const handleGameEnded = useCallback((data: GameEndedEvent) => {
    console.log("Session ended:", data.sessionId);
    setFinalScores(data.finalScores);
    setSessionEnded(true);
  }, []);

  useGameStream({
    sessionId,
    onGameStarted: handleGameStarted,
    onGameState: handleGameState,
    onGameResults: handleGameResults,
    onGameEnded: handleGameEnded,
  });

  const handleMouseDown = useCallback((e: React.MouseEvent) => {
    boggleBoardRef.current?.handleMouseDown(e);
  }, []);

  const handleMouseUp = useCallback((e: React.MouseEvent) => {
    boggleBoardRef.current?.handleMouseUp(e);
  }, []);

  const displayedScore =
    result?.scores?.[username] ?? liveScore ?? currentScore;

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
              currentScore={displayedScore}
            />
          </>
        )}

        {roundEnded && !result && (
          <div className="mt-6 text-xl font-semibold">
            Waiting for results...
          </div>
        )}

        {finalScores && (
            <GameOver
                onPlayAgain={() => router.push("/")}
                finalScore={currentScore}
                finalScores={finalScores}
                isMultiplayer={true}
            />
        )}

        {sessionEnded && (
          <div className="mt-4 text-sm font-medium">
            Session ended.
          </div>
        )}
      </main>
    </div>
  );
}
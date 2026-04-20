"use client";

import { useCallback, useRef, useState } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import BoggleBoard, { BoggleBoardHandle } from "../../components/boggle-board";
import WordInput from "../../components/word-input";
import ScoreDisplay from "../../components/score-display";
import MultiplayerTimer from "../../components/multiplayer-timer";
import useMultiplayerWordSubmission from "../../components/use-multiplayer-word-submission";
import useGameStream, {
  GameStartedEvent,
  GameResultsEvent,
  GameStateEvent,
  GameEndedEvent,
} from "../../components/use-game-stream";
import type { FinalScore } from "../../components/use-game-stream";
import GameOver from "../../components/game-over";

/**
 * Multiplayer gameplay page.
 *
 * For score updates, this page must use the multiplayer submission hook rather
 * than the single-player verification hook. That ensures each submitted word
 * goes through the backend scoring pipeline and the score shown in the UI
 * reflects the authoritative server state.
 */
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

  /**
   * Multiplayer pages must submit words through /api/submit-word so the backend
   * can update ScoreTracker and return the correct currentScore value.
   */
  const { submittedWords, verifyWord, resetWords, currentScore } =
    useMultiplayerWordSubmission(sessionId, username);

  const boggleBoardRef = useRef<BoggleBoardHandle>(null);

  /**
   * Initializes local UI state when the server broadcasts the start of a new game.
   */
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
    [resetWords],
  );

  /**
   * Acknowledges round completion to the backend when the local timer ends.
   *
   * This logic is left unchanged here because the requested fix is limited to
   * score updates. The finish call is still retained to preserve the existing
   * end-of-round behavior in this branch.
   */
  const handleRoundEnded = useCallback(async () => {
    setRoundEnded(true);
    setGameStarted(false);

    try {
      const res = await fetch(
        `http://163.192.206.210:8080/api/finish?sessionId=${sessionId}&username=${encodeURIComponent(username)}`,
        { method: "POST" },
      );

      if (!res.ok) {
        const text = await res.text();
        throw new Error(`Finish failed: ${res.status} ${text}`);
      }
    } catch (error) {
      console.error("Failed to acknowledge round finish", error);
    }
  }, [sessionId, username]);

  /**
   * Updates the visible score when the backend broadcasts a new game-state event
   * for the current player.
   */
  const handleGameState = useCallback(
    (data: GameStateEvent) => {
      if (data.username === username) {
        setLiveScore(data.currentScore);
      }
    },
    [username],
  );

  /**
   * Stores final result data when the backend publishes end-of-round results.
   */
  const handleGameResults = useCallback((data: GameResultsEvent) => {
    setResult(data);
  }, []);

  /**
   * Stores final score ordering when the backend ends the session.
   */
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

  /**
   * Prefers the final result score when available. Otherwise, it uses the latest
   * live score from SSE and falls back to the local hook state as needed.
   */
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

        {finalScores && (
          <GameOver
            onPlayAgain={() => router.push("/")}
            finalScore={currentScore}
            finalScores={finalScores}
            isMultiplayer={true}
          />
        )}
      </main>
    </div>
  );
}
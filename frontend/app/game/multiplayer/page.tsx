"use client";

import { useCallback, useEffect, useRef, useState } from "react";
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
import type {
  FinalScore,
  LobbyUpdateEvent,
} from "../../components/use-game-stream";
import GameOver from "../../components/game-over";
import PlayerList from "../../components/player-list";

/**
 * Represents a single player in the lobby.
 */
type LobbyPlayer = {
  username: string;
  isCurrentUser: boolean;
};

interface Lobby {
  sessionId: number;
  started: boolean;
  playerCount: number;
  maxPlayers: number;
  hostUsername: string;
}

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
  const [players, setPlayers] = useState<LobbyPlayer[]>([
    { username, isCurrentUser: true },
  ]);
  const [hostUsername, setHostUsername] = useState("");

  useEffect(() => {
    if (isNaN(sessionId)) return;

    fetch(`/api/session/players?sessionId=${sessionId}`, {
      method: "GET",
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to load session.");
        return res.json();
      })
      .then((data) => {
        console.log(data);
        setHostUsername(data.hostUsername);

        const currentUsername = window.sessionStorage
          .getItem("username")
          ?.trim();
        const playerList: LobbyPlayer[] = data.playerList.map((u: string) => ({
          username: u,
          isCurrentUser: u === currentUsername,
        }));
        setPlayers(playerList);
      })
      .catch(() => {
        console.error("Could not load session. Please try again later.");
      });
  }, [sessionId]);

  const onStartButtonClicked = () => {
    fetch(
      `http://localhost:8080/api/start?sessionId=${sessionId}&username=${encodeURIComponent(username)}`,
      { method: "POST" },
    ).catch((err) => console.error(err));
  };

  // TODO: Leave button
  const onLeaveButtonClicked = () => {};

  const handleLobbyUpdate = useCallback((data: LobbyUpdateEvent) => {
    console.log("Lobby Updated:", data);
    const playerList: LobbyPlayer[] = data.playerList.map((user) => ({
      username: user,
      isCurrentUser: user === username,
    }));
    setPlayers(playerList);
  }, []);

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
        `http://localhost:8080/api/finish?sessionId=${sessionId}&username=${encodeURIComponent(username)}`,
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
    onLobbyUpdate: handleLobbyUpdate,
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

        {/*Before game start*/}
        {!board && (
          <div className="mt-6 text-xl font-semibold flex flex-col items-center">
            <h2>Waiting for game to start...</h2>
            {/* Player list */}
            <div className="w-full max-w-2xl bg-amber-50 rounded-2xl border border-amber-200 shadow-sm p-6 mb-6 items-center">
              <PlayerList players={players} />
            </div>
            {/*Start Button*/}
            {hostUsername === username && (
              <button
                type="button"
                onClick={onStartButtonClicked}
                //disabled={!canStart}
                className="px-8 py-3 rounded-xl bg-amber-500 text-white font-bold text-lg shadow-md hover:bg-amber-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                aria-label="Start the multiplayer game"
              >
                Start Game
              </button>
            )}
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

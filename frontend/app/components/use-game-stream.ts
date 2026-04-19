import { useEffect } from "react";

export type GameStartedEvent = {
  sessionId: number;
  board: string[][];
};

export type GameStateEvent = {
  sessionId: number;
  username: string;
  originalWord: string;
  normalizedWord: string;
  accepted: boolean;
  duplicate: boolean;
  valid: boolean;
  pointsAwarded: number;
  currentScore: number;
  acceptedWords: string[];
};

export type GameResultsEvent = {
  sessionId: number;
  scores: Record<string, number>;
  winner: string;
};

type Props = {
  sessionId: number;
  onGameStarted?: (data: GameStartedEvent) => void;
  onGameState?: (data: GameStateEvent) => void;
  onRoundEnded?: () => void;
  onGameResults?: (data: GameResultsEvent) => void;
};

export default function useGameStream({
  sessionId,
  onGameStarted,
  onGameState,
  onRoundEnded,
  onGameResults,
}: Props) {
  useEffect(() => {
    if (Number.isNaN(sessionId)) return;

    const es = new EventSource(
      `http://localhost:8080/api/game/stream?sessionId=${sessionId}`
    );

    es.addEventListener("connected", (e: MessageEvent) => {
      console.log("SSE connected:", e.data);
    });

    es.addEventListener("game-started", (e: MessageEvent) => {
      const data: GameStartedEvent = JSON.parse(e.data);
      console.log("game-started received:", data);
      onGameStarted?.(data);
    });

    es.addEventListener("game-state", (e: MessageEvent) => {
      const data: GameStateEvent = JSON.parse(e.data);
      onGameState?.(data);
    });

    es.addEventListener("round-ended", () => {
      console.log("round-ended received");
      onRoundEnded?.();
    });

    es.addEventListener("game-results", (e: MessageEvent) => {
      const data: GameResultsEvent = JSON.parse(e.data);
      console.log("game-results received:", data);
      onGameResults?.(data);
    });

    es.onerror = (event) => {
      console.error("SSE connection error", event);
    };

    return () => {
      es.close();
    };
  }, [sessionId, onGameStarted, onGameState, onRoundEnded, onGameResults]);
}
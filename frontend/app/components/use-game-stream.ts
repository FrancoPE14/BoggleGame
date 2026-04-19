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

    es.addEventListener("game-started", (e: MessageEvent) => {
      const data: GameStartedEvent = JSON.parse(e.data);
      onGameStarted?.(data);
    });

    es.addEventListener("game-state", (e: MessageEvent) => {
      const data: GameStateEvent = JSON.parse(e.data);
      onGameState?.(data);
    });

    es.addEventListener("round-ended", () => {
      onRoundEnded?.();
    });

    es.addEventListener("game-results", (e: MessageEvent) => {
      const data: GameResultsEvent = JSON.parse(e.data);
      onGameResults?.(data);
    });

    es.onerror = () => {
      console.error("SSE connection error");
      es.close();
    };

    return () => {
      es.close();
    };
  }, [sessionId, onGameStarted, onGameState, onRoundEnded, onGameResults]);
}
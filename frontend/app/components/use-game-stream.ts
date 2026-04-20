"use client";

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

export type GameEndedEvent = {
  sessionId: number;
};

export type LobbyUpdateEvent = {
  sessionId: number;
  started: boolean;
  playerCount: number;
  maxPlayers: number;
  hostUsername: string;
  joinedUsername: string;
  players?: string[];
  playerList?: string[];
};

type Props = {
  sessionId: number;
  onGameStarted?: (data: GameStartedEvent) => void;
  onGameState?: (data: GameStateEvent) => void;
  onGameResults?: (data: GameResultsEvent) => void;
  onGameEnded?: (data: GameEndedEvent) => void;
  onLobbyUpdate?: (data: LobbyUpdateEvent) => void;
};

export default function useGameStream({
  sessionId,
  onGameStarted,
  onGameState,
  onGameResults,
  onGameEnded,
  onLobbyUpdate,
}: Props) {
  useEffect(() => {
    if (Number.isNaN(sessionId)) return;

    const es = new EventSource(
      `http://163.192.206.210:8080/api/game/stream?sessionId=${sessionId}`
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
      console.log("game-state received:", data);
      onGameState?.(data);
    });

    es.addEventListener("game-results", (e: MessageEvent) => {
      const data: GameResultsEvent = JSON.parse(e.data);
      console.log("game-results received:", data);
      onGameResults?.(data);
    });

    es.addEventListener("game-ended", (e: MessageEvent) => {
      const data: GameEndedEvent = JSON.parse(e.data);
      console.log("game-ended received:", data);
      onGameEnded?.(data);
    });

    es.addEventListener("lobby-update", (e: MessageEvent) => {
      const data: LobbyUpdateEvent = JSON.parse(e.data);
      console.log("lobby-update received:", data);
      onLobbyUpdate?.(data);
    });

    es.onerror = (event) => {
      console.error("SSE connection error", event);
    };

    return () => {
      es.close();
    };
  }, [
    sessionId,
    onGameStarted,
    onGameState,
    onGameResults,
    onGameEnded,
    onLobbyUpdate,
  ]);
}
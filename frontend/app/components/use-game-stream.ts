import { useEffect } from "react";

type Props = {
  sessionId: number;
  onGameStarted?: (data: any) => void;
  onGameState?: (data: any) => void;
  onRoundEnded?: () => void;
  onGameResults?: (data: any) => void;
};

export default function useGameStream({
  sessionId,
  onGameStarted,
  onGameState,
  onRoundEnded,
  onGameResults,
}: Props) {
  useEffect(() => {
    if (!sessionId) return;

    const es = new EventSource(
      `http://localhost:8080/api/game/stream?sessionId=${sessionId}`
    );

    es.addEventListener("game-started", (e) => {
      const data = JSON.parse(e.data);
      onGameStarted?.(data);
    });

    es.addEventListener("game-state", (e) => {
      const data = JSON.parse(e.data);
      onGameState?.(data);
    });

    es.addEventListener("round-ended", () => {
      onRoundEnded?.();
    });

    es.addEventListener("game-results", (e) => {
      const data = JSON.parse(e.data);
      onGameResults?.(data);
    });

    es.onerror = () => {
      console.error("SSE connection error");
      es.close();
    };

    return () => {
      es.close();
    };
  }, [sessionId]);
}
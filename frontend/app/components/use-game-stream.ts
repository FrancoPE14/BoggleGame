import { useEffect } from "react";

type Props = {
  sessionId: number;
  onGameStarted?: (data: any) => void;
  onGameState?: (data: any) => void;
  onRoundEnded?: () => void;
};

export default function useGameStream({
  sessionId,
  onGameStarted,
  onGameState,
  onRoundEnded,
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

    return () => {
      es.close();
    };
  }, [sessionId]);
}
"use client";

import { useEffect, useRef, useState } from "react";

export default function useGameStream() {
  const [connected, setConnected] = useState(false);
  const eventSourceRef = useRef<EventSource | null>(null);

  useEffect(() => {
    const es = new EventSource("http://localhost:8080/api/game/stream");
    eventSourceRef.current = es;

    es.addEventListener("connected", () => {
      console.log("SSE connected");
      setConnected(true);
    });

    es.addEventListener("test", (event) => {
      const data = JSON.parse((event as MessageEvent).data);
      console.log("TEST EVENT:", data);
    });

    es.onerror = () => {
      console.log("SSE error → reconnecting...");
      setConnected(false);
      es.close();


      setTimeout(() => {
        window.location.reload();
      }, 2000);
    };

    return () => {
      es.close();
    };
  }, []);

  return { connected };
}
import LobbyCard from "./components/lobby-card";

import { useEffect } from "react";

export default function LobbyPage() {
  useEffect(() => {
    fetch("/api/sessions")
      .then((res) => {
        if (!res.ok) throw new Error("Failed to load session.");
        return res.json();
      })
      .then((data) => {
        console.log(data);
      })
      .catch(() => {
        console.error("Could not load session. Please try again later.");
      });
  }, []);

  return (
    <div className="flex min-h-screen min-w-screen w-full max-w-3xl mx-auto flex-col items-center justify-start p-5 bg-amber-100 dark:bg-amber-100">
      <h1>Lobbies</h1>
      <LobbyCard
        sessionId={1234}
        lobbyname={"Lobby 1"}
        numPlayers={3}
      ></LobbyCard>
    </div>
  );
}

"use client";

import LobbyCard from "./components/lobby-card";
import { useEffect, useState } from "react";

interface Lobby {
  sessionId: number;
  started: boolean;
  playerCount: number;
  maxPlayers: number;
  hostUsername: string;
}

export default function LobbyPage() {
  const [lobbies, setLobbies] = useState<Lobby[]>([]);

  useEffect(() => {
    fetch("/api/sessions")
      .then((res) => {
        if (!res.ok) throw new Error("Failed to load session.");
        return res.json();
      })
      .then((data) => {
        console.log(data);
        setLobbies(data);
      })
      .catch(() => {
        console.error("Could not load session. Please try again later.");
      });
  }, []);

  function enterLobby(sessionId: number) {
    console.log("Enter: " + sessionId);

    const username = window.sessionStorage.getItem("username");

    if (!username) {
      console.error("No username found in sessionStorage");
      return;
    }

    fetch(`/api/join?sessionId=${sessionId}&username=${encodeURIComponent(username)}`, {
        method: "POST",
      })
        .then(async (res) => {
          if (!res.ok) {
            const text = await res.text();
            throw new Error(`Failed to join lobby: ${res.status} ${text}`);
          }
          return res.json();
        })
        .then((data) => {
          console.log("Joined lobby:", data);
        })
        .catch((err) => {
          console.error("Error joining lobby:", err);
        });
  }

  return (
    <div className="flex min-h-screen min-w-screen w-full max-w-3xl mx-auto flex-col items-center justify-start p-5 bg-amber-100 dark:bg-amber-100">
      <h1 style={{ margin: 20 }}>Lobbies</h1>
      {lobbies.map((lobby) => (
        <LobbyCard
          key={lobby.sessionId}
          sessionId={lobby.sessionId}
          lobbyname={`Lobby ${lobby.sessionId}`}
          playerCount={lobby.playerCount}
          maxPlayers={lobby.maxPlayers}
          onClick={() => enterLobby(lobby.sessionId)}
        ></LobbyCard>
      ))}
    </div>
  );
}

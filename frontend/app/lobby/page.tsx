"use client";

import { useEffect, useState, useSyncExternalStore } from "react";
import { useRouter } from "next/navigation";
import PlayerList from "../components/player-list";

/**
 * Represents a single player in the lobby.
 */
type LobbyPlayer = {
  /** The player's display name. */
  username: string;
  /** Whether this player is the current user. */
  isCurrentUser: boolean;
};

/**
 * Builds the initial mock player list for the lobby.
 * Reads the current user's name from sessionStorage.
 *
 * @returns The initial array of lobby players.
 */
function buildInitialPlayers(): LobbyPlayer[] {
  const username =
    typeof window !== "undefined"
      ? window.sessionStorage.getItem("username")?.trim() || "You"
      : "You";

  // TODO: Replace mock player data with real lobby state from backend.
  // Depends on backend issues #179 (lobby endpoints) and #166 (multiplayer support).
  return [
    { username, isCurrentUser: true },
    { username: "Player2", isCurrentUser: false },
    { username: "Player3", isCurrentUser: false },
  ];
}

/** No-op subscribe for useSyncExternalStore hydration check. */
const emptySubscribe = (): (() => void) => () => {};

/**
 * Returns true on the client and false during SSR.
 * Used with useSyncExternalStore to safely detect hydration.
 */
const getClientSnapshot = (): boolean => true;

/**
 * Returns false during SSR to show the loading state.
 */
const getServerSnapshot = (): boolean => false;

/**
 * The multiplayer lobby page where players wait before starting a match.
 * Players see who else has joined and can mark themselves as ready.
 * The Start Game button activates when at least two players are present.
 *
 * @returns The rendered lobby page.
 */
export default function LobbyPage(): React.JSX.Element {
  const router = useRouter();
  const [players, setPlayers] = useState<LobbyPlayer[]>(buildInitialPlayers);
  const [sessionId, setSessionId] = useState<number>(NaN);

  const mounted = useSyncExternalStore(
    emptySubscribe,
    getClientSnapshot,
    getServerSnapshot,
  );

  // Fetch session id of player (given username)
  useEffect(() => {
    const username = window.sessionStorage.getItem("username")?.trim();
    if (!username) {
      throw new Error("No username found in session storage.");
    }

    fetch(`/api/session?username=${encodeURIComponent(username)}`)
      .then((res) => res.json())
      .then((id: number) => {
        if (id === -1) throw new Error("User is not in a session");
        setSessionId(id);
      })
      .catch(console.error);
  }, []);

  /**
   * Navigates to the game page to start the match.
   */
  const handleStartGame = (): void => {
    router.push("/game");
  };

  const canStart = players.length >= 2;
  const currentUser = players.find((p) => p.isCurrentUser);

  if (!mounted) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-amber-50">
        <p className="text-amber-700 text-lg font-medium">Loading lobby...</p>
      </div>
    );
  }

  return (
    <div className="flex flex-col items-center justify-start min-h-screen bg-amber-50 px-4 py-8">
      {/* Header */}
      <h1 className="text-3xl font-bold text-amber-700 mb-2">
        Multiplayer Lobby
      </h1>
      <p className="text-sm text-amber-600 mb-6">
        {canStart ? "Ready to start!" : "Waiting for players to join..."}
      </p>

      {/* Player list */}
      <div className="w-full max-w-2xl bg-white rounded-2xl border border-amber-200 shadow-sm p-6 mb-6">
        <PlayerList players={players} />
      </div>

      {/* Action area */}
      <div className="flex flex-col items-center gap-3 w-full max-w-2xl">
        <button
          type="button"
          onClick={handleStartGame}
          disabled={!canStart}
          className="px-8 py-3 rounded-xl bg-amber-500 text-white font-bold text-lg shadow-md hover:bg-amber-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          aria-label="Start the multiplayer game"
        >
          Start Game
        </button>

        {!canStart && (
          <p className="text-xs text-amber-600">
            Waiting for at least 2 players...
          </p>
        )}
      </div>
    </div>
  );
}

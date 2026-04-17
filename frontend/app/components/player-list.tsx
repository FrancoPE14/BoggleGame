"use client";

/**
 * Represents a single player in the lobby.
 */
type LobbyPlayer = {
    /** The player's display name. */
    username: string;
    /** Whether the player is ready to start the game. */
    ready: boolean;
    /** Whether this player is the current user. */
    isCurrentUser: boolean;
};

/**
 * Props for the PlayerList component.
 */
type PlayerListProps = {
    /** The list of players currently in the lobby. */
    players: LobbyPlayer[];
};

/**
 * Renders a list of players in the lobby with their ready status.
 * Each player is shown as a card with their username, a status indicator,
 * and a label distinguishing the current user from other players.
 *
 * @param props - Component props containing the player list.
 * @returns The rendered player list.
 */
export default function PlayerList({ players }: PlayerListProps): React.JSX.Element {
    if (players.length === 0) {
        return (
            <p className="text-center text-amber-600 py-8">
                No players in the lobby yet.
            </p>
        );
    }

    return (
        <ul className="flex flex-col gap-3" aria-label="Players in lobby">
            {players.map((player, index) => (
                <li
                    key={`${player.username}-${index}`}
                    className={`flex items-center justify-between px-4 py-3 rounded-xl border ${
                        player.isCurrentUser
                            ? "bg-amber-100 border-amber-400"
                            : "bg-white border-amber-200"
                    }`}
                >
                    <div className="flex items-center gap-3">
                        <span
                            className={`w-3 h-3 rounded-full ${
                                player.ready ? "bg-green-500" : "bg-amber-400"
                            }`}
                            aria-hidden="true"
                        />
                        <span className="font-medium text-amber-700">
                            {player.username}
                            {player.isCurrentUser && (
                                <span className="ml-2 text-xs text-amber-500">
                                    (you)
                                </span>
                            )}
                        </span>
                    </div>
                    <span
                        className={`text-sm font-semibold ${
                            player.ready ? "text-green-600" : "text-amber-600"
                        }`}
                    >
                        {player.ready ? "Ready" : "Waiting"}
                    </span>
                </li>
            ))}
        </ul>
    );
}

interface LobbyCardProps {
  sessionId: number;
  lobbyname: string;
  playerCount: number;
  maxPlayers: number;
  onClick: () => void;
}

export default function LobbyCard({
  sessionId,
  lobbyname,
  playerCount,
  maxPlayers,
  onClick,
}: LobbyCardProps) {
  return (
    <div
      className="w-full bg-gradient-to-r from-amber-300 to-amber-200 rounded-lg px-4 py-2 my-1 flex items-center justify-between shadow-md hover:shadow-lg transition-shadow cursor-pointer"
      onClick={onClick}
    >
      <h3 className="text-white text-sm font-semibold">{lobbyname}</h3>
      <div className="text-white text-sm font-bold">
        {playerCount} / {maxPlayers}
      </div>
    </div>
  );
}

interface LobbyCardProps {
  sessionId: number;
  lobbyname: string;
  numPlayers: number;
}

export default function LobbyCard({
  sessionId,
  lobbyname,
  numPlayers,
}: LobbyCardProps) {
  return (
    <div className="w-full bg-gradient-to-r from-amber-300 to-amber-200 rounded-lg px-6 py-4 flex items-center justify-between shadow-md hover:shadow-lg transition-shadow cursor-pointer">
      <h3 className="text-white text-lg font-semibold">{lobbyname}</h3>
      <div className="text-white text-xl font-bold">{numPlayers} / 4</div>
    </div>
  );
}

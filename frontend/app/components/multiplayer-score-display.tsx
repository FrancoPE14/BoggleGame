type Props = {
  score: number;
};

export default function MultiplayerScoreDisplay({ score }: Props) {
  return (
    <div className="text-xl font-bold">
      Score: {score}
    </div>
  );
}
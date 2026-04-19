export default function InformationRulesPage() {
  return (
    <div>

      <h2>About This Game</h2>
      <p>
        This game is a web verison of the 1973 Hasbro board game <a href = https://en.wikipedia.org/wiki/Boggle > Boggle </a>.
        Players compete in real time on the same letter board to find as many words before the round ends.
      </p>

      <h2>How To Play</h2>
      <ul>
        <li>A randomized board is created at the start of every game.</li>
        <li>All players use the same board during the round.</li>
        <li>Players find as many words as they can before time runs out.</li>
        <li>When the round ends, the player with the most points wins!.</li>
      </ul>

      <h2>How to Form Words</h2>
      <ul>
        <li>Click and drag to select letters to make words.</li>
        <li>Letters must be connected in sequence using adjacent tiles.</li>
        <li>Adjacent letters can also be diagonal!</li>
        <li>A letter can only be used once in the same word.</li>
        <li>All words must be at least 3 letter long.</li>
      </ul>

      <h2>Scoring</h2>
      <ul>
        <li>Each submitted word earns you points! The longer the word, the more points it earns.</li>
        <li>The same word cannot be scored more than once, even if it appears multiple times on the board.</li>
        <li>Plural forms of words can be used (for example, both 'badger' and 'badgers' would be valid)</li>
        <li>Final scores are based on valid words submitted during the round.</li>
      </ul>

      <h2>Multiplayer</h2>
      <ul>
        <li>You can play with other players on the same board!</li>
        <li>Select "play online" when setting up your game.</li>
      </ul>

    </div>
  );
}
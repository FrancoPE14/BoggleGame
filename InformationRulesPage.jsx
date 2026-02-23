export default function InformationRulesPage() {
  return (
    <div>
      <h1>Information &amp; Rules</h1>

      <h2>About This Game</h2>
      <p>
        This game is based on Boggle. Players compete in real time on the same
        letter board to find valid words before the round ends.
      </p>

      <h2>Core Gameplay</h2>
      <ul>
        <li>A set of letter dice is shuffled randomly at the start of each round.</li>
        <li>The shuffled letters are laid out on the board.</li>
        <li>All players use the same board during the round.</li>
        <li>Players enter words they find from the board.</li>
        <li>When the round ends, the game shows scores and each player’s word list.</li>
      </ul>

      <h2>How to Form Words</h2>
      <ul>
        <li>Words must be formed using letters on the current board.</li>
        <li>Letters must be connected in sequence using adjacent tiles.</li>
        <li>A tile cannot be reused within the same word.</li>
        <li>Submitted words must be valid dictionary words.</li>
        <li>
          The results screen highlights unique words, and scoring is based on
          the game rules used in this version.
        </li>
      </ul>

      <h2>Scoring &amp; Results</h2>
      <ul>
        <li>Valid submitted words earn points.</li>
        <li>The results screen displays everyone’s word lists.</li>
        <li>Unique words are highlighted.</li>
        <li>Final scores are based on valid words submitted during the round.</li>
      </ul>

      <h2>Multiplayer</h2>
      <ul>
        <li>The basic version supports real-time games with other users.</li>
        <li>Players compete on the same board at the same time.</li>
      </ul>

      <h2>Optional / Advanced Features</h2>
      <p>Depending on the version, the game may also include:</p>
      <ul>
        <li>Custom game settings</li>
        <li>User accounts</li>
        <li>User statistics tracking</li>
        <li>Playing against a computer</li>
        <li>Custom board creation for other players</li>
        <li>Different alphabets</li>
        <li>A shared &ldquo;definitive&rdquo; dictionary</li>
      </ul>

      <h2>Notes</h2>
      <p>
        Specific scoring rules, timers, and dictionary settings may vary by game
        mode or version.
      </p>
    </div>
  );
}
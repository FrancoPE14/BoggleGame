import Link from "next/link";

export default function InformationRulesPage() {
  return (
    <div className="min-h-screen bg-zinc-50 text-zinc-900 dark:bg-black dark:text-zinc-100">
      <main className="mx-auto w-full max-w-4xl px-4 py-8">
        <div className="rounded-xl border border-zinc-200 bg-white p-6 shadow-sm dark:border-zinc-800 dark:bg-zinc-900">
          <nav aria-label="Rules page navigation" className="mb-4">
            <Link
              href="/"
              className="inline-block rounded-md border border-zinc-300 bg-zinc-100 px-3 py-2 text-sm font-medium hover:bg-zinc-200 dark:border-zinc-700 dark:bg-zinc-800 dark:hover:bg-zinc-700"
            >
              Back to Menu
            </Link>
          </nav>

          <header className="mb-6">
            <h1 className="text-3xl font-bold">Information &amp; Rules</h1>
            <p className="mt-2 text-sm text-zinc-600 dark:text-zinc-300">
              Learn how to play this online multiplayer Boggle game, how words are
              formed, and how scoring works.
            </p>
          </header>

          <section className="mb-5">
            <h2 className="mb-2 text-xl font-semibold">About This Game</h2>
            <p className="leading-7">
              This game is based on Boggle. Players compete in real time on the same
              letter board to find valid words before the round ends.
            </p>
          </section>

          <section className="mb-5">
            <h2 className="mb-2 text-xl font-semibold">Core Gameplay</h2>
            <ul className="list-disc space-y-1 pl-5 leading-7">
              <li>A set of letter dice is shuffled randomly at the start of each round.</li>
              <li>The shuffled letters are laid out on the board.</li>
              <li>All players use the same board during the round.</li>
              <li>Players enter words they find from the board.</li>
              <li>
                When the round ends, the game shows scores and each player&apos;s word
                list.
              </li>
            </ul>
          </section>

          <section className="mb-5">
            <h2 className="mb-2 text-xl font-semibold">How to Form Words</h2>
            <ul className="list-disc space-y-1 pl-5 leading-7">
              <li>Words must be formed using letters on the current board.</li>
              <li>Letters must be connected in sequence using adjacent tiles.</li>
              <li>A tile cannot be reused within the same word.</li>
              <li>Submitted words must be valid dictionary words.</li>
              <li>
                The results screen highlights unique words, and scoring is based on
                the game rules used in this version.
              </li>
            </ul>
          </section>

          <section className="mb-5">
            <h2 className="mb-2 text-xl font-semibold">Scoring &amp; Results</h2>
            <ul className="list-disc space-y-1 pl-5 leading-7">
              <li>Valid submitted words earn points.</li>
              <li>The results screen displays everyone&apos;s word lists.</li>
              <li>Unique words are highlighted.</li>
              <li>Final scores are based on valid words submitted during the round.</li>
            </ul>
          </section>

          <section className="mb-5">
            <h2 className="mb-2 text-xl font-semibold">Multiplayer</h2>
            <ul className="list-disc space-y-1 pl-5 leading-7">
              <li>The basic version supports real-time games with other users.</li>
              <li>Players compete on the same board at the same time.</li>
            </ul>
          </section>

          <section className="mb-5">
            <h2 className="mb-2 text-xl font-semibold">
              Optional / Advanced Features
            </h2>
            <p className="mb-2 leading-7">
              Depending on the version, the game may also include:
            </p>
            <ul className="list-disc space-y-1 pl-5 leading-7">
              <li>Custom game settings</li>
              <li>User accounts</li>
              <li>User statistics tracking</li>
              <li>Playing against a computer</li>
              <li>Custom board creation for other players</li>
              <li>Different alphabets</li>
              <li>A shared definitive dictionary</li>
            </ul>
          </section>

          <section>
            <h2 className="mb-2 text-xl font-semibold">Notes</h2>
            <p className="leading-7">
              Specific scoring rules, timers, and dictionary settings may vary by game
              mode or version.
            </p>
          </section>
        </div>
      </main>
    </div>
  );
}
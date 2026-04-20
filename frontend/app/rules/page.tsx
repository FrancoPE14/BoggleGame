import Link from "next/link";

export default function InformationRulesPage() {
  return (
    <div className="min-h-screen bg-zinc-50" style={{ color: "#18181b" }}>
      <main className="mx-auto w-full max-w-4xl px-4 py-8">
        <div className="rounded-xl border border-zinc-200 bg-white p-6 shadow-sm">
          <nav aria-label="Rules page navigation" className="mb-4">
            <Link
              href="/"
              className="inline-block rounded-md px-3 py-2 text-sm font-medium"
              style={{ backgroundColor: "#27272a", color: "#ffffff" }}
            >
              Back to Menu
            </Link>
          </nav>

          <header className="mb-6">
            <h1 className="text-3xl font-bold" style={{ color: "#18181b" }}>Information &amp; Rules</h1>
            <p className="mt-2 text-sm" style={{ color: "#52525b" }}>
              Learn how to play this online multiplayer Boggle game, how words are
              formed, and how scoring works.
            </p>
          </header>

          <section className="mb-5">
            <h2 className="mb-2 text-xl font-semibold" style={{ color: "#18181b" }}>About This Game</h2>
            <p className="leading-7" style={{ color: "#3f3f46" }}>
              This game is a web verison of the 1973 Hasbro board game Boggle.
        Players compete in real time on the same letter board to find as many words before the round ends.
            </p>
          </section>

          <section className="mb-5">
            <h2 className="mb-2 text-xl font-semibold" style={{ color: "#18181b" }}>How to play</h2>
            <ul className="list-disc space-y-1 pl-5 leading-7" style={{ color: "#3f3f46" }}>
              <li>A randomized board is created at the start of every game.</li>
              <li>All players use the same board during the round.</li>
              <li>Players find as many words as they can before time runs out.</li>
              <li>When the round ends, the player with the most points wins!</li>
            </ul>
          </section>

          <section className="mb-5">
            <h2 className="mb-2 text-xl font-semibold" style={{ color: "#18181b" }}>How to Form Words</h2>
            <ul className="list-disc space-y-1 pl-5 leading-7" style={{ color: "#3f3f46" }}>
                <li>Click and drag to select letters to make words.</li>
                <li>Letters must be connected in sequence using adjacent tiles.</li>
                <li>Adjacent letters can also be diagonal.</li>
                <li>A letter can only be used once in the same word.</li>
                <li>All words must be at least 3 letter long.</li>
            </ul>
          </section>

          <section className="mb-5">
            <h2 className="mb-2 text-xl font-semibold" style={{ color: "#18181b" }}>Scoring &amp; Results</h2>
            <ul className="list-disc space-y-1 pl-5 leading-7" style={{ color: "#3f3f46" }}>
              <li>Each submitted word earns you points! The longer the word, the more points it earns.</li>
              <li>The same word cannot be scored more than once, even if it appears multiple times on the board.</li>
              <li>Plural forms of words can be used (for example, both &ldquo;badger&ldquo; and &ldquo;badgers&ldquo; would be valid).</li>
              <li>Final scores are based on valid words submitted during the round.</li>
            </ul>
          </section>

          <section className="mb-5">
            <h2 className="mb-2 text-xl font-semibold" style={{ color: "#18181b" }}>Multiplayer</h2>
            <ul className="list-disc space-y-1 pl-5 leading-7" style={{ color: "#3f3f46" }}>
              <li>You can play with other players on the same board!</li>
              <li>To play with friends, select the &ldquo;play online&ldquo; button on the home screen.</li>
            </ul>
          </section>
        </div>
      </main>
    </div>
  );
}

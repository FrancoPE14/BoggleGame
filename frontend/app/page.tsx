import Image from "next/image";
import Link from "next/link";
import BoggleBoard from "./components/boggle-board";
import WordInput from "./components/word-input";

export default function Home() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-zinc-50 font-sans dark:bg-black">
      <main className="flex min-h-screen w-full max-w-3xl mx-auto flex-col items-center justify-center py-32 px-16 bg-white dark:bg-black">
        <Image
          src="/LOGO.png"
          width={100}
          height={100}
          alt="Logo Image"
        ></Image>

        <BoggleBoard />

        <WordInput />

        <div className="mt-6">
          <Link
            href="/rules"
            className="inline-block rounded-md border border-zinc-300 bg-zinc-100 px-4 py-2 text-sm font-medium text-zinc-900 hover:bg-zinc-200 dark:border-zinc-700 dark:bg-zinc-800 dark:text-zinc-100 dark:hover:bg-zinc-700"
          >
            Information &amp; Rules
          </Link>
        </div>
      </main>
    </div>
  );
}
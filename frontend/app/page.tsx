"use client";
import { Button, Image } from "react-bootstrap";

export default function Home() {
  return (
    <div className="flex min-h-screen min-w-screen w-full max-w-3xl mx-auto flex-col items-center justify-center bg-amber-100 dark:bg-amber-100">
      <Image src="/LOGO.png" width={200} height={200} alt="Logo Image"></Image>

      <div>
        <Button
          href="/game"
          variant="outline-dark"
          size="lg"
          className="mt-1 mb-1 ms-1 me-1"
        >
          Play Solo
        </Button>

        <Button
          href="/lobby"
          variant="outline-dark"
          size="lg"
          className="mt-1 mb-1 ms-1 me-1"
        >
          Play Online
        </Button>
      </div>

      <Button
        href="/leaderboard"
        variant="outline-dark"
        size="sm"
        className="mt-1 mb-1"
      >
        Leaderboard
      </Button>

      <Button
        href="/rules"
        variant="outline-dark"
        size="sm"
        className="inline-block rounded-md mt-1 mb-1"
      >
        Information & Rules
      </Button>
    </div>
  );
}

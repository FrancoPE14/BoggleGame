"use client";

import LetterButton from "./letter-button";

const BOARD_SIZE = 5;

const letters = [
  "T",
  "E",
  "S",
  "T",
  "S", // 1
  "W",
  "O",
  "R",
  "D",
  "S", // 2
  "G",
  "A",
  "M",
  "E",
  "S", // 3
  "P",
  "L",
  "A",
  "Y",
  "S", // 4
  "B",
  "O",
  "G",
  "G",
  "L", // 5
];

export default function BoggleBoard() {
  return (
    <div
      className="
        grid gap-2
        w-[90vw] max-w-105
        aspect-square
        justify-content: center
      "
      style={{
        gridTemplateColumns: `repeat(${BOARD_SIZE}, minmax(0, 1fr))`,
      }}
    >
      {letters.map((letter, index) => (
        <div key={index} className="aspect-square">
          <LetterButton letter={letter} />
        </div>
      ))}
    </div>
  );
}

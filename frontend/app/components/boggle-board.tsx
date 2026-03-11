"use client";

import LetterButton from "./letter-button";
import React, { useState, useRef } from "react";
import { SubmittedWord } from "./use-word-verification";

const BOARD_SIZE = 5;

const board = [
  ["T", "E", "S", "T", "S"], // 1
  ["W", "O", "R", "D", "S"], // 2
  ["G", "A", "M", "E", "S"], // 3
  ["P", "L", "A", "Y", "S"], // 4
  ["B", "O", "G", "G", "L"], // 5
];

type BoggleBoardProps = {
  submittedWords: SubmittedWord[];
  verifyWord: (word: string) => Promise<boolean>;
};

export default function BoggleBoard({
  submittedWords,
  verifyWord,
}: BoggleBoardProps) {
  const [inputWord, setInputWord] = useState("");
  const [highlightedTiles, setHighlightedTiles] = useState<Set<string>>(
    new Set(),
  );

  const currentPositionRef = useRef<number[]>([-1, -1]);
  const currentLetterRef = useRef("");
  const visitedRef = useRef(new Set<string>());
  const mouseDownRef = useRef(false);
  const wordRef = useRef("");

  const handleMouseDown = (e: React.MouseEvent) => {
    e.preventDefault();
    mouseDownRef.current = true;
    addCurrentLetter();
  };

  /**
   * When mouse up, submit potential input string to verify word.
   * Reset values like input string and visited list.
   * @param e React.MouseEvent
   */
  const handleMouseUp = (e: React.MouseEvent) => {
    e.preventDefault();

    const formedWord = wordRef.current;

    if (
      formedWord.length >= 3 &&
      !submittedWords.some((w) => w.word === formedWord)
    ) {
      verifyWord(formedWord);
    }

    currentPositionRef.current = [-1, -1];
    visitedRef.current.clear();
    mouseDownRef.current = false;
    setInputWord("");
    wordRef.current = "";
    setHighlightedTiles(new Set());
  };

  /**
   * Updates position and letter mouse is currently hovering over.
   * If the new position is not adjacent to last selected letter, then return.
   * If mouse is being held down, then add the letter to the input string.
   * @param row the row of the hovered letter
   * @param col the column of the hovered letter
   * @param letter the string value (A - Z) of the hovered letter-button component
   * @returns
   */
  const setCurrentButton = (row: number, col: number, letter: string) => {
    currentLetterRef.current = letter;
    const pos = currentPositionRef.current;
    const key = `${row},${col}`;

    if (visitedRef.current.has(key)) return;
    if (pos[0] === -1 && pos[1] === -1) {
      currentPositionRef.current = [row, col];
    } else if (row > pos[0] + 1 || row < pos[0] - 1) {
      return;
    } else if (col > pos[1] + 1 || col < pos[1] - 1) {
      return;
    } else {
      currentPositionRef.current = [row, col];
    }

    if (!mouseDownRef.current) return;

    addCurrentLetter();
  };

  /**
   * Adds current letter to input string.
   * Highlights letter-button component
   * @returns
   */
  const addCurrentLetter = () => {
    if (!mouseDownRef.current) return;

    const pos = currentPositionRef.current;
    const row = pos[0];
    const col = pos[1];
    const key = `${row},${col}`;

    if (visitedRef.current.has(key)) return;

    visitedRef.current.add(key);
    const letter = currentLetterRef.current;
    wordRef.current = wordRef.current + letter;
    setInputWord((prev) => prev + letter);
    setHighlightedTiles(new Set(visitedRef.current));
  };

  return (
    <div
      className="justify-content: center"
      onMouseDown={handleMouseDown}
      onMouseUp={handleMouseUp}
    >
      <div
        style={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
        }}
      >
        <h3>{inputWord === "" ? "Make a word!" : inputWord}</h3>
      </div>

      <div
        className="
        grid gap-2
        w-[90vw] max-w-105
        aspect-square
      "
        style={{
          gridTemplateColumns: `repeat(${BOARD_SIZE}, minmax(0, 1fr))`,
        }}
      >
        {board.map((rows, row) =>
          rows.map((letter, col) => (
            <LetterButton
              key={`${row}-${col}`}
              letter={letter}
              position={[row, col]}
              setCurrentButton={(r, c, l) => setCurrentButton(r, c, l)}
              isHighlighted={highlightedTiles.has(`${row},${col}`)}
            />
          )),
        )}
      </div>
    </div>
  );
}

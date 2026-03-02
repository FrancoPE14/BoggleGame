"use client";
import LetterButton from "./letter-button";
import React, { useState } from "react";
const BOARD_SIZE = 5;

const board = [
  ["T", "E", "S", "T", "S"], // 1
  ["W", "O", "R", "D", "S"], // 2
  ["G", "A", "M", "E", "S"], // 3
  ["P", "L", "A", "Y", "S"], // 4
  ["B", "O", "G", "G", "L"], // 5
];

let currentPosition = [-1, -1];
let currentLetter = "";
const visited = new Set<string>();
let mouseDown = false;

export default function BoggleBoard() {
  const [inputWord, setInputWord] = useState(""); // The string player is creating with board

  // Start counting char
  const handleMouseDown = (e: React.MouseEvent) => {
    e.preventDefault();
    mouseDown = true;
    addCurrentletter();
  };

  // Verify current input word and reset
  const handleMouseUp = (e: React.MouseEvent) => {
    e.preventDefault();

    // Clear curr pos and visited set
    currentPosition[0] = -1;
    currentPosition[1] = -1;
    visited.clear();

    // reset word input
    setInputWord("");

    mouseDown = false;
  };

  // Set the current button to the one mouse is over last
  const setCurrentButton = (row: number, col: number, letter: string) => {
    currentLetter = letter;
    if (currentPosition[0] === -1 && currentPosition[1] === -1) {
      currentPosition[0] = row;
      currentPosition[1] = col;
    }
    // not adjacent row
    else if (row > currentPosition[0] + 1 || row < currentPosition[0] - 1) {
      return;
    }
    // not adjacent col
    else if (col > currentPosition[1] + 1 || col < currentPosition[1] - 1) {
      return;
    }
    // valid position
    else {
      currentPosition[0] = row;
      currentPosition[1] = col;
    }
    console.log(
      "Letter: " +
        currentLetter +
        " Pos: " +
        currentPosition[0] +
        ", " +
        currentPosition[1],
    );
    if (!mouseDown) return;

    addCurrentletter();
  };

  // Add letter to input string
  const addCurrentletter = () => {
    if (!mouseDown) return;

    const row = currentPosition[0];
    const col = currentPosition[1];
    const key = `${row},${col}`;

    if (visited.has(key)) return;

    if (currentPosition[0] === -1 && currentPosition[1] === -1) {
      currentPosition[0] = row;
      currentPosition[1] = col;
      visited.add(key);
      setInputWord((prev) => prev + currentLetter);
      return;
    }

    currentPosition[0] = row;
    currentPosition[1] = col;
    visited.add(key);
    setInputWord((prev) => prev + currentLetter);
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
              key={row.toString() + " " + col.toString()}
              letter={letter}
              position={[row, col]}
              setCurrentButton={(r, c, l) => setCurrentButton(r, c, l)}
            />
          )),
        )}
      </div>
    </div>
  );
}

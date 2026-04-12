"use client";

type LetterButtonProps = {
  letter: string;
  position: [number, number];
  setCurrentButton: (r: number, c: number, l: string) => void;
  isHighlighted: boolean;
};

export default function LetterButton({
  letter,
  position,
  setCurrentButton,
  isHighlighted,
}: LetterButtonProps) {
  const handleMouseEnter = () => {
    setCurrentButton(position[0], position[1], letter);
  };

  const handleMouseLeave = () => {
    setCurrentButton(-1, -1, "");
  };

  return (
    <button
      onClick={handleMouseEnter}
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
      className={`
        inline-flex items-center justify-center
        rounded-lg w-full h-full
        text-xl font-bold select-none
        transition-all duration-150
        ${
          isHighlighted
            ? "bg-amber-400 text-white ring-2 ring-amber-500 scale-105 shadow-md"
            : "bg-amber-200 text-gray-700 hover:bg-amber-100"
        }
      `}
    >
      {letter}
    </button>
  );
}

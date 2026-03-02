"use client";

type LetterButtonProps = {
  letter: string;
  position: [number, number];
  setCurrentButton: (r: number, c: number, l: string) => void;
};

export default function LetterButton({
  letter,
  position,
  setCurrentButton,
}: LetterButtonProps) {
  const handleMouseEnter = () => {
    setCurrentButton(position[0], position[1], letter);
  };

  return (
    <button
      onClick={handleMouseEnter}
      onMouseEnter={handleMouseEnter}
      className="
        inline-flex items-center justify-center
        rounded-lg
        w-full h-full
        text-xl font-bold
        text-gray-700
        bg-amber-200
        hover:bg-amber-100
        select-none
        transition-colors duration-200
      "
    >
      {letter}
    </button>
  );
}

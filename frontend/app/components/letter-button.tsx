"use client";

type LetterButtonProps = {
  letter: string;
};

export default function LetterButton({ letter }: LetterButtonProps) {
  const handleClick = () => {
    console.log("Clicked:", letter);
  };

  return (
    <button
      onClick={handleClick}
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

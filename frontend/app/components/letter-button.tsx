"use client";

export default function LetterButton() {
  const handleClick = () => {
    console.log("Clicked");
  };

  return (
    <button
      onClick={handleClick}
      className="
        inline-flex items-center justify-center
        rounded-lg px-5 py-2.5
        text-sm font-semibold
        text-white
        bg-blue-600
        hover:bg-blue-700
        focus:outline-none focus:ring-2 focus:ring-blue-400 focus:ring-offset-2
        disabled:bg-blue-400 disabled:cursor-not-allowed
        transition-colors duration-200
      "
    >
      Click me
    </button>
  );
}

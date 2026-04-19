import { useState } from "react";

export default function useMultiplayerWordSubmission(
  sessionId: number,
  username: string
) {
  const [input, setInput] = useState("");
  const [score, setScore] = useState(0);
  const [words, setWords] = useState<string[]>([]);

  const submitWord = async () => {
    if (!input) return;

    const res = await fetch(
      `http://163.192.206.210:8080/api/submit-word?sessionId=${sessionId}&username=${username}&word=${input}`,
      { method: "POST" }
    );

    const data = await res.json();

    setScore(data.currentScore);
    setWords(data.acceptedWords);
    setInput("");
  };

  return {
    input,
    setInput,
    submitWord,
    score,
    words,
  };
}
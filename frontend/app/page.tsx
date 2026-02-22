import Image from "next/image";
import BoggleBoard from "./components/boggle-board";

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
      </main>
    </div>
  );
}

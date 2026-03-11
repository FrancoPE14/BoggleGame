import { createContext } from "react";
import type { Dispatch, SetStateAction } from "react";

//Dispatch is just what allows us to specify a function to change the boolean
//We can specify a useState with this
type LoginStatusContextType = [boolean, Dispatch<SetStateAction<boolean>>];

const LoginStatusContext = createContext<LoginStatusContextType>([
  false,
  () => undefined,
]);

export default LoginStatusContext;

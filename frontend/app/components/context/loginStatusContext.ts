import { createContext } from "react";
import type { Dispatch, SetStateAction } from "react";

//useState context
type LoginStatusContextType = [boolean, Dispatch<SetStateAction<boolean>>];

//Create context
const LoginStatusContext = createContext<LoginStatusContextType>([
  false,
  () => undefined,
]);

export default LoginStatusContext;

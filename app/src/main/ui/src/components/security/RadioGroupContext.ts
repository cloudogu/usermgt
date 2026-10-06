import {createContext, useContext} from "react";

export const RadioGroupContext = createContext<string | undefined>(undefined);

export const useRadioGroupName = () => useContext(RadioGroupContext);

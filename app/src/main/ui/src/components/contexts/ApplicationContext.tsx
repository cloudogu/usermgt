import { createContext, useContext } from "react";
import type { CasUser } from "../../services/CasUser";

export type ApplicationContextProps = {
    casUser: CasUser;
}
export const ApplicationContext = createContext<ApplicationContextProps>({
    casUser: {
        principal: "default",
        admin: false,
        loading: true
    },
});

export function useApplicationContext() {
    const context = useContext(ApplicationContext);

    if(!context){
        throw new Error("no ApplicationContext provided for useApplicationContext");
    }

    return context;
}
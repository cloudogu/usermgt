import { createContext, useContext } from "react";
import type { CasUser } from "../../services/CasUser";

const isExternalLdap = process.env.EXTERNAL_LDAP || "false";


export type ApplicationContextProps = {
    casUser: CasUser;
    externalLdap: boolean;
}
export const ApplicationContext = createContext<ApplicationContextProps>({
    casUser: {
        principal: "default",
        admin: false,
        loading: true
    },
    externalLdap: isExternalLdap === "true",
});

export function useApplicationContext() {
    const context = useContext(ApplicationContext);

    if(!context){
        throw new Error("no ApplicationContext provided for useApplicationContext");
    }

    return context;
}
